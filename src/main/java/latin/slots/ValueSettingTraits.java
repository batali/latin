
package latin.slots;

import latin.setter.ISlot;
import latin.setter.UseSlotHandler;
import latin.setter.ValueSlot;

import java.util.List;

public class ValueSettingTraits extends SettingTraits {

    public static final ValueSettingTraits traits = new ValueSettingTraits();

    private ValueSettingTraits() {
    }

    @Override
    public int compareChoices(ISetting s1, ISetting s2) {
        return s1.getIndex() - s2.getIndex();
    }

    @Override
    public String settingString(ISetting s) {
        return valueSettingString(s.getPathString(), s.getChoiceName(), s.getValue());
    }

    @Override
    public <T> T applyOn(PathHandler<T> pathHandler, ISetting s) throws SettingSpecException {
        return pathHandler.onValue(s.getPathString(), s.getChoiceName(), s.getIndex(), s.getValue());
    }

    @Override
    public <T> T getBooleanSetting(PathHandler<T> pathHandler, String pathString, boolean sv, List<String> choiceNames)
            throws SettingSpecException {
        throw new SettingSpecException("No boolean setting from value");
    }

    @Override
    public <T> T getValueSetting(PathHandler<T> pathHandler, String pathString, String choiceName, boolean sv, List<String> choiceNames)
            throws SettingSpecException {
        int ci = findChoiceIndex(choiceName, choiceNames, true);
        return pathHandler.onValue(pathString, choiceNames.get(ci), ci, sv);
    }

    public <T> T useSettingSpec(UseSettingSpecHandler<T> handler, String pathString, SlotSettingSpec slotSettingSpec) {
        return handler.useValueSettingSpec(pathString, slotSettingSpec);
    }

    public <T> T useSlot(UseSlotHandler<T> handler, ISlot iSlot) throws SettingSpecException {
        return handler.useValueSlot(castSlot(iSlot, ValueSlot.class));
    }

}


