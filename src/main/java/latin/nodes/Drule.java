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
    public Supported peekSupported() {
        return supported;
    }

    @Override
    public void recordSet(BooleanSetting setting, boolean sp, DeduceQueue deduceQueue) throws ContradictionException {
        addCount(sp, 1);
        deduce(deduceQueue);
    }

    @Override
    public void recordUnset(BooleanSetting  setting, boolean sp, RetractQueue retractQueue) {
        addCount(sp, -1);
        if (sp) {
            if (disjunctionDeduceTest(setters)) {
                retractQueue.addRededucer(this);
            }
        }
        else {
            Supported os = supported;
            if (os != null) {
                if(os.unsetSupporter()) {
                    Preconditions.checkState(supported == null);
                    Preconditions.checkState(os != null);
                    retractQueue.addRetracted(os);
                }
            }
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

    public void collectSupport(SupportCollector supportCollector) {
        for (BooleanSetting bs : setters) {
            supportCollector.recordSupporter(bs.getOpposite());
        }
    }
}
