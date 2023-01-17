// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class DesigListLong extends DesigList {

    private DesigList DesigList;
    private Comma Comma;
    private Designator Designator;

    public DesigListLong (DesigList DesigList, Comma Comma, Designator Designator) {
        this.DesigList=DesigList;
        if(DesigList!=null) DesigList.setParent(this);
        this.Comma=Comma;
        if(Comma!=null) Comma.setParent(this);
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
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

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesigList!=null) DesigList.accept(visitor);
        if(Comma!=null) Comma.accept(visitor);
        if(Designator!=null) Designator.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesigList!=null) DesigList.traverseTopDown(visitor);
        if(Comma!=null) Comma.traverseTopDown(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesigList!=null) DesigList.traverseBottomUp(visitor);
        if(Comma!=null) Comma.traverseBottomUp(visitor);
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesigListLong(\n");

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

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesigListLong]");
        return buffer.toString();
    }
}
