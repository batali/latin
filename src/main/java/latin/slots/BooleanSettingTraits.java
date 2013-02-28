
package latin.slots;

import com.google.common.collect.Lists;
import latin.setter.BooleanSlot;
import latin.setter.ISlot;
import latin.setter.UseSlotHandler;

import java.util.List;

public class BooleanSettingTraits extends SettingTraits {

    public static final List<String> booleanChoiceNames = Lists.newArrayList("F", "T");

    public static final BooleanSettingTraits traits = new BooleanSettingTraits();

    private BooleanSettingTraits() {
    }

    @Override
    public String settingString(ISetting s) {
        return s.getValue() ? s.getPathString() : "!" + s.getPathString();
    }

    @Override
    public <T> T applyOn(PathHandler<T> pathHandler, ISetting s) throws SettingSpecException {
        return pathHandler.onBoolean(s.getPathString(), s.getValue());
    }

    @Override
    public <T> T getBooleanSetting(PathHandler<T> pathHandler, String pathString, boolean sv, List<String> choiceNames)
            throws SettingSpecException {
        return pathHandler.onBoolean(pathString, sv);
    }

    @Override
    public <T> T getValueSetting(PathHandler<T> pathHandler, String pathString, String choiceName, boolean sv, List<String> choiceNames)
            throws SettingSpecException {
        return pathHandler.onBoolean(pathString, binarySettingValue(choiceName, sv, choiceNames));
    }

    public static final SlotSettingSpec booleanSpec = new SlotSettingSpec(booleanChoiceNames, traits);

    public <T> T useSettingSpec(UseSettingSpecHandler<T> handler, String pathString, SlotSettingSpec slotSettingSpec) {
        return handler.useBooleanSettingSpec(pathString, slotSettingSpec);
    }

    public <T> T useSlot(UseSlotHandler<T> handler, ISlot iSlot) throws SettingSpecException {
        return handler.useBooleanSlot(castSlot(iSlot, BooleanSlot.class));
    }
}