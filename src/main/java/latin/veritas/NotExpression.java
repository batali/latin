
package latin.veritas;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    @Override
    public void collectPaths(Set<String> pathStrings) {
        subExpression.collectPaths(pathStrings);
    }

    public String prettyPrint(boolean top) {
        PropExpression pe = this;
        StringBuilder sb = new StringBuilder();
        while(pe instanceof NotExpression) {
            sb.append("!");
            pe = ((NotExpression)pe).subExpression;
        }
        sb.append(pe.prettyPrint(false));
        return sb.toString();
    }

}