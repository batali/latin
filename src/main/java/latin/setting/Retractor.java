package latin.setting;

import java.util.Collection;

public interface Retractor {
    public boolean addRetracted(Supportable supportable);
    public boolean retractLoop();
    public void addRededucer(DeduceRule deduceRule);
    public boolean atContradiction(DeduceRule deduceRule, Supportable supportable);
    public Collection<DeduceRule> getRededucers();
}