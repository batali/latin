
package latin.veritas;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AtomicExpression implements PropExpression {

    public final String pathString;

    public AtomicExpression(String pathString) {
        this.pathString = pathString;
    }

    @Override
    public void collectPaths(Set<String> pathStrings) {
        pathStrings.add(pathString);
    }

    @Override
    public String asList() {
        return toString();
    }

    public abstract String getValueString();

    public abstract <T> T getSetting(boolean sv, Psetting.GetSetting<T> handler);

    public abstract String getSettingString(boolean pol);

    public String toString() {
        return getSettingString(true);
    }

    @Override
    public boolean meval(MevalEnvironment menv) {
        return menv.evalSlot(pathString, getValueString());
    }

    @Override
    public List<List<Psetting>> getCnf(boolean sv, Psetting.Handler handler) {
        return Psetting.singletonCnf(getSetting(sv, handler));
    }

    public SettingSpec getSetting(boolean bv) {
        return new AtomicExpressionSetting(this, bv);
    }

    public List<List<SettingSpec>> getCnf(boolean bv) {
        return Collections.singletonList(Collections.singletonList(getSetting(bv)));
    }

    public String prettyPrint(boolean top) {
        return toString();
    }

    @Override
    public int weight() {
        return 1;
    }

    static class AtomicExpressionSetting implements SettingSpec {
        final AtomicExpression atomicExpression;
        final boolean polarity;
        public AtomicExpressionSetting(AtomicExpression atomicExpression, boolean polarity) {
            this.atomicExpression = atomicExpression;
            this.polarity = polarity;
        }
        public String getPathString() {
            return atomicExpression.pathString;
        }
        public String getValueString() {
            return atomicExpression.getValueString();
        }
        public boolean getPolarity() {
            return polarity;
        }
        public AtomicExpressionSetting getOpposite() {
            return new AtomicExpressionSetting(atomicExpression, !polarity);
        }
        public String toString() {
            return atomicExpression.getSettingString(polarity);
        }
    }
}