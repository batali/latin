
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

    public boolean haveSupporter() {
        return supporter != null;
    }

    public boolean supportedBy(Supporter asupporter) {
        return Objects.equal(supporter, asupporter);
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

    public BooleanSetting getSupportedSetting() {
        if (haveSupporter()) {
            return this;
        }
        else {
            BooleanSetting op = getOpposite();
            return op.haveSupporter() ? op : null;
        }
    }

    public boolean supportable() {
        return !getOpposite().haveSupporter();
    }

    public boolean setSupport(Supporter newSupporter) {
        Preconditions.checkState(supportable());
        if (!haveSupporter()) {
            supporter = newSupporter;
            Preconditions.checkState(newSupporter.addSupported(this));
            return true;
        }
        else {
            return false;
        }
    }

    public boolean removeSupport() {
        Supporter osupporter = supporter;
        supporter = null;
        if (osupporter != null) {
            osupporter.removeSupported(this);
        }
        return osupporter != null;
    }

    public Set<BSRule> getRules() {
        return rules;
    }

    @Override
    public void announceSet(DeduceQueue deduceQueue) throws ContradictionException {
        Preconditions.checkState(haveSupporter());
        BooleanSetting op = getOpposite();
        for (BSRule r : op.getRules()) {
            r.recordSet(op, false, deduceQueue);
        }
        for (BSRule r : getRules()) {
            r.recordSet(this, true, deduceQueue);
        }
    }

    @Override
    public void announceUnset(RetractQueue retractQueue, Object stopAt) {
        Preconditions.checkState(!haveSupporter());
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
