
package latin.nodes;

public interface Deducer extends Supporter {
    public void deduce(DeduceQueue deduceQueue) throws ContradictionException;
}