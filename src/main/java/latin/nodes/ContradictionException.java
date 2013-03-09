
package latin.nodes;

public class ContradictionException extends Exception {

    public final Supporter atRule;

    public ContradictionException(String message, Supporter atRule) {
        super(message);
        this.atRule = atRule;
    }

    public ContradictionException() {
        this("", null);
    }

    public ContradictionException(String message) {
        this(message, null);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}