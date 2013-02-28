
package latin.setter;

public class ContradictionException extends Exception {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}