
package latin.nodes;

public interface RetractQueue {
    public boolean addRetracted(Supported supported);
    public void addRededucer (Deducer deducer);
    public void retractLoop () throws ContradictionException;
    public boolean removeSupport(Supported supported);
}