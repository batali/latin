
package latin.veritas;

public class ValueExpression extends AtomicExpression {

    public final String choiceName;

    public ValueExpression(String pathString, String choiceName) {
        super(pathString);
        this.choiceName = choiceName;
    }

    @Override
    public boolean meval(MevalEnvironment menv) {
        return menv.evalSlot(pathString, choiceName);
    }

    public String toString() {
        return pathString + "=" + choiceName;
    }

    public <T> T getSetting(boolean sv, Psetting.GetSetting<T>handler) {
        return handler.getValueSetting(pathString, choiceName, sv);
    }
}