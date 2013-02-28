
package latin.slots;

import java.util.List;

public abstract class AtomicExpression implements PropExpression {

    public final String pathString;

    public AtomicExpression(String pathString) {
        this.pathString = pathString;
    }

    @Override
    public String asList() {
        return toString();
    }

    public abstract <T> T applyOn(boolean sv, SettingHandler<T> bettingHandler) throws SettingSpecException;

    @Override
    public List<List<ISetting>> getCnf(boolean sv, SettingHandler<ISetting> settingHandler) throws SettingSpecException {
        return NormalForm.singletonCnf(applyOn(sv, settingHandler));
    }

}