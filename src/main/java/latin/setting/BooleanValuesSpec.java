
package latin.setting;

public class BooleanValuesSpec extends AbstractValuesSpec<Boolean> {
    public static final BooleanValuesSpec spec = new BooleanValuesSpec();
    public int getValueCount() {
        return 2;
    }
    public static boolean indexBoolean(int index) {
        return index==1;
    }
    public static int booleanIndex(boolean b) {
        return b ? 1 : 0;
    }
    public Boolean getIndexValue(int index) {
        return indexBoolean(index);
    }
    public int getValueIndex(Boolean b) {
        return booleanIndex(b);
    }
    public String getIndexString(int index) {
        return indexBoolean(index) ? "T" : "F";
    }
    String getSettingString(String nodeName, boolean bv) {
        return bv ? nodeName : "!" + nodeName;
    }
    public String getSettingString(String nodeName, int valueIndex, boolean polarity) {
        return getSettingString(nodeName, (valueIndex==booleanIndex(polarity)));
    }
}