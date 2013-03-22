
package latin.nodes;

public interface RetractQueue {
    public void addRededucer (Deducer deducer);
    public boolean addRetracted(Supported supported);
    public boolean removeSupport(Supported supported);
    public Supported peekRetracted();
    public Supported pollRetracted();
    public Deducer peekRededucer();
    public Deducer pollRededucer();
    public boolean haveRetracted();
    public boolean haveRededucer();
    public boolean retractLoop();
    public boolean rededuceLoop(DeduceQueue deduceQueue) throws ContradictionException;
}