
package latin.setting;

public interface Node<T> extends Settings {
    public String getName();
    public ValuesSpec<T> getValuesSpec();
    public Setting getSetting(int index, boolean polarity);
    public T getSupportedValue();
    public boolean haveSupportedValue();
    public Setting getValueSetting(T value);
    public Setting getValueSetting(T value, boolean polarity);
}