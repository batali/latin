
package latin.nodes;

public interface Deducer extends Supporter {
    public boolean deduce(DeduceQueue deduceQueue) throws ContradictionException;
}