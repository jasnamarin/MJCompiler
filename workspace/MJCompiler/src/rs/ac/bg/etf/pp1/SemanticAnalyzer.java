package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

import java.util.*;
import rs.ac.bg.etf.pp1.MJCompilerError.CompilerErrorType;

public class SemanticAnalyzer extends VisitorAdaptor {
/* Obilazi one cvorove apstraktnog sintaksnog stabla
* (AST) koji su relevantni za semanticku analizu.
* */

	static Struct boolType = new Struct(Struct.Bool); // Posto bool ne postoji vec u symtable.

	private int nVars = 0;

	public int nVars() {
		return nVars;
	}

	private Obj currentMethod = Tab.noObj;
	private int currentMethodFormParCnt = 0;
	private Struct declaredType = Tab.noType; // Za proveru tipa kod const, var, return-a.
	private List<Struct> actualParametersList = new ArrayList<>();
	private int withinLoop = 0;


	private int elemAccessCount = 0;
	public int elemAccessCount() {
		return elemAccessCount;
	}
	private int symbolicConstCount = 0;
	public int symbolicConstCount() {
		return symbolicConstCount;
	}
	private int globalVarCount = 0; // Deklaracije, ukljucujuci i nizove.
	public int globalVarCount() {
		return globalVarCount;
	}
	private int localVarCount = 0;  // Deklaracije, ukljucujuci i nizove.
	public int localVarCount() {
		return localVarCount;
	}
	private int globalFunctionCallCount = 0; // Poziv funkcije je u gramatici Designator LPAREN ActPars RPAREN.
	public int globalFunctionCallCount() {
		return globalFunctionCallCount;
	}
	private int formParamUsageCount = 0; // Koriscenje formalnog argumenta f-je.
	public int formParamUsageCount() {
		return formParamUsageCount;
	}
	Logger log = Logger.getLogger(getClass());

	boolean errorDetected = false;
	public List<MJCompilerError> semanticErrors = new ArrayList<>();

//--------------------------------------error-reporting------------------------------------------------

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" Linija: ").append(line);
		log.error(msg.toString());
		semanticErrors.add(new MJCompilerError(line, CompilerErrorType.SEMANTIC_ERR, message));
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" Linija: ").append(line);
		log.info(msg.toString());
	}

	public boolean passed() {
		return !errorDetected;
	}

//-----------------------------------------program---------------------------------------------------

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgramName(), Tab.noType);
		Tab.insert(Obj.Type, "bool", boolType); // TODO: test bool type.
		Tab.openScope();
	}

	public void visit(Program program) {
		/* Obilazi se na samom kraju, nakon sto je detektovano sve unutar programa.
		* */
		nVars = Tab.currentScope.getnVars(); // == Code.dataSize

		Tab.chainLocalSymbols(program.getProgName().obj); // Sve objekte iz scope-a uvezuje kao locals program-a.
		Tab.closeScope();

		Obj tmpObj = Tab.find("main");
		if(tmpObj.getKind() != Obj.Meth)
			report_error("Greska - main funkija nije pronadjena!", null );
	}

//----------------------------------------constant--------------------------------------------------

	public void visit(ConstSingle symbolicConst) {
		String identifier = symbolicConst.getConstName();
		if(Tab.currentScope.findSymbol(identifier) != null) {
			report_error("Greska - ime " + identifier + " je vec deklarisano u istom opsegu vazenja!", symbolicConst);
			return;
		}

		if(declaredType.getKind() != Struct.Bool && declaredType.getKind() != Struct.Char && declaredType.getKind() != Struct.Int) {
			report_error("Greska - deklarisana konstanta nedozvoljenog tipa!", symbolicConst);
			return;
		}

		ConstVal constVal = symbolicConst.getConstVal();

		if(constVal instanceof NumVal) {
			if(!(declaredType.equals(Tab.intType))) {
				report_error("Greska - tip u deklaraciji konstante i stvarni tip vrednosti konstante "
						+ "nisu ekvivalentni!", symbolicConst);
				return;
			}
		}
		if(constVal instanceof ChVal) {
			if(!(declaredType.equals(Tab.charType))) {
				report_error("Greska - tip u deklaraciji konstante i stvarni tip vrednosti konstante "
						+ "nisu ekvivalentni!", symbolicConst);
				return;
			}
		}
		if(constVal instanceof BoolVal) {
			if(!(declaredType.equals(boolType))) {
				report_error("Greska - tip u deklaraciji konstante i stvarni tip vrednosti konstante "
						+ "nisu ekvivalentni!", symbolicConst);
				return;
			}
		}

		int value = constVal instanceof NumVal ? ((NumVal)constVal).getNumVal() : constVal instanceof ChVal ?
				((ChVal)constVal).getChVal() : ((BoolVal)constVal).getBoolVal() == "true" ? 1 : 0;
		Obj constObj = Tab.insert(Obj.Con, identifier, declaredType);
		constObj.setAdr(value);

		symbolicConstCount++;
		report_info("Detektovana deklaracija konstante " + identifier + " sa vrednoscu " + constVal, symbolicConst);
	}

//---------------------------------------variable---------------------------------------------------

	public void visit(SingleVar var) {
		String identifier = var.getVarName();
		if(Tab.currentScope.findSymbol(identifier) != null) {
			report_error("Greska - ime " + identifier + " je vec deklarisano u istom opsegu vazenja!", var);
			return;
		}

		Obj varObj = Tab.insert(Obj.Var, identifier, declaredType);
		if(varObj.getLevel() == 0) {
			globalVarCount++;
			report_info("Detektovana deklaracija globalne promenljive " + identifier, var);
		}
		else {
			localVarCount++;
			report_info("Detektovana deklaracija lokalne promenljive " + identifier, var);
		}
	}

	public void visit(ArrayVar var) {
		String identifier = var.getVarName();
		if(Tab.currentScope.findSymbol(identifier) != null) {
			report_error("Greska - ime " + identifier + " je vec deklarisano u istom opsegu vazenja!", var);
			return;
		}

		Obj varObj = Tab.insert(Obj.Var, identifier, new Struct(Struct.Array, declaredType));
		if(varObj.getLevel() == 0) {
			globalVarCount++;
			report_info("Detektovana globalna deklaracija niza " + identifier, var);
		}
		else {
			localVarCount++;
			report_info("Detektovana lokalna deklaracija niza " + identifier, var);
		}
	}

//-----------------------------------------method---------------------------------------------------

	public void visit(MethodTypeName methodTypeName) {
		if (methodTypeName.getReturnType() instanceof NonEmptyReturnType) {
			NonEmptyReturnType type = (NonEmptyReturnType) methodTypeName.getReturnType();
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethodName(), type.getType().struct);
			currentMethod.setLevel(0);
		}
		else {
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethodName(), Tab.noType);
			currentMethod.setLevel(0);
		}

		methodTypeName.obj = currentMethod;
		Tab.openScope();
	}

	public void visit(MethodDecl methodDecl) {
		if(currentMethod != Tab.noObj) {
			Tab.chainLocalSymbols(currentMethod);
		}
		Tab.closeScope();

		currentMethod = Tab.noObj;
	}

	public void visit(VoidReturnType voidType) {
		declaredType = Tab.noType;
	}

	public void visit(NonEmptyReturnType returnType) {
		declaredType = returnType.getType().struct;
	}

	public void visit(FormParSingle formPar) {
		Obj varObj = Tab.insert(Obj.Var, formPar.getParameterName(), declaredType);
		++currentMethodFormParCnt;
	}

	public void visit(FormParSingleArr formParArr) {
		Obj arrObj = Tab.insert(Obj.Var, formParArr.getParameterName(), new Struct(Struct.Array, declaredType));
		++currentMethodFormParCnt;
	}

	public void visit(FormParsMulti formPars) {
		Obj varObj = Tab.insert(Obj.Var, formPars.getParameterName(), declaredType);
		++currentMethodFormParCnt;
	}

	public void visit(FormParsArrMulti formPars) {
		Obj arrObj = Tab.insert(Obj.Var, formPars.getParameterName(), new Struct(Struct.Array, declaredType));
		++currentMethodFormParCnt;
	}

	public void visit(VarsDeclList varDeclList) {
		if(currentMethodFormParCnt > 0)
			currentMethod.setLevel(currentMethodFormParCnt);
		currentMethodFormParCnt = 0;
	}

	public void visit(EmptyVarsList varDeclList) {
		if(currentMethodFormParCnt > 0)
			currentMethod.setLevel(currentMethodFormParCnt);
		currentMethodFormParCnt = 0;
	}

//------------------------------------------type----------------------------------------------------

	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getTypeName());

		if(typeObj == Tab.noObj) {
			report_error("Greska - Tip " + type.getTypeName() + " nije pronadjen u tabeli simbola! ", type);
			type.struct = declaredType = Tab.noType;
		}
		else {
			if(typeObj.getKind() == Obj.Type){
				type.struct = declaredType = typeObj.getType(); // Takodje struct.
			}
			else {
				report_error("Greska - " + type.getTypeName() + " nije validan tip!", type);
				type.struct = declaredType = Tab.noType;
			}
		}
	}

//-----------------------------------designator-statement---------------------------------------------

	public void visit(DesignatorStmt designatorStatement) {
		Designator designator = designatorStatement.getDesignator();
		DesigOp desigOp = designatorStatement.getDesigOp();

		if(desigOp instanceof DesigAssign) {
			if(designator.obj.getKind() != Obj.Var && designator.obj.getKind() != Obj.Elem
					&& designator.obj.getKind() != Obj.Fld) {
				report_error("Greska - designator mora da oznacava promenljivu, element niza, "
						+" ili polje unutar objekta!", designatorStatement);
				return;
			}

			Struct designatorType = designator.obj.getType();
			Struct exprType =  ((DesigAssign)desigOp).getExpr().struct;
			if(!exprType.assignableTo(designatorType)) {
				report_error("Greska - Tipovi src i dst izraza moraju biti kompatibilan pri dodeli!",
						designatorStatement);
				return;
			}
		}

		if(desigOp instanceof DesigInc || desigOp instanceof DesigDec) {
			if(designator.obj.getKind() != Obj.Var && designator.obj.getKind() != Obj.Elem
					&& designator.obj.getKind() != Obj.Fld) {
				report_error("Greska - designator mora da oznacava promenljivu, element niza, "
						+" ili polje unutar objekta!", designatorStatement);
				return;
			}

			if(designator.obj.getType() != Tab.intType) {
				report_error("Greska - operacije ++ i -- su dozvoljene samo za tip int!"
						, designatorStatement);
				return;
			}
		}

		if(desigOp instanceof DesigActParams || desigOp instanceof DesigNoParams) {
			if(designator.obj.getKind() != Obj.Meth) {
				report_error("Greska - pokusaj poziva necega sto nije globalna funkcija glavnog programa!",
						designatorStatement);
				return;
			}

			if(designator.obj.getLevel() != actualParametersList.size()) {
				report_error("Greska - broj formalnih i stvarnih parametara funkcije nije isti!", designatorStatement);
				return;
			}

			for(Obj formalParameter : designator.obj.getLocalSymbols()) {
				if(!(actualParametersList.get(formalParameter.getAdr()).assignableTo(formalParameter.getType()))) {
					report_error("Greska - tip stvarnog argumenta mora da bude kompatibilan pri dodeli"
							+ " sa tipom formalnog argumenta na odgovarajucoj poziciji!", designatorStatement);
					return;
				}
			}
			actualParametersList.clear();

			globalFunctionCallCount++;
			report_info("Detektovan poziv globalne funkcije programa " + designator.obj.getName(),
					designatorStatement);
		}
	}

	public void visit(DesignatorAssignment designatorAssignment) {
		DesigList left = designatorAssignment.getDesigList();
		Designator right = designatorAssignment.getDesignator();
		Struct desigType = right.obj.getType();
		int desigCount = 0;

		if (right.obj.getType().getKind() != Struct.Array) {
			report_error("Greska - desna strana znaka dodele mora da bude niz!", designatorAssignment);
			return;
		}

		while (left != null) {
			if (left instanceof EmptyDesigList) {
				left = null;
			}
			else if (left instanceof DesigListComma) {
				left = ((DesigListComma)left).getDesigList();
			}
			else {
				Obj designatorObject;
				if(left instanceof SingleDesig) {
					designatorObject = ((SingleDesig)left).getDesignator().obj;
				}
				else {
					designatorObject = ((DesigListLong)left).getDesignator().obj;
				}
				if(!(desigType.getElemType().assignableTo(designatorObject.getType()))) {
					report_error("Greska - Elementi niza sa desne strane znaka dodele moraju da budu kompatibilni "
							+"pri dodeli sa svim promenljivim sa leve strane znaka dodele!", designatorAssignment);
					return;
				}
				desigCount++;

				if (left instanceof SingleDesig) left = null;
				else left = ((DesigListLong) left).getDesigList();
			}
		}
	}

	public void visit(SingleDesig designator) {
		int kind = designator.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem && kind != Obj.Fld) {
			report_error("Greska - svaki element sa leve strane znaka dodele mora da oznacava promenljivu, "
					+" element niza, ili polje unutar objekta!", designator);
		}
	}

	public void visit(DesignatorExpr designatorExpr) {
		if(designatorExpr.getDesignator().obj.getType().getKind() != Struct.Array) {
			report_error("Greska - tip neterminala Designator mora da bude niz!", designatorExpr);
			return;
		}
		if(designatorExpr.getExpr().struct.getKind() != Struct.Int) {
			report_error("Greska - tip izraza mora da bude int!", designatorExpr);
			return;
		}
		Obj desigObj = designatorExpr.getDesignator().obj;
		designatorExpr.obj = new Obj(Obj.Elem, desigObj.getName(), desigObj.getType().getElemType());
	}

	public void visit(DesignatorIdent designatorIdent) {
		Obj obj = Tab.find(designatorIdent.getDesignatorName());
		if(obj.getType() == Tab.noType) {
			report_error("Greska - korisceno ime nikada nije deklarisano!", designatorIdent);
		}
		designatorIdent.obj = obj;
	}

//--------------------------------------statement-------------------------------------------------

	public void visit(While statement) {
		withinLoop++;
	}

	public void visit(StatWhile statement) {
		withinLoop--;
	}


	public void visit(StatBreak statement) {
		if(withinLoop <= 0) {
			report_error("Greska - Break moze da se koristi samo unutar while / foreach petlji!", statement);
		}
	}

	public void visit(StatContinue statement) {
		if(withinLoop <= 0) {
			report_error("Greska - Continue moze da se koristi samo unutar while / foreach petlji!", statement);
		}
	}

	public void visit(StatRead statement) {
		int designatorKind = statement.getDesignator().obj.getKind();
		Struct designatorType = statement.getDesignator().obj.getType();
		if(designatorKind != Obj.Var && designatorKind != Obj.Elem
				&& designatorKind != Obj.Fld) {
			report_error("Greska - designator mora da oznacava promenljivu, element niza, "
					+" ili polje unutar objekta!", statement);
			return;
		}
		if(designatorType != Tab.intType && designatorType != boolType
				&& designatorType != Tab.charType){
			report_error("Greska - designator mora biti tipa int, char, ili bool!", statement);
			return;
		}
	}

	public void visit(PrintExpr printExpr) {
		Struct exprType = printExpr.getExpr().struct;
		if(exprType != Tab.intType && exprType != Tab.charType && exprType != boolType) {
			report_error("Greska - izraz mora biti tipa int, char, ili bool!", printExpr);
		}
	}

	public void visit(PrintWithConst printExpr) {
		Struct exprType = printExpr.getExpr().struct;
		if(exprType != Tab.intType && exprType != Tab.charType && exprType != boolType) {
			report_error("Greska - izraz mora biti tipa int, char, ili bool!", printExpr);
		}
	}

	public void visit(StatReturn statement) {
		if(!(statement.getExpr().struct.equals(declaredType))) {
			report_error("Greska - tip izraza mora da bude ekvivalentan povratnom"
					+ " tipu tekuce globalne funkcije!", statement);
			return;
		}
		if(currentMethod == Tab.noObj) {
			report_error("Greska - return statement ne sme da se nadje izvan globalne funkcije!", statement);
			//todo - test.
		}
	}

	public void visit(StatReturnVoid statement) {
		if(declaredType != Tab.noType) {
			report_error("Greska - metoda mora da bude deklarisana kao void!", statement);
		}
	}

	public void visit(SingleCondFact condition) {
		if(condition.getExpr().struct != boolType) {
			report_error("Greska - izraz unutar uslova mora da bude tipa bool!", condition);
		}
	}

	public void visit(MultipleCondFact condition) {
		if(!(condition.getExpr().struct.compatibleWith(condition.getExpr1().struct))) {
			report_error("Greska - tipovi izraza unutar uslova moraju da budu kompatibilni!", condition);
			return;
		}

		if((condition.getExpr().struct.getKind() == Struct.Array || condition.getExpr().struct.getKind() == Struct.Class)
			&& !(condition.getRelop() instanceof Eq || condition.getRelop() instanceof Neq)) {
			report_error("Greska - uz promenljive tipa klase ili niza, od relacionih operatora,"
					+ " mogu se koristiti samo EQ i NEQ!", condition);
			return;
		}
	}

	public void visit(ForEach foreach) {
		withinLoop++;
	}

	public void visit(StatForeach statement) {
		withinLoop--;
		if(statement.getDesignator().obj.getType().getKind() != Struct.Array) {
			report_error("Foreach petlja moze da iterira samo kroz niz!", statement);
			return;
		}

		Struct desigType = statement.getDesignator().obj.getType().getElemType();
		Obj iterObj = Tab.find(statement.getIter());
		if(iterObj.getKind() != Obj.Var || iterObj.getType() != desigType) {
			report_error("Greska - iterator mora da bude promeljiva istog tipa kao"
					+ " elementi niza!", statement);
			return;
		}
	}

	public void visit(ActPars actPars) {
		if(actPars instanceof ActParams) {
			actualParametersList.add(((ActParams) actPars).getExpr().struct);
		}
	}

	public void visit(ActParams actPars) {
		actualParametersList.add(actPars.getExpr().struct);
	}

	public void visit(SingleActParam actPar) {
		actualParametersList.add(actPar.getExpr().struct);
	}

//---------------------------------------factor--------------------------------------------------

	public void visit(FactorNum factor) {
		factor.struct = Tab.intType;
	}

	public void visit(FactorCh factor) {
		factor.struct = Tab.charType;
	}

	public void visit(FactorBool factor) {
		factor.struct = boolType;
	}

	public void visit(FactorExpr factor) {
		factor.struct = factor.getExpr().struct;
	}

	public void visit(FactorNewTypeParams factor) {
		factor.struct = factor.getType().struct;
	}

	public void visit(FactorNewType factor) {
		factor.struct = factor.getType().struct;
	}

	public void visit(FactorDesignator factor) {
		factor.struct = factor.getDesignator().obj.getType();
	}

	public void visit(FactorDesignatorCall methodCall) {
		Obj desigObj = methodCall.getDesignator().obj;
		if(desigObj.getKind() != Obj.Meth) {
			report_error("Greska - pokusaj poziva necega sto nije globalna funkcija glavnog programa!", methodCall);
			return;
		}

		if(desigObj.getLevel() > 0) {
			report_error("Greska - broj formalnih i stvarnih parametara funkcije nije isti!", methodCall);
			return;
		}

		methodCall.struct = methodCall.getDesignator().obj.getType();

		globalFunctionCallCount++;
		report_info("Detektovan poziv globalne funkcije programa " + desigObj.getName(),
				methodCall);
	}

	public void visit(FactorDesignatorParams methodCall) {
		Obj desigObj = methodCall.getDesignator().obj;
		if(desigObj.getKind() != Obj.Meth) {
			report_error("Greska - Pokusaj poziva necega sto nije globalna funkcija glavnog programa!", methodCall);
			return;
		}

		if(desigObj.getLevel() != actualParametersList.size()) {
			report_error("Greska - Broj formalnih i stvarnih parametara funkcije nije isti!", methodCall);
			return;
		}

		for(Obj formalParameter : desigObj.getLocalSymbols()) {
			if(!(actualParametersList.get(formalParameter.getAdr()).assignableTo(formalParameter.getType()))) {
				report_error("Greska - Tip stvarnog argumenta mora da bude kompatibilan pri dodeli"
						+ " sa tipom formalnog argumenta na odgovarajucoj poziciji!", methodCall);
				return;
			}
		}
		actualParametersList.clear();

		methodCall.struct = methodCall.getDesignator().obj.getType();

		globalFunctionCallCount++;
		report_info("Detektovan poziv globalne funkcije programa " + desigObj.getName(),
				methodCall);
	}

	public void visit(FactorNewTypeExpr factor) {
		if(factor.getExpr().struct.getKind() != Struct.Int) {
			report_error("Greska - izraz unutar uglastih zagrada mora biti tipa int!", factor);
			return;
		}

		factor.struct = new Struct(Struct.Array, factor.getType().struct);
	}

//--------------------------------------expression-------------------------------------------------

	public void visit(TermExpr expr) {
		expr.struct = expr.getTerm().struct;
	}

	public void visit(NegTermExpr expr) {
		if(expr.getTerm().struct.getKind() != Struct.Int) {
			report_error("Greska - term nakon znaka minus mora da bude int!", expr);
			return;
		}

		expr.struct = expr.getTerm().struct;
	}

	public void visit(MultiTermExpr multiExpr) {
		if(multiExpr.getExpr().struct.getKind() != Struct.Int || multiExpr.getTerm().struct.getKind() != Struct.Int) {
			report_error("Greska - jedini tip dozvoljen kod operacije sabiranja je int!", multiExpr);
			return;
		}

		if(!(multiExpr.getExpr().struct.compatibleWith(multiExpr.getTerm().struct))) {
			report_error("Greska - tipovi prilikom sabiranja nisu kompatibilni!", multiExpr);
			return;
		}

		multiExpr.struct = multiExpr.getTerm().struct;
	}

	public void visit(MultiTerm mulExpr) {
		if(mulExpr.getTerm().struct.getKind() != Struct.Int || mulExpr.getFactor().struct.getKind() != Struct.Int) {
			report_error("Greska - jedini tip dozvoljen kod operacije mnozenja je int!", mulExpr);
			return;
		}
		mulExpr.struct = mulExpr.getFactor().struct;
	}

	public void visit(SingleTerm term) {
		term.struct = term.getFactor().struct;
	}

//TODO: add context conditions for chr, ord, len.
//every ident declared before first use?
	// sve uvezano (struct, obj)?

} // TODO: check if IDENT should keep track of var count instead of VarDecl,.. zbog int a, b, c; npr.
// testiraj bar count svega i popravi ono sto pada parsiranje B level testa.