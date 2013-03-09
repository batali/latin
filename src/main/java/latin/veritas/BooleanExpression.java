
package latin.veritas;

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

    public <T> T getSetting (boolean sv, Psetting.GetSetting<T> handler) {
        return handler.getBooleanSetting(pathString, sv);
    }
}