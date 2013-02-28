
package latin.setter;

import latin.slots.SettingSpecException;
import latin.slots.SlotSettingSpec;

public interface ISlot {
    public SlotSettingSpec getSlotSettingSpec();
    public String getPathString();
    public boolean haveValueSetter();
    public Setter getValueSetter();
    public Setter getSetter(String choiceName, boolean sv) throws SettingSpecException;
}