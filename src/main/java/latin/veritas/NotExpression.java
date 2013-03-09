
package latin.veritas;

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
    public List<List<Psetting>> getCnf(boolean bv, Psetting.Handler handler) {
        return subExpression.getCnf(!bv, handler);
    }
}