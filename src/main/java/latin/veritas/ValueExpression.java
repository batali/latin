
package latin.veritas;

public class ValueExpression extends AtomicExpression {

    public final String choiceName;

    public ValueExpression(String pathString, String choiceName) {
        super(pathString);
        this.choiceName = choiceName;
    }

    public String getSettingString(boolean pol) {
        String op = pol ? "=" : "!=";
        return pathString + op + choiceName;
    }

    @Override
    public String getValueString() {
        return choiceName;
    }

    public <T> T getSetting(boolean sv, Psetting.GetSetting<T>handler) {
        return handler.getValueSetting(pathString, choiceName, sv);
    }


}