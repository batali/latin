
package latin.setting;

public abstract class AbstractValuesSpec<T> implements ValuesSpec<T> {
    public String getSettingString(String nodeName, int valueIndex, boolean polarity) {
        String op = polarity ? "=" : "!=";
        return nodeName + op + getIndexString(valueIndex);
    }
    public String getIndexString(int i) {
        return getIndexValue(i).toString();
    }
    public int findStringIndex(String valueName) {
        for (int i = 0; i < getValueCount(); i++) {
            if (getIndexString(i).equalsIgnoreCase(valueName)) {
                return i;
            }
        }
        return -1;
    }
    public int getValueIndex(T value) {
        for (int i = 0; i < getValueCount(); i++) {
            if (getIndexValue(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
