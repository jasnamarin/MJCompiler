// generated with ast extension for cup
// version 0.8
// 17/0/2023 20:45:57


package rs.ac.bg.etf.pp1.ast;

public class ConstDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private Type Type;
    private ConstSingle ConstSingle;
    private ConstMulti ConstMulti;

    public ConstDecl (Type Type, ConstSingle ConstSingle, ConstMulti ConstMulti) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ConstSingle=ConstSingle;
        if(ConstSingle!=null) ConstSingle.setParent(this);
        this.ConstMulti=ConstMulti;
        if(ConstMulti!=null) ConstMulti.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ConstSingle getConstSingle() {
        return ConstSingle;
    }

    public void setConstSingle(ConstSingle ConstSingle) {
        this.ConstSingle=ConstSingle;
    }

    public ConstMulti getConstMulti() {
        return ConstMulti;
    }

    public void setConstMulti(ConstMulti ConstMulti) {
        this.ConstMulti=ConstMulti;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(ConstSingle!=null) ConstSingle.accept(visitor);
        if(ConstMulti!=null) ConstMulti.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ConstSingle!=null) ConstSingle.traverseTopDown(visitor);
        if(ConstMulti!=null) ConstMulti.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ConstSingle!=null) ConstSingle.traverseBottomUp(visitor);
        if(ConstMulti!=null) ConstMulti.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDecl(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstSingle!=null)
            buffer.append(ConstSingle.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstMulti!=null)
            buffer.append(ConstMulti.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDecl]");
        return buffer.toString();
    }
}
