// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class ClassNameDerived1 extends ClassName {

    private String className;
    private DerivedFrom DerivedFrom;

    public ClassNameDerived1 (String className, DerivedFrom DerivedFrom) {
        this.className=className;
        this.DerivedFrom=DerivedFrom;
        if(DerivedFrom!=null) DerivedFrom.setParent(this);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className=className;
    }

    public DerivedFrom getDerivedFrom() {
        return DerivedFrom;
    }

    public void setDerivedFrom(DerivedFrom DerivedFrom) {
        this.DerivedFrom=DerivedFrom;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DerivedFrom!=null) DerivedFrom.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DerivedFrom!=null) DerivedFrom.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DerivedFrom!=null) DerivedFrom.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassNameDerived1(\n");

        buffer.append(" "+tab+className);
        buffer.append("\n");

        if(DerivedFrom!=null)
            buffer.append(DerivedFrom.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassNameDerived1]");
        return buffer.toString();
    }
}
