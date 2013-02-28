
package latin.slots;

public class BooleanExpression extends AtomicExpression {

    public BooleanExpression(String pathString) {
        super(pathString);
    }

    @Override
    public boolean meval(MevalEnvironment menv) {
        return menv.evalSlot(pathString, "T");
    }

    public String toString() {
        return pathString;
    }

    public <T> T applyOn(boolean sv, SettingHandler<T> bettingHandler) throws SettingSpecException {
        return bettingHandler.getBooleanSetting(pathString, sv);
    }

}