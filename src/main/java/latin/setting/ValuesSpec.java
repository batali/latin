
package latin.setting;

public interface ValuesSpec<T> {
    public int getValueCount();
    public T getIndexValue(int index);
    public int getValueIndex(T t);
    public String getIndexString(int index);
    public String getSettingString(String nodeName, int index, boolean polarity);
    public int findStringIndex(String valueName);
}

