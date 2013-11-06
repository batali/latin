
package latin.setting;

import java.util.List;

public class ListValuesSpec<T> extends AbstractValuesSpec<T> {
    List<T> valuesList;
    public ListValuesSpec(List<T> valuesList) {
        this.valuesList = valuesList;
    }
    public T getIndexValue(int i) {
        return valuesList.get(i);
    }
    public int getValueCount() {
        return valuesList.size();
    }
}