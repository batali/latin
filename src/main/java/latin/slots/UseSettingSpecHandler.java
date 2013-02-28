
package latin.slots;

public interface UseSettingSpecHandler<T> {
    public T useBooleanSettingSpec(String pathString, SlotSettingSpec slotSettingSpec);
    public T useBinarySettingSpec(String pathString, SlotSettingSpec slotSettingSpec);
    public T useValueSettingSpec(String pathString, SlotSettingSpec slotSettingSpec);
}