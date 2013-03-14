package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Drule extends AbstractDrule {

    private List<? extends BooleanSetting> setters;
    public final String name;
    private Supported supported;

    public Drule(String name, List<? extends BooleanSetting> setters) {
        super(setters.size());
        this.setters = setters;
        for (BooleanSetting s : setters) {
            s.addRule(this);
        }
        this.name = name;
        this.supported = null;
    }

    public boolean addSupported(Supported ns) {
        Preconditions.checkState(supported == null);
        Preconditions.checkState(Objects.equal(ns.getSupporter(), this));
        supported = ns;
        return true;
    }

    @Override
    public boolean removeSupported(Supported rs) {
        Preconditions.checkState(rs.getSupporter()==null);
        Preconditions.checkState(Objects.equal(supported, rs));
        supported = null;
        return true;
    }


    @Override
    public void afterUnset(boolean sp, RetractQueue retractQueue) {
        if (sp) {
            if (disjunctionDeduceTest(setters)) {
                retractQueue.addRededucer(this);
            }
        }
        else {
            retractQueue.removeSupport(supported);
        }
    }

    @Override
    public void deduce(DeduceQueue deduceQueue) throws ContradictionException {
        disjunctionDeduce(deduceQueue, setters);
    }

    public Pair<Integer,Integer> getCounts() {
        return getCounts(setters);
    }

    public String toString() {
        return name;
    }

    public SupportCollector collectSupport(SupportCollector supportCollector) {
        for (BooleanSetting bs : setters) {
            int st = bs.getStatus();
            if (st > 0) {
                supportCollector.recordSupporter(bs);
            }
            else if (st < 0) {
                supportCollector.recordSupporter(bs.getOpposite());
            }
        }
        return supportCollector;
    }

    public void collectContradictionSupport(SupportCollector supportCollector) {
        for (BooleanSetting bs : setters) {
            supportCollector.recordSupporter(bs.getOpposite());
        }
    }

    public boolean doesSupport() {
        return supported != null;
    }

}

