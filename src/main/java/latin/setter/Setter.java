package latin.setter;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import latin.slots.ISetting;
import latin.slots.SettingTraits;
import latin.slots.SlotSettingSpec;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class Setter implements ISetting {

    private @Nullable Supporter supporter;
    private Set<DisjunctionRule> rules;

    public Setter () {
        this.supporter = null;
        this.rules = Sets.newHashSet();
    }

    public abstract ISlot getSlot();
    public abstract Setter getOpposite();

    public Setter asSetter() {
        return this;
    }

    public SlotSettingSpec getSlotSettingSpec() {
        return getSlot().getSlotSettingSpec();
    }

    @Override
    public SettingTraits getTraits() {
        return getSlotSettingSpec().getTraits();
    }

    @Override
    public String getPathString() {
        return getSlot().getPathString();
    }

    public @Nullable Supporter getSupporter () {
        return supporter;
    }

    public boolean haveSupporter() {
        return supporter != null;
    }

    public void setSupporter(Supporter newSupporter) {
        Preconditions.checkState(getStatus() == 0);
        supporter = newSupporter;
    }

    public Supporter removeSupporter() {
        Preconditions.checkState(getStatus() > 0);
        Supporter osupporter = supporter;
        supporter = null;
        return osupporter;
    }

    public void addRule(DisjunctionRule rule) {
        rules.add(rule);
    }

    public Supporter getPairSupporter() {
        if (supporter != null) {
            return supporter;
        }
        else {
            return getOpposite().getSupporter();
        }
    }

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

    public String toString() {
        return getTraits().settingString(this);
    }

    public void announceSupported(Propagator propagator) throws ContradictionException {
        for (DisjunctionRule trule : rules) {
            trule.recordSupported(this, true, propagator);
        }
        for (DisjunctionRule frule : getOpposite().rules) {
            frule.recordSupported(this, false, propagator);
        }
    }

    public void announceRetracted(Propagator propagator, SupportRule stopAfter) {
        for (DisjunctionRule trule : rules) {
            trule.recordRetracted(this, true, propagator);
            if (Objects.equal(trule, stopAfter)) {
                return;
            }
        }
        for (DisjunctionRule frule : getOpposite().rules) {
            frule.recordRetracted(this, false, propagator);
            if (Objects.equal(frule, stopAfter)) {
                return;
            }
        }
    }

    public Setter getValueSetter() {
        int st = getStatus();
        if (st >= 0) {
            return this;
        }
        else {
            return getOpposite();
        }
    }

}