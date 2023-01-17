// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class PrintWithConst extends PrintVal {

    private Expr Expr;
    private Integer numConstVal;

    public PrintWithConst (Expr Expr, Integer numConstVal) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.numConstVal=numConstVal;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public Integer getNumConstVal() {
        return numConstVal;
    }

    public void setNumConstVal(Integer numConstVal) {
        this.numConstVal=numConstVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintWithConst(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+numConstVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintWithConst]");
        return buffer.toString();
    }
}
