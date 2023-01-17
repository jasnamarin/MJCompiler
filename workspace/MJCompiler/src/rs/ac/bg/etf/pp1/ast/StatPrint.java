// generated with ast extension for cup
// version 0.8
// 17/0/2023 18:47:51


package rs.ac.bg.etf.pp1.ast;

public class StatPrint extends Statement {

    private PrintVal PrintVal;

    public StatPrint (PrintVal PrintVal) {
        this.PrintVal=PrintVal;
        if(PrintVal!=null) PrintVal.setParent(this);
    }

    public PrintVal getPrintVal() {
        return PrintVal;
    }

    public void setPrintVal(PrintVal PrintVal) {
        this.PrintVal=PrintVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(PrintVal!=null) PrintVal.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(PrintVal!=null) PrintVal.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(PrintVal!=null) PrintVal.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("StatPrint(\n");

        if(PrintVal!=null)
            buffer.append(PrintVal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [StatPrint]");
        return buffer.toString();
    }
}
