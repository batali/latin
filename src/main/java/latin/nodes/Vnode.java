
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class Vnode<T> extends AbstractDisjunctionRule implements Node<T>, ChoiceSettings {

    public final String pathString;
    public final List<ValueProp> valueProps;
    private @Nullable ValueProp setProp;
    private Set<BooleanSetting> supportedSet;

    public Vnode<T> getVnode() {
        return this;
    }

    public class ValueProp extends Prop {

        public final T value;

        public ValueProp (T value) {
            super();
            this.value = value;
        }

        public boolean setSetProp(Setting setting) {
            if (setting.value && setProp == null) {
                setProp = this;
            }
            return true;
        }

        public String getSettingString(boolean sv) {
            String ops = sv ? "=" : "!=";
            return pathString + ops + value.toString();
        }

        public boolean unsetSetProp(Setting setting) {
            if (setting.value && Objects.equal(setProp, this)) {
                setProp = null;
            }
            return true;
        }

        @Override
        public boolean whenSet(Setting setting) {
            return super.whenSet(setting) && setSetProp(setting);
        }

        public boolean whenUnset(Setting setting) {
            return super.whenUnset(setting) && unsetSetProp(setting);
        }

        @Override
        public BSRule getRule() {
            return getVnode();
        }

    }

    Vnode(String pathString, List<T> values) {
        super(values.size());
        this.pathString = pathString;
        this.setProp = null;
        this.supportedSet = Sets.newHashSet();
        this.valueProps = Lists.newArrayList();
        for (T t : values) {
            valueProps.add(new ValueProp(t));
        }
    }

    public String toString() {
        return pathString;
    }

    public int getChoiceIndex(String choiceName) {
        int s = valueProps.size();
        for (int i = 0; i < s; i++) {
            if (valueProps.get(i).value.toString().equalsIgnoreCase(choiceName)) {
                return i;
            }
        }
        return -1;
    }

    public boolean ISupportSetProp() {
        return setProp != null && setProp.trueSetting.supportedBy(getVnode());
    }

    @Override
    public BooleanSetting getChoiceSetting(String choiceName, boolean sv) {
        int i = getChoiceIndex(choiceName);
        ValueProp vp = valueProps.get(i);
        return vp.getBooleanSetting(sv);
    }

    @Override
    public String getPathString() {
        return pathString;
    }

    @Override
    public int setterCount() {
        return valueProps.size();
    }

    @Override
    public BooleanSetting getIndexSetting(int index) {
        return valueProps.get(index).trueSetting;
    }

    @Override
    public BooleanSetting getSupportedSetting() {
        return setProp != null ? setProp.trueSetting : null;
    }

    @Override
    public BooleanSetting getSetting(int p) {
        return getIndexSetting(p);
    }

    @Override
    public void retractSupported(RetractQueue rq) {
        Supported supported;
        while((supported = peekSupported()) != null) {
            rq.removeSupport(supported);
        }
    }

    public BooleanSetting peekSupported() {
        if (supportedSet.isEmpty()) {
            return null;
        }
        else {
            return supportedSet.iterator().next();
        }
    }

    public int supportedStatus() {
        BooleanSetting sst = peekSupported();
        if (sst == null) {
            return 0;
        }
        else {
            return sst.booleanValue() ? 1 : -1;
        }
    }

    @Override
    public boolean addSupported(Supported supported) {
        Preconditions.checkNotNull(supported);
        Preconditions.checkState(supported instanceof BooleanSetting);
        return supportedSet.add((BooleanSetting)supported);
    }

    @Override
    public boolean removeSupported(Supported supported) {
        Preconditions.checkNotNull(supported);
        Preconditions.checkState(supported instanceof BooleanSetting);
        return supportedSet.remove((BooleanSetting)supported);
    }

    @Override
    public boolean doesSupport() {
        return !supportedSet.isEmpty();
    }

    @Override
    public boolean deduce(DeduceQueue dq) throws ContradictionException {
        if (trueCount > 1) {
            Preconditions.checkState(!ISupportSetProp());
            throw new ContradictionException("Multiple True " + trueCount, this);
        }
        else if (trueCount == 1) {
            boolean rv = false;
            if (falseCount + 1 < settingCount) {
                Preconditions.checkState(!ISupportSetProp());
                for (ValueProp valueProp : valueProps) {
                    if (valueProp.falseSetting.supportable()) {
                        if (dq.setSupport(valueProp.falseSetting, this)) {
                            rv = true;
                        }
                    }

                }
            }
            return rv;
        }
        else {
            return super.deduce(dq);
        }
    }

    @Override
    public boolean undermines(BooleanSetting setting, boolean sv) {
        return sv || ISupportSetProp() || trueCount == 0;
    }

    @Override
    public boolean canRededuce(BooleanSetting setting, boolean sv) {
        if (sv) {
            return disjunctionDeduceTest();
        }
        else {
            return trueCount > 0;
        }
    }

    @Override
    public int getSupporterStatus() {
        return (trueCount == 0 || ISupportSetProp()) ? -1 : 1;
    }

}
