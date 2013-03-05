
package latin.setter;

import java.util.List;

public abstract class AbstractDisjunctionRule implements SupportRule {

    protected int trueCount;
    protected int falseCount;

    public AbstractDisjunctionRule() {
        this.trueCount = 0;
        this.falseCount = 0;
    }

    public abstract List<? extends Setter> getSetters();

    public int getTrueCount() {
        return trueCount;
    }

    public void addTrueCount(int delta) {
        trueCount += delta;
    }

    public void addFalseCount(int delta) {
        falseCount += delta;
    }

    public void addCount(boolean tv, int delta) {
        if (tv) {
            trueCount += delta;
        }
        else {
            falseCount += delta;
        }
    }

    public boolean disjunctionReDeduceTest() {
        return trueCount == 0 && falseCount + 1 == getSetters().size();
    }

    public void disjunctionDeduce(Propagator propagator) throws ContradictionException {
        if (trueCount == 0) {
            List<? extends Setter> setters = getSetters();
            if (falseCount == setters.size()) {
                propagator.recordContradiction(this);
            }
            else if (falseCount + 1 == setters.size()) {
                for (Setter setter : setters) {
                    if (setter.getStatus() == 0) {
                        propagator.recordSupported(setter, this);
                        return;
                    }
                }
            }
        }
    }

    public boolean checkCounts() {
        int ntc = 0;
        int nfc = 0;
        for (Setter setter : getSetters()) {
            int status = setter.getStatus();
            if (status > 0) {
                ntc += 1;
            }
            else if (status < 0) {
                nfc += 1;
            }
        }
        boolean okp = (ntc == trueCount && nfc == falseCount);
        return okp;
    }

    public String getCountsString() {
        int s = getSetters().size();
        int tc = trueCount;
        int fc = falseCount;
        int uc = s - (tc + fc);
        return String.format("tc %d fc %d uc %d", tc, fc, uc);
    }

    @Override
    public void collectSupported(SupportCollector supportCollector) {
        for (Setter setter : getSetters()) {
            int status = setter.getStatus();
            if (status > 0) {
                supportCollector.recordSupported(setter);
            }
            else if (status < 0) {
                supportCollector.recordSupported(setter.getOpposite());
            }
        }
    }

}

