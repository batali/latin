
package latin.slots;

public interface SettingHandler<T> {
    public T getValueSetting(String pathString, String choiceName, boolean sv) throws SettingSpecException;
    public T getBooleanSetting(String pathString, boolean sv) throws SettingSpecException;
}