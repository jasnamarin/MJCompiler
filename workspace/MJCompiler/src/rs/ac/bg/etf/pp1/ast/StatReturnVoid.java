// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class StatReturnVoid extends Statement {

    public StatReturnVoid () {
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
        buffer.append("StatReturnVoid(\n");

        buffer.append(tab);
        buffer.append(") [StatReturnVoid]");
        return buffer.toString();
    }
}
