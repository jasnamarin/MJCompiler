// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class DesignatorStmt extends DesignatorStatement {

    private Designator Designator;
    private DesigOp DesigOp;

    public DesignatorStmt (Designator Designator, DesigOp DesigOp) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.DesigOp=DesigOp;
        if(DesigOp!=null) DesigOp.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public DesigOp getDesigOp() {
        return DesigOp;
    }

    public void setDesigOp(DesigOp DesigOp) {
        this.DesigOp=DesigOp;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(DesigOp!=null) DesigOp.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(DesigOp!=null) DesigOp.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(DesigOp!=null) DesigOp.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorStmt(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesigOp!=null)
            buffer.append(DesigOp.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorStmt]");
        return buffer.toString();
    }
}
