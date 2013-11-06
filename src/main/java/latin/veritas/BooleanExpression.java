
package latin.veritas;

public class BooleanExpression extends AtomicExpression {

    public BooleanExpression(String pathString) {
        super(pathString);
    }

    @Override
    public String getValueString() {
        return "T";
    }

    public <T> T getSetting (boolean sv, Psetting.GetSetting<T> handler) {
        return handler.getBooleanSetting(pathString, sv);
    }

    @Override
    public String getSettingString(boolean sv) {
        return sv ? pathString : "!" + pathString;
    }

}