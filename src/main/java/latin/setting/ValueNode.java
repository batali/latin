
package latin.setting;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

public class ValueNode<T> implements Node<T> {
    String nodeName;
    ValuesSpec<T> valuesSpec;
    Settings settings;
    public ValueNode(String nodeName, ValuesSpec<T> valuesSpec) {
        this.nodeName = nodeName;
        this.valuesSpec = valuesSpec;
        if (valuesSpec.getValueCount() == 2) {
            this.settings = new BinarySettings(nodeName, valuesSpec);
        }
        else {
            this.settings = new MultiSettings(nodeName, valuesSpec);
        }
    }

    @Override
    public String getName() {
        return nodeName;
    }

    @Override
    public ValuesSpec<T> getValuesSpec() {
        return valuesSpec;
    }

    @Override
    public Setting getValueSetting(T value, boolean polarity) {
        int i = valuesSpec.getValueIndex(value);
        return getSetting(i, polarity);
    }

    @Override
    public Setting getValueSetting(T value) {
        return getValueSetting(value, true);
    }

    @Override
    public Setting getSetting(int index, boolean polarity) {
        Setting s = settings.getSetting(index);
        return polarity ? s : s.getOpposite();
    }

    @Override
    public T getSupportedValue() {
        Setting ss = settings.getSupportedSetting();
        Preconditions.checkNotNull(ss);
        return valuesSpec.getIndexValue(ss.getValueIndex());
    }

    @Override
    public boolean haveSupportedValue() {
        return settings.getSupportedSetting() != null;
    }

    @Override
    public int getSettingCount() {
        return settings.getSettingCount();
    }

    @Override
    public Setting getSetting(int index) {
        return settings.getSetting(index);
    }

    @Override
    public Setting getSupportedSetting() {
        return settings.getSupportedSetting();
    }

    @Override
    public boolean addValueRule(Setting.Rule rule) {
        return settings.addValueRule(rule);
    }

    @Override
    public boolean removeValueRule(Setting.Rule rule) {
        return settings.removeValueRule(rule);
    }

    public List<String> getAvailableValueStrings() {
        List<String> available = Lists.newArrayList();
        for (int p = 0; p < getSettingCount(); p++) {
            Setting s = getSetting(p);
            if (s.isSatisfiable()) {
                available.add(valuesSpec.getIndexString(p));
            }
        }
        return available;
    }

}
