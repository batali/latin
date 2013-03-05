
package latin.setter;

public class ContradictionException extends Exception {

    public ContradictionException() {
        super();
    }

    public ContradictionException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}