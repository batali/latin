
package latin.slots;

import java.util.List;

public class SlotSettingSpec {
    public final List<String> choiceNames;
    public final SettingTraits settingTraits;
    public SlotSettingSpec(List<String> choiceNames, SettingTraits settingTraits) {
        this.choiceNames = choiceNames;
        this.settingTraits = settingTraits;
    }
    public List<String> getChoiceNames() {
        return choiceNames;
    }
    public SettingTraits getTraits() {
        return settingTraits;
    }
}