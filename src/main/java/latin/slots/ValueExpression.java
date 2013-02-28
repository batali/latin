
package latin.slots;

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

    public <T> T applyOn(boolean sv, SettingHandler<T> bettingHandler) throws SettingSpecException {
        return bettingHandler.getValueSetting(pathString, choiceName, sv);
    }

}