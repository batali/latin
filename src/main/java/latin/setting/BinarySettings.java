
package latin.setting;

import com.google.common.base.Preconditions;

public class BinarySettings implements Settings {

    static class FalseSetting extends AbstractSetting {
        public int getValueIndex() {
            return 0;
        }
        public FalseSetting(String nodeName, ValuesSpec<?> valuesSpec, AbstractSetting opposite) {
            super(valuesSpec.getSettingString(nodeName, 0, true), opposite);
        }
    }

    static class TrueSetting extends AbstractSetting {
        public int getValueIndex() {
            return 1;
        }
        public TrueSetting(String nodeName, ValuesSpec<?> valuesSpec) {
            super(valuesSpec.getSettingString(nodeName, 1, true), null);
            this.opposite = new FalseSetting(nodeName, valuesSpec, this);
        }
    }

    TrueSetting trueSetting;

    public BinarySettings(String nodeName, ValuesSpec<?> valuesSpec) {
        Preconditions.checkArgument(valuesSpec.getValueCount()==2);
        this.trueSetting = new TrueSetting(nodeName, valuesSpec);
    }

    @Override
    public int getSettingCount() {
        return 2;
    }

    @Override
    public Setting getSetting(int index) {
        return index==1 ? trueSetting : trueSetting.opposite;
    }

    @Override
    public Setting getSupportedSetting() {
        return trueSetting.getSupported();
    }

    @Override
    public boolean addValueRule(Setting.Rule r) {
        return trueSetting.addRule(r);
    }

    @Override
    public boolean removeValueRule(Setting.Rule r) {
        return trueSetting.removeRule(r);
    }

}

