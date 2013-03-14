
package latin.nodes;

public interface Deducer extends Supporter {
    public void deduce(DeduceQueue deduceQueue) throws ContradictionException;
    public boolean deduceCheck();
    public boolean canRededuce(BooleanSetting setting, boolean sv);
}