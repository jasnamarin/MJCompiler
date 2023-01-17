// generated with ast extension for cup
// version 0.8
// 17/0/2023 18:47:51


package rs.ac.bg.etf.pp1.ast;

public class FactorNum extends Factor {

    private Integer numVal;

    public FactorNum (Integer numVal) {
        this.numVal=numVal;
    }

    public Integer getNumVal() {
        return numVal;
    }

    public void setNumVal(Integer numVal) {
        this.numVal=numVal;
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
        buffer.append("FactorNum(\n");

        buffer.append(" "+tab+numVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorNum]");
        return buffer.toString();
    }
}
