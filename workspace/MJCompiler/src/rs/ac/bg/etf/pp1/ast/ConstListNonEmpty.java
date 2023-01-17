// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class ConstListNonEmpty extends ConstMulti {

    private ConstMulti ConstMulti;
    private ConstSingle ConstSingle;

    public ConstListNonEmpty (ConstMulti ConstMulti, ConstSingle ConstSingle) {
        this.ConstMulti=ConstMulti;
        if(ConstMulti!=null) ConstMulti.setParent(this);
        this.ConstSingle=ConstSingle;
        if(ConstSingle!=null) ConstSingle.setParent(this);
    }

    public ConstMulti getConstMulti() {
        return ConstMulti;
    }

    public void setConstMulti(ConstMulti ConstMulti) {
        this.ConstMulti=ConstMulti;
    }

    public ConstSingle getConstSingle() {
        return ConstSingle;
    }

    public void setConstSingle(ConstSingle ConstSingle) {
        this.ConstSingle=ConstSingle;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstMulti!=null) ConstMulti.accept(visitor);
        if(ConstSingle!=null) ConstSingle.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstMulti!=null) ConstMulti.traverseTopDown(visitor);
        if(ConstSingle!=null) ConstSingle.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstMulti!=null) ConstMulti.traverseBottomUp(visitor);
        if(ConstSingle!=null) ConstSingle.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstListNonEmpty(\n");

        if(ConstMulti!=null)
            buffer.append(ConstMulti.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstSingle!=null)
            buffer.append(ConstSingle.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstListNonEmpty]");
        return buffer.toString();
    }
}
