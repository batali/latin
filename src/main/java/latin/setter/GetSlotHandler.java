
package latin.setter;

import latin.slots.SettingSpecException;

public interface GetSlotHandler {
    public BinarySlot getBinarySlot(String pathString) throws SettingSpecException;
    public BooleanSlot getBooleanSlot(String pathString) throws SettingSpecException;
    public ValueSlot getValueSlot(String pathString) throws SettingSpecException;
}