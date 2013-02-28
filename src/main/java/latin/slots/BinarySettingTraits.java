
package latin.slots;

import latin.setter.BinarySlot;
import latin.setter.ISlot;
import latin.setter.UseSlotHandler;

import java.util.List;

public class BinarySettingTraits extends SettingTraits {

    public static final BinarySettingTraits traits = new BinarySettingTraits();

    private BinarySettingTraits () {
    }

    @Override
    public String settingString(ISetting s) {
        return ValueSettingTraits.valueSettingString(s.getPathString(), s.getChoiceName(), true);
    }

    @Override
    public <T> T applyOn(PathHandler<T> pathHandler, ISetting s) throws SettingSpecException {
        return pathHandler.onBinary(s.getPathString(), s.getChoiceName(), s.getValue());
    }

    @Override
    public <T> T getBooleanSetting(PathHandler<T> pathHandler, String pathString, boolean sv, List<String> choiceNames)
            throws SettingSpecException {
        return pathHandler.onBinary(pathString, choiceNames.get(booleanChoiceIndex(sv)), sv);
    }

    @Override
    public <T> T getValueSetting(PathHandler<T> pathHandler, String pathString, String choiceName, boolean sv, List<String> choiceNames)
            throws SettingSpecException {
        return getBooleanSetting(pathHandler, pathString, binarySettingValue(choiceName, sv, choiceNames), choiceNames);
    }

    public <T> T useSettingSpec(UseSettingSpecHandler<T> handler, String pathString, SlotSettingSpec slotSettingSpec) {
        return handler.useBinarySettingSpec(pathString, slotSettingSpec);
    }

    public <T> T useSlot(UseSlotHandler<T> handler, ISlot iSlot) throws SettingSpecException {
        return handler.useBinarySlot(castSlot(iSlot, BinarySlot.class));
    }
}
