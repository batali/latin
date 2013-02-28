
package latin.slots;

public interface PathHandler<T> {
    public T onBoolean(String pathString, boolean sv) throws SettingSpecException;
    public T onBinary(String pathString, String choiceName, boolean sv) throws SettingSpecException;
    public T onValue(String pathString, String choiceName, int index, boolean sv) throws SettingSpecException;
}