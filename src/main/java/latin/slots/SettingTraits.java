
package latin.slots;

import com.google.common.base.Preconditions;
import latin.setter.ISlot;

import java.util.List;

public abstract class SettingTraits {

    public abstract String settingString(ISetting s);
    public abstract <T> T applyOn(PathHandler<T> pathHandler, ISetting s) throws SettingSpecException;

    public int comparePathStrings(ISetting s1, ISetting s2) {
        return s1.getPathString().compareTo(s2.getPathString());
    }

    public int compareChoices(ISetting s1, ISetting s2) {
        return s1.getChoiceName().compareTo(s2.getChoiceName());
    }

    public int compareProps(ISetting s1, ISetting s2) {
        int d = comparePathStrings(s1, s2);
        if (d == 0) {
            d = compareChoices(s1, s2);
        }
        return d;
    }

    public static String valueSettingString(String pathString, String choiceName, boolean sv) {
        return pathString + (sv ? "=" : "!=") + choiceName;
    }

    public static int findChoiceIndex(String choiceName, List<String> choiceNames, boolean errorp) throws SettingSpecException {
        int i = choiceNames.indexOf(choiceName);
        if (i < 0 && errorp) {
            throw new SettingSpecException("Unfound choice name " + choiceName);
        }
        return i;
    }

    public static boolean binarySettingValue(String choiceName, boolean sv, List<String> choiceNames) throws SettingSpecException {
        int i = findChoiceIndex(choiceName, choiceNames, true);
        return (i==1)==sv;
    }

    public static int booleanChoiceIndex(boolean bv) {
        return bv ? 1 : 0;
    }

    public abstract <T> T getBooleanSetting(PathHandler<T> pathHandler, String pathString, boolean sv, List<String> choiceNames)
        throws SettingSpecException;

    public abstract <T> T getValueSetting(PathHandler<T> pathHandler, String pathString, String choiceName, boolean sv, List<String> choiceNames)
        throws SettingSpecException;

    public static SlotSettingSpec getValueSettingSpec(List<String> choiceNames) {
        int s = choiceNames.size();
        Preconditions.checkState(s >= 2);
        if (s == 2) {
            return new SlotSettingSpec(choiceNames, BinarySettingTraits.traits);
        }
        else {
            return new SlotSettingSpec(choiceNames, ValueSettingTraits.traits);
        }
    }

    public abstract <T> T useSettingSpec(UseSettingSpecHandler<T> handler, String pathString, SlotSettingSpec slotSettingSpec);

    public static <T extends ISlot> T castSlot(ISlot slot, Class<T> cls) throws SettingSpecException {
        if (!cls.isInstance(slot)) {
            throw new SettingSpecException("wrong class");
        }
        return cls.cast(slot);
    }
}

