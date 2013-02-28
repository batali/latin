
package latin.setter;

public interface Deducer {
    public void deduce(Propagator propagator) throws ContradictionException;
}