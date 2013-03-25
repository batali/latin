
package latin.nodes;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractDisjunctionRule implements BSRule {

    protected int trueCount;
    protected int falseCount;
    protected final int settingCount;

    public AbstractDisjunctionRule(int settingCount) {
        this.settingCount = settingCount;
        this.trueCount = 0;
        this.falseCount = 0;
    }

    public abstract BooleanSetting getSetting(int p);
    public abstract void retractSupported(RetractQueue rq);

    public void addCount(boolean tv, int delta) {
        if (tv) {
            trueCount += delta;
        }
        else {
            falseCount += delta;
        }
    }

    public Pair<Integer,Integer> getCounts() {
        int tc = 0;
        int fc = 0;
        for (int p = 0; p < settingCount; p++) {
            BooleanSetting setting = getSetting(p);
            int st = setting.getStatus();
            if (st > 0) {
                tc += 1;
            }
            else if (st < 0) {
                fc += 1;
            }
        }
        return Pair.of(tc, fc);
    }

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

    public boolean disjunctionDeduceTest() {
        return trueCount == 0 && falseCount + 1 >= settingCount;
    }

    @Override
    public boolean deduce(DeduceQueue dq) throws ContradictionException {
        if (disjunctionDeduceTest()) {
            for (int p = 0; p < settingCount; p++) {
                BooleanSetting setting = getSetting(p);
                if (setting.supportable()) {
                    return dq.setSupport(setting, this);
                }
            }
            throw new ContradictionException("No Supportable", this);
        }
        return false;
    }

    public void recordSet(BooleanSetting setting, boolean sv, DeduceQueue dq) throws ContradictionException {
        addCount(sv, 1);
        deduce(dq);
    }

    public boolean undermines(BooleanSetting setting, boolean sv) {
        return !sv;
    }

    public boolean canRededuce(BooleanSetting setting, boolean sv) {
        return sv && disjunctionDeduceTest();
    }

    @Override
    public void recordUnset(BooleanSetting setting, boolean sv, RetractQueue rq) {
        boolean um = doesSupport() && undermines(setting, sv);
        addCount(sv, -1);
        if (um) {
            retractSupported(rq);
        }
        else if (canRededuce(setting, sv)) {
            rq.addRededucer(this);
        }
    }

    public int getSupporterStatus() {
        return -1;
    }

    @Override
    public SupportCollector collectSupport(SupportCollector supportCollector) {
        int ss = getSupporterStatus();
        for (int p = 0; p < settingCount; p++) {
            BooleanSetting bst = getSetting(p);
            int st = bst.getStatus();
            Preconditions.checkState(st != 0);
            if (st == ss) {
                BooleanSetting sst = (st > 0) ? bst : bst.getOpposite();
                Preconditions.checkState(sst.haveSupporter());
                Preconditions.checkState(!sst.supportedBy(this));
                supportCollector.recordSupporter(sst);
            }
        }
        return supportCollector;
    }

}