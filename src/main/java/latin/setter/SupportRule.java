
package latin.setter;

public interface SupportRule extends Supporter, Deducer {
    public void recordSupported(Setter setter, boolean tv, Propagator propagator) throws ContradictionException;
    public void recordRetracted(Setter setter, boolean tv, Propagator propagator);
}