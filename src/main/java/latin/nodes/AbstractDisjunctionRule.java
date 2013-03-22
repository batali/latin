
package latin.nodes;

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

    public boolean deduceCheck() {
        return trueCount == 0 && falseCount + 1 >= settingCount;
    }

    public void deduce(DeduceQueue dq) throws ContradictionException {
        if (deduceCheck()) {
            for (int p = 0; p < settingCount; p++) {
                BooleanSetting setting = getSetting(p);
                if (setting.supportable()) {
                    dq.setSupport(setting, this);
                    return;
                }
            }
            throw new ContradictionException("No supportable", this);
        }
    }

    public boolean canRededuce(BooleanSetting setting, boolean sv) {
        return sv && deduceCheck();
    }

    public void recordSet(BooleanSetting setting, boolean sv, DeduceQueue dq) throws ContradictionException {
        addCount(sv, 1);
        if (deduceCheck()) {
            deduce(dq);
        }
    }

    @Override
    public void recordUnset(BooleanSetting setting, boolean sv, RetractQueue rq) {
        addCount(sv, -1);
        if (!sv) {
            retractSupported(rq);
        }
        else if (deduceCheck()) {
            rq.addRededucer(this);
        }
    }

    @Override
    public SupportCollector collectSupport(SupportCollector supportCollector) {
        for (int p = 0; p < settingCount; p++) {
            BooleanSetting setting = getSetting(p);
            supportCollector.recordSupporter(setting.getOpposite());
        }
        return supportCollector;
    }

}