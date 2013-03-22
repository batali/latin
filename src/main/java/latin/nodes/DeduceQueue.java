
package latin.nodes;

public interface DeduceQueue {
    public boolean addSupported(Supported supported);
    public boolean setSupport(Supported supported, Supporter supporter);
    public Supported peekSupported();
    public Supported pollSupported();
    public boolean propagateLoop() throws ContradictionException;
    public boolean haveSupported();
    public boolean finish();
}
