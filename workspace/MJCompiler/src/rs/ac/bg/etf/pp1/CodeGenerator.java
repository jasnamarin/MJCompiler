package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {

	private int ind = 0;
	private int mainPc;
	
	public int getMainPc() {
		return mainPc;
	}

//---------------------------------------program--------------------------------------------------
	public void visit(ProgName progName) {
		// chr, ord
		Tab.find("chr").setAdr(0);
		Tab.find("ord").setAdr(0);

		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);

		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);

		// len
		Tab.find("len").setAdr(Code.pc);

		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);

		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

//--------------------------------------functions-------------------------------------------------
//--definitions

	public void visit(MethodTypeName methodTypeName) {
		if("main".equalsIgnoreCase(methodTypeName.getMethodName())){
			Code.mainPc = mainPc = Code.pc;
		}

		methodTypeName.obj.setAdr(Code.pc);

		Code.put(Code.enter);
		Code.put(methodTypeName.obj.getLevel()); // psize (broj parametara)
		Code.put(methodTypeName.obj.getLocalSymbols().size()); // lsize (+ lokalnih promenljivih)
	}

	public void visit(MethodDeclNoPars methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(MethodDeclPars methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

//--calls--statements

	public void visit(DesignatorStmt designatorStmt) {
		DesigOp desigOp = designatorStmt.getDesigOp();
		Obj desigObj = designatorStmt.getDesignator().obj;
		if(desigOp instanceof DesigActParams || desigOp instanceof DesigNoParams) {
			callFunction(designatorStmt.getDesignator());
			// todo: check if need to add special case for void (Code.pop?)
		}
		if(desigOp instanceof DesigAssign) {
			Code.store(desigObj);
		}
		if(desigOp instanceof DesigInc) {
			Code.load(desigObj);
			Code.put(Code.const_1);
			Code.put(Code.add);
			Code.store(desigObj);
		}
		if(desigOp instanceof DesigDec) {
			Code.load(desigObj);
			Code.put(Code.const_1);
			Code.put(Code.sub);
			Code.store(desigObj);
		}
	}

	public void visit(FactorDesignatorParams factor) {
		callFunction(((FactorDesignatorParams)factor).getDesignator());
	}

	public void visit(FactorDesignatorCall factor) {
		callFunction(((FactorDesignatorCall)factor).getDesignator());
	}

	public void callFunction(Designator designator) {
		int address = designator.obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put(address);
	}

//--------------------------------------statements-------------------------------------------------

	public void visit(StatRead statement) {
		Code.put(statement.getDesignator().obj.getType().equals(Tab.charType) ? Code.bread : Code.read);
		Code.store(statement.getDesignator().obj);
	}

	public void visit(StatPrint statement) {
		PrintVal printVal = statement.getPrintVal();
		if(printVal instanceof PrintExpr) {
			if(((PrintExpr) printVal).getExpr().struct == Tab.charType) {
				Code.put(Code.const_1);
				Code.put(Code.bprint);
			}
			else {
				Code.put(Code.const_4);
				Code.put(Code.print);
			}
		}
		else {
			Code.loadConst(((PrintWithConst) printVal).getNumConstVal());
			if(((PrintWithConst) printVal).getExpr().struct == Tab.charType) {
				Code.put(Code.bprint);
			}
			else {
				Code.put(Code.print);
			}
		}
	}

	public void visit(StatReturn statement) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(StatReturnVoid statement) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

//-------------------------------------expressions------------------------------------------------

	public void visit(MultiTermExpr addExpr) {
		Code.put(addExpr.getAddop() instanceof Add ? Code.add : Code.sub);
	}

	public void visit(NegTermExpr negExpr) {
		Code.put(Code.neg);
	}

	public void visit(MultiTerm multiTerm) {
		Code.put(multiTerm.getMulop() instanceof Mul ? Code.mul :
				(multiTerm.getMulop() instanceof Div ? Code.div : Code.rem));
	}

	public void visit(FactorNewTypeExpr factor) {
		Code.put(Code.newarray);
		Code.put(factor.getType().struct.equals(Tab.charType) ? 0 : 1);
	}

	public void visit(FactorNum factor) {
		Code.load(new Obj(Obj.Con, "", Tab.intType, factor.getNumVal(), 0));
	}

	public void visit(FactorCh factor) {
		Code.load(new Obj(Obj.Con, "", Tab.charType, factor.getChVal(), 0));
	}

	public void visit(FactorBool factor) {
		Code.load(new Obj(Obj.Con, "", SemanticAnalyzer.boolType, factor.getBoolVal() == "true" ? 1 : 0, 0));
	}

//---------------------------------------variables--------------------------------------------------
//--single
	public void visit(DesignatorIdent designator) {
		if(designator.getParent() instanceof StatForeach
		|| designator.getParent() instanceof FactorDesignator || designator.getParent() instanceof DesignatorExpr) {
			Code.load(designator.obj);
		}
	}

//--arrays
	public void visit(DesignatorExpr designator) {
		if(designator.getParent() instanceof StatForeach || designator.getParent() instanceof FactorDesignator
				|| designator.getParent() instanceof DesignatorExpr || designator.getParent() instanceof DesigInc
				|| designator.getParent() instanceof DesigDec) {
			Code.load(designator.obj);
		}
	}

//--array-assignment
	public void visit(Comma comma) {
		ind++;
	}

	public void visit(SingleDesig desig) {
		assignArrayVal(desig.getDesignator().obj, desig);
	}

	public void visit(DesigListLong desigList) {
		assignArrayVal(desigList.getDesignator().obj, desigList);
	}

	public void assignArrayVal(Obj desigObj, DesigList parentSubtree) {
		while(parentSubtree.getParent() instanceof DesigList) {
			parentSubtree = (DesigList) parentSubtree.getParent();
		}
		DesignatorAssignment designatorAssignment = (DesignatorAssignment) parentSubtree.getParent();
		Designator array = designatorAssignment.getDesignator();

		Code.load(array.obj);
		Code.loadConst(ind);
		Code.put(Code.aload);

		Code.loadConst(ind);
		Code.load(array.obj);
		Code.put(Code.arraylength);
		Code.putFalseJump(Code.ge, Code.pc + 5);

		Code.put(Code.trap);
		Code.put(1);

		Code.store(desigObj);
	}

	public void visit(DesignatorAssignment designatorAssignment) {
		ind = 0;
	}

}