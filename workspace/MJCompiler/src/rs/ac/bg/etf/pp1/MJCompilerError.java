package rs.ac.bg.etf.pp1;

public class MJCompilerError {

    public enum CompilerErrorType { LEX_ERR, SYNTAX_ERR, SEMANTIC_ERR };

    private int line;
    private CompilerErrorType type;
    private String msg;

    public MJCompilerError(int line, CompilerErrorType type, String msg) {
        super();
        this.line = line;
        this.type = type;
        this.msg = msg;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public CompilerErrorType getType() {
        return type;
    }

    public void setType(CompilerErrorType type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return String.format("[Line #%-2d]  %s: %s", line, type, msg);
    }

}
