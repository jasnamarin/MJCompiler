// generated with ast extension for cup
// version 0.8
// 17/0/2023 18:47:51


package rs.ac.bg.etf.pp1.ast;

public class DesigInc extends DesigOp {

    public DesigInc () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesigInc(\n");

        buffer.append(tab);
        buffer.append(") [DesigInc]");
        return buffer.toString();
    }
}
