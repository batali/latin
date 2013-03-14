package latin.nodes;

import com.google.common.base.Objects;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class AbstractDrule implements BSRule {

    protected int trueCount;
    protected int falseCount;
    protected final int settingCount;

    public AbstractDrule(int settingCount) {
        this.settingCount = settingCount;
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

    public boolean disjunctionDeduceTest(List<? extends BooleanSetting> settings) {
        return trueCount == 0 && falseCount + 1 >= settings.size();
    }

    public boolean contradictionCheck() {
        return falseCount == settingCount;
    }

    public void disjunctionDeduce(DeduceQueue deduceQueue, List<? extends BooleanSetting> settings)
            throws ContradictionException {
        if (trueCount == 0) {
            if (falseCount == settings.size()) {
                throw new ContradictionException("zero true", this);
            }
            else if (falseCount + 1 == settings.size()) {
                boolean foundone = false;
                for (BooleanSetting setting : settings) {
                    if (setting.supportable()) {
                        foundone = true;
                        deduceQueue.setSupport(setting, this);
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

    @Override
    public void recordSet(BooleanSetting setting, boolean sp, DeduceQueue deduceQueue) throws ContradictionException {
        addCount(sp, 1);
        if (contradictionCheck()) {
            throw new ContradictionException(String.format("%s %d %d / %d",
                    getClass().getSimpleName(), trueCount, falseCount, settingCount), this);
        }
        deduce(deduceQueue);
    }

    public abstract void afterUnset(boolean sv, RetractQueue retractQueue);

    @Override
    public void recordUnset(BooleanSetting  setting, boolean sp, RetractQueue retractQueue, BSRule stopAt) {
        addCount(sp, -1);
        if (Objects.equal(this, stopAt)) {
            return;
        }
        afterUnset(sp, retractQueue);
    }
}
