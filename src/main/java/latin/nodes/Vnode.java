
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class Vnode<T> extends AbstractDisjunctionRule implements Node<T>, ChoiceSettings {

    public final String pathString;
    public final List<ValueProp> valueProps;
    private @Nullable ValueProp setProp;
    private Set<Supported> supportedSet;
    int supportMode;

    public Vnode<T> getVnode() {
        return this;
    }

    public class ValueProp extends Prop {

        public final T value;

        public ValueProp (T value) {
            super();
            this.value = value;
        }

        public boolean setSetProp() {
            if (setProp == null) {
                setProp = this;
            }
            return true;
        }

        public String getSettingString(boolean sv) {
            String ops = sv ? "=" : "!=";
            return pathString + ops + value.toString();
        }

        public boolean unsetSetProp() {
            if (Objects.equal(setProp, this)) {
                setProp = null;
            }
            return true;
        }

        public boolean whenSet(Setting setting) {
            return super.whenSet(setting) && (
                    !setting.booleanValue() || setSetProp());
        }

        public boolean whenUnset(Setting setting) {
            return super.whenUnset(setting) && (
                    !setting.booleanValue() || unsetSetProp());
        }

        public void whenSetAnnounced(Setting setting, DeduceQueue deduceQueue) throws ContradictionException {
            recordSet(trueSetting, setting.booleanValue(), deduceQueue);
            super.whenSetAnnounced(setting, deduceQueue);
        }

        public boolean whenUnsetAnnounced(Setting setting, RetractQueue retractQueue, Object stopAt) {
            recordUnset(trueSetting, setting.booleanValue(), retractQueue);
            return (super.whenUnsetAnnounced(setting, retractQueue, stopAt) && !Objects.equal(getVnode(), stopAt));
        }
    }

    Vnode(String pathString, List<T> values) {
        super(values.size());
        this.pathString = pathString;
        this.setProp = null;
        this.supportedSet = Sets.newHashSet();
        this.supportMode = 0;
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
        supportMode = 0;
    }

    public Supported peekSupported() {
        if (supportedSet.isEmpty()) {
            return null;
        }
        else {
            return supportedSet.iterator().next();
        }
    }

    @Override
    public boolean addSupported(Supported supported) {
        return supportedSet.add(supported);
    }

    @Override
    public boolean removeSupported(Supported supported) {
        return supportedSet.remove(supported);
    }

    @Override
    public boolean doesSupport() {
        return !supportedSet.isEmpty();
    }

    public void deduce(DeduceQueue dq) throws ContradictionException {
        if (trueCount > 1) {
            throw new ContradictionException("Vnode[" + toString() + "]", this);
        }
        else if (trueCount == 1) {
            if (supportMode >= 0 && falseCount + 1 < settingCount) {
                for (ValueProp valueProp : valueProps) {
                    if (valueProp.getSupportedSetting() == null) {
                        if (dq.setSupport(valueProp.falseSetting, this)) {
                            supportMode = 1;
                        }
                    }
                }
            }
        }
        else {
            if (supportMode == 0) {
                super.deduce(dq);
                if (!supportedSet.isEmpty()) {
                    supportMode = -1;
                }
            }
        }
    }

    public boolean deduceCheck() {
        return ((trueCount >= 1 && falseCount + 1 <= settingCount) ||
                (trueCount == 0 && falseCount + 1 >= settingCount));
    }

    @Override
    public void recordUnset(BooleanSetting setting, boolean sv, RetractQueue rq) {
        addCount(sv, -1);
        if ((sv && supportMode > 0) || (!sv && supportMode < 0)) {
            retractSupported(rq);
        }
        else if (deduceCheck()) {
            rq.addRededucer(this);
        }
    }

    @Override
    public SupportCollector collectSupport(SupportCollector supportCollector) {
        if (supportMode > 0) {
            for (ValueProp valueProp : valueProps) {
                supportCollector.recordSupporter(valueProp.trueSetting);
            }
        }
        else if (supportMode < 0) {
            return super.collectSupport(supportCollector);
        }
        return supportCollector;
    }


}
