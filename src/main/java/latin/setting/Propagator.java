
package latin.setting;

public interface Propagator {
    public boolean addDeduced(Supportable supportable);
    public boolean deduceLoop();
    public void recordContradictionRule(DeduceRule deduceRule);
    public boolean haveContradiction();
}