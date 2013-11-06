
package latin.setting;

import java.util.List;

public class StringValuesSpec extends AbstractValuesSpec<String> {
    public final List<String> values;
    public StringValuesSpec(List<String> values) {
        this.values = values;
    }
    public String getIndexValue(int i) {
        return values.get(i);
    }
    public int getValueCount() {
        return values.size();
    }
    public int getValueIndex(String v) {
        return values.indexOf(v);
    }
}