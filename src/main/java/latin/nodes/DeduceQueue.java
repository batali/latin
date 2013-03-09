
package latin.nodes;

public interface DeduceQueue {
    public void addDeduced(Supported supported) throws ContradictionException;
}
