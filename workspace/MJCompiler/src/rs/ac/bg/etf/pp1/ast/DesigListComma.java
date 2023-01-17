// generated with ast extension for cup
// version 0.8
// 17/0/2023 18:47:51


package rs.ac.bg.etf.pp1.ast;

public class DesigListComma extends DesigList {

    private DesigList DesigList;
    private Comma Comma;

    public DesigListComma (DesigList DesigList, Comma Comma) {
        this.DesigList=DesigList;
        if(DesigList!=null) DesigList.setParent(this);
        this.Comma=Comma;
        if(Comma!=null) Comma.setParent(this);
    }

    public DesigList getDesigList() {
        return DesigList;
    }

    public void setDesigList(DesigList DesigList) {
        this.DesigList=DesigList;
    }

    public Comma getComma() {
        return Comma;
    }

    public void setComma(Comma Comma) {
        this.Comma=Comma;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesigList!=null) DesigList.accept(visitor);
        if(Comma!=null) Comma.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesigList!=null) DesigList.traverseTopDown(visitor);
        if(Comma!=null) Comma.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesigList!=null) DesigList.traverseBottomUp(visitor);
        if(Comma!=null) Comma.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesigListComma(\n");

        if(DesigList!=null)
            buffer.append(DesigList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Comma!=null)
            buffer.append(Comma.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesigListComma]");
        return buffer.toString();
    }
}
