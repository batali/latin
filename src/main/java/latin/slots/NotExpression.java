
package latin.slots;

import java.util.Arrays;
import java.util.List;

public class NotExpression implements PropExpression {

    private PropExpression subExpression;

    public NotExpression (PropExpression subExpression) {
        this.subExpression = subExpression;
    }

    @Override
    public boolean meval(MevalEnvironment menv) {
        return !subExpression.meval(menv);
    }

    @Override
    public String toString() {
        return "!" + subExpression.toString();
    }

    @Override
    public String asList() {
        String [] sl = { "!", subExpression.asList() };
        return Arrays.toString(sl);
    }

    @Override
    public List<List<ISetting>> getCnf(boolean bv, SettingHandler<ISetting> settingHandler) throws SettingSpecException {
        return subExpression.getCnf(!bv, settingHandler);
    }

}