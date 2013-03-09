
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Set;

public abstract class BooleanSetting implements Supported {

    public abstract BooleanSetting getOpposite();

    protected Supporter supporter;
    protected Set<BSRule> rules;

    public BooleanSetting() {
        this.supporter = null;
        this.rules = Sets.newHashSet();
    }

    public Supporter getSupporter() {
        return supporter;
    }

    public abstract boolean booleanValue();

    public BooleanSetting getTrueSetting() {
        return booleanValue() ? this : getOpposite();
    }

    @Override
    public int getStatus() {
        if (supporter != null) {
            return 1;
        }
        else if (getOpposite().supporter != null) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public boolean supportable() {
        return getStatus() >= 0;
    }

    @Override
    public boolean setSupporter(Supporter newSupporter) throws ContradictionException {
        Preconditions.checkState(supportable());
        if (getSupporter() != null) {
            return false;
        }
        Preconditions.checkState(getOpposite().getSupporter()==null);
        supporter = newSupporter;
        Preconditions.checkState(newSupporter.addSupported(this));
        return true;
    }

    @Override
    public boolean unsetSupporter() {
        Supporter os = supporter;
        supporter = null;
        Preconditions.checkState(os != null);
        Preconditions.checkState(os.removeSupported(this));
        return true;
    }

    public Set<BSRule> getRules() {
        return rules;
    }

    @Override
    public void announceSet(DeduceQueue deduceQueue) throws ContradictionException {
        BooleanSetting op = getOpposite();
        for (BSRule r : op.getRules()) {
            r.recordSet(op, false, deduceQueue);
        }
        for (BSRule r : getRules()) {
            r.recordSet(this, true, deduceQueue);
        }
    }

    @Override
    public void announceUnset(RetractQueue retractQueue, Supporter stopAt) {
        BooleanSetting op = getOpposite();
        for (BSRule r : op.getRules()) {
            r.recordUnset(op, false, retractQueue);
            if (Objects.equal(r, stopAt)) {
                return;
            }
        }
        for (BSRule r : getRules()) {
            r.recordUnset(this, true, retractQueue);
            if (Objects.equal(r, stopAt)) {
                return;
            }
        }
    }

    public void addRule(BSRule r) {
        getRules().add(r);
    }

}
