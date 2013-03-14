
package latin.nodes;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractDisjunctionRule implements BSRule {

    public final List<? extends BooleanSetting> settings;
    protected int trueCount;
    protected int falseCount;
    protected @Nullable Supported dsupported;

    public AbstractDisjunctionRule(List<? extends BooleanSetting> settings) {
        this.settings = settings;
        this.trueCount = 0;
        this.falseCount = 0;
        this.dsupported = null;
    }

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
        for (BooleanSetting setting : settings) {
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

    public boolean contradictionCheck() {
        return falseCount == settings.size();
    }

    public boolean deduceCheck() {
        return trueCount == 0 && falseCount + 1 == settings.size();
    }

    public void deduce(DeduceQueue dq) throws ContradictionException {
        if (deduceCheck()) {
            for (BooleanSetting setting : settings) {
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

    public boolean retractDsupported(RetractQueue rq) {
        return rq.removeSupport(dsupported);
    }

    public void recordSet(BooleanSetting setting, boolean sv, DeduceQueue dq) throws ContradictionException {
        addCount(sv, 1);
        if (contradictionCheck()) {
            throw new ContradictionException("DisjunctionRule", this);
        }
        else {
            deduce(dq);
        }
    }

    public void recordUnset(BooleanSetting setting, boolean sv, RetractQueue rq) {
        addCount(sv, -1);
        if (!sv) {
            rq.removeSupport(dsupported);
        }
        else if (deduceCheck()) {
            rq.addRededucer(this);
        }
    }

    public void collectContradictionSupport(SupportCollector supportCollector) {
        for (BooleanSetting setting : settings) {
            supportCollector.recordSupporter(setting.getOpposite());
        }
    }

    @Override
    public SupportCollector collectSupport(SupportCollector supportCollector) {
        collectContradictionSupport(supportCollector);
        return supportCollector;
    }
}