
package latin.nodes;

public interface DeduceQueue {
    public boolean setSupport(Supported supported, Supporter supporter);
    public boolean propagateLoop() throws ContradictionException;
}
