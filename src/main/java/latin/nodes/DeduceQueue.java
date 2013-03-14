
package latin.nodes;

public interface DeduceQueue {
    public boolean setSupport(Supported supported, Supporter supporter);
    public void addDeduced(Supported supported) throws ContradictionException;
}
