package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

import java.util.*;
import rs.ac.bg.etf.pp1.MJCompilerError.CompilerErrorType;

public class SemanticAnalyzer extends VisitorAdaptor {

	// iz project template-a
	int printCallCount = 0;
	int varDeclCount = 0;

	// moje
	static Struct boolType = new Struct(Struct.Bool);

	int nVars = 0;
	Obj currentMethod = null;
	Type currentType = null;

	int elemAccessCount = 0;
	int globalVarCount = 0; // declarations including arrays
	int localVarCount = 0;  // declarations including arrays
	int recordDefCount = 0;
	int functionCallCount = 0;

	Logger log = Logger.getLogger(getClass());

	boolean errorDetected = false;
	public List<MJCompilerError> semanticErrors = new ArrayList<MJCompilerError>();


//--------------------------------------error-reporting------------------------------------------------


	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" in line ").append(line);
		log.error(msg.toString());
		semanticErrors.add(new MJCompilerError(line, CompilerErrorType.SEMANTIC_ERR, message));
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" in line ").append(line);
		log.info(msg.toString());
	}

	public boolean passed() {
		return !errorDetected;
	}


//-----------------------------------------program---------------------------------------------------


	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgramName(), Tab.noType);
		Tab.insert(Obj.Type, "bool", boolType);
		Tab.openScope();
	}

	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();

		Obj tmpObj = Tab.find("main");
		if(tmpObj.getKind() != Obj.Meth)
			report_error("Error - main function missing!", null );

		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}


//-------------------------------------function-call------------------------------------------------


	public void visit(FuncCall functionCall) {
		functionCallCount++;
		report_info("Detected function call", functionCall);
	}


//-----------------------------------------method---------------------------------------------------


	public void visit(MethodTypeName methodTypeName) {
		if (methodTypeName.getReturnType() instanceof NonEmptyReturnType) {
			NonEmptyReturnType type = (NonEmptyReturnType) methodTypeName.getReturnType();
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethodName(), type.getType().struct);
		}
		else {
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethodName(), Tab.noType);
		}

		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Detected method " + methodTypeName.getMethodName(), methodTypeName);
	}

	public void visit(MethodDecl methodDecl) {
		if(currentMethod != null)
			Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();

		currentMethod = null;
	}


//------------------------------------------type----------------------------------------------------


	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getTypeName());

		currentType = new Type(type.getTypeName());
		if(typeObj == Tab.noObj) {
			report_error("Error - Type " + type.getTypeName() + " not found in Symbol Table! ", type);
			type.struct = Tab.noType;
			currentType.struct = Tab.noType;
		}
		else {
			if(typeObj.getKind() == Obj.Type){
				type.struct = typeObj.getType();
				currentType.struct = type.struct;
			}
			else {
				report_error("Error - " + type.getTypeName() + " is not a valid type!", type);
				type.struct = Tab.noType;
				currentType.struct = type.struct;
			}
		}
	}


//---------------------------------------array-element---------------------------------------------


	public void visit(DesigExpr arrIndexExpr) {
		elemAccessCount++;
		report_info("Detected array element access", arrIndexExpr);
	}


//---------------------------------------variable---------------------------------------------------


	public void visit(SingleVar var) {
		Obj varObj = Tab.insert(Obj.Var, var.getVarName(), currentType.struct);
		if(varObj.getLevel() == 0) {
			globalVarCount++;
			report_info("Detected global variable declaration " + var.getVarName(), var);
		}
		else {
			localVarCount++;
			report_info("Detected local variable declaration " + var.getVarName(), var);
		}
	}

	public void visit(ArrayVar var) {
		Obj varObj = Tab.insert(Obj.Var, var.getVarName(), new Struct(Struct.Array, currentType.struct));
		if(varObj.getLevel() == 0) {
			globalVarCount++;
			report_info("Detected global array declaration " + var.getVarName(), var);
		}
		else {
			localVarCount++;
			report_info("Detected local array declaration " + var.getVarName(), var);
		}
	}

	public void visit(VarDecl varDecl) { // doesn't work
		varDeclCount++;
	}

	public void visit(PrintStm printStm) {
		printCallCount++;
	}


}
