package latin.nodes;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class AbstractDrule implements BSRule {

    protected int trueCount;
    protected int falseCount;

    public AbstractDrule() {
        this.trueCount = 0;
        this.falseCount = 0;
    }

    public void addCount(boolean tv, int delta) {
        if (tv) {
            trueCount += delta;
        }
        else {
            falseCount += delta;
        }
    }

    public abstract Supported peekSupported();

    public void retractAll(RetractQueue retractQueue) {
        Supported s = null;
        while ((s = peekSupported()) != null) {
            if (s.unsetSupporter()) {
                retractQueue.addRetracted(s);
            }
        }
    }

    public boolean disjunctionDeduceTest(List<? extends BooleanSetting> settings) {
        return trueCount == 0 && falseCount + 1 >= settings.size();
    }

    public void disjunctionDeduce(DeduceQueue deduceQueue, List<? extends BooleanSetting> settings)
            throws ContradictionException {
        if (trueCount == 0) {
            if (falseCount == settings.size()) {
                throw new ContradictionException("zero true", this);
            }
            else if (falseCount + 1 == settings.size()) {
                for (BooleanSetting setting : settings) {
                    if (setting.supportable() && setting.setSupporter(this)) {
                        deduceQueue.addDeduced(setting);
                        break;
                    }
                }
            }
        }
    }

    public static Pair<Integer,Integer> getCounts(List<? extends BooleanSetting> settings) {
        int ctc = 0;
        int cfc = 0;
        for (BooleanSetting bs : settings) {
            int st = bs.getStatus();
            if (st > 0) {
                ctc += 1;
            }
            else if (st < 0) {
                cfc += 1;
            }
        }
        return Pair.of(ctc, cfc);
    }

    public abstract Pair<Integer,Integer> getCounts();

    public void setCounts(DeduceQueue deduceQueue) throws ContradictionException {
        Pair<Integer,Integer> cp = getCounts();
        trueCount = cp.getLeft();
        falseCount = cp.getRight();
        deduce(deduceQueue);
    }

    public boolean checkCounts() {
        Pair<Integer,Integer> cp = getCounts();
        return trueCount == cp.getLeft() && falseCount == cp.getRight();
    }

}
