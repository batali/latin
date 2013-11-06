
package latin.setting;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class MultiSettings extends AbstractDisjunctionRule implements Settings {

    List<TrueSetting> settingList;
    TrueSetting supportedSetting;
    int supportedCount;
    Set<Setting.Rule> valueRules;

    boolean setSupportedSetting(TrueSetting setting) {
        Preconditions.checkState(supportedSetting == null);
        supportedSetting = setting;
        return true;
    }

    boolean removeSupportedSetting(TrueSetting setting) {
        Preconditions.checkState(Objects.equal(supportedSetting, setting));
        supportedSetting = null;
        return true;
    }

    Setting.Rule getRule() {
        return this;
    }

    static class FalseSetting extends AbstractSetting {
        public int getValueIndex() {
            return opposite.getValueIndex();
        }
        public boolean getPolarity() {
            return false;
        }
        public FalseSetting(String nodeName, ValuesSpec<?> valuesSpec, AbstractSetting opposite) {
            super(valuesSpec.getSettingString(nodeName, opposite.getValueIndex(), false), opposite);
        }
    }

    class TrueSetting extends AbstractSetting {
        int valueIndex;
        public TrueSetting(String nodeName, ValuesSpec<?> valuesSpec, int valueIndex) {
            super(valuesSpec.getSettingString(nodeName, valueIndex, true), null);
            this.valueIndex = valueIndex;
            this.opposite = new FalseSetting(nodeName, valuesSpec, this);
        }
        public int getValueIndex() {
            return valueIndex;
        }
        public boolean isSatisfiable() {
            return isSupported() || (!opposite.isSupported() && supportedSetting == null);
        }
        public boolean setSupport(Supporter supporter, Propagator propagator) {
            return super.setSupport(supporter, propagator) && setSupportedSetting(this);
        }
        public boolean removeSupport(Retractor retractor) {
            return super.removeSupport(retractor) && removeSupportedSetting(this);
        }
        public boolean announceValueSet(boolean bv, Propagator propagator) {
            return (super.announceValueSet(bv, propagator) &&
                    announceSetToRule(bv, getRule(), propagator) &&
                    announceSetToRules(bv, valueRules, propagator));
        }
        public boolean announceValueUnset(boolean bv, Retractor retractor) {
            return (super.announceValueUnset(bv, retractor) &&
                    announceUnsetToRule(bv, getRule(), retractor) &&
                    announceUnsetToRules(bv, valueRules, retractor));
        }
    }

    public MultiSettings(String nodeName, ValuesSpec<?> valuesSpec) {
        super(valuesSpec.getValueCount());
        this.settingList = Lists.newArrayList();
        this.supportedSetting = null;
        this.supportedCount = 0;
        for (int i = 0; i < settingCount; i++) {
            settingList.add(new TrueSetting(nodeName, valuesSpec, i));
        }
        this.valueRules = Sets.newHashSet();
    }

    @Override
    public int getSettingCount() {
        return settingList.size();
    }

    @Override
    public TrueSetting getSetting(int index) {
        return settingList.get(index);
    }

    @Override
    public boolean supportingAny() {
        return supportedCount > 0;
    }

    @Override
    public Setting getSupportedSetting() {
        return supportedSetting;
    }

    @Override
    public boolean addSupported(Supportable s) {
        supportedCount += 1;
        return true;
    }

    @Override
    public boolean removeSupported(Supportable s) {
        supportedCount -= 1;
        return true;
    }

    @Override
    public boolean deduceCheck() {
        if (trueCount == 0) {
            return disjunctionDeduceCheck();
        }
        else {
            return falseCount + 1 < settingCount;
        }
    }

    @Override
    public boolean deduce(Propagator propagator) {
        if (trueCount == 0) {
            return disjunctionDeduce(settingList, propagator);
        }
        else {
            Preconditions.checkState(supportedSetting != null);
            for (TrueSetting s : settingList) {
                if (s.isSupported()) {
                    if (s != supportedSetting) {
                        propagator.recordContradictionRule(this);
                        return false;
                    }
                }
                else if (!s.opposite.isSupported()) {
                    s.opposite.setSupport(this, propagator);
                }
            }
            return true;
        }
    }

    @Override
    public boolean retractCheck() {
        return trueCount == 0 || supporting(supportedSetting);
    }

    public boolean retract(Retractor retractor) {
        if (trueCount == 0) {
            for (TrueSetting s : settingList) {
                Setting op = s.opposite;
                if (supporting(op)) {
                    op.removeSupport(retractor);
                }
            }
        }
        else if (supporting(supportedSetting)) {
            supportedSetting.removeSupport(retractor);
        }
        return true;
    }

    public boolean addValueRule(Setting.Rule rule) {
        return valueRules.add(rule);
    }

    public boolean removeValueRule(Setting.Rule rule) {
        return valueRules.remove(rule);
    }
}
