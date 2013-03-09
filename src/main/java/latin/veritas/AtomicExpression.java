
package latin.veritas;

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

    public abstract <T> T getSetting(boolean sv, Psetting.GetSetting<T> handler);

    @Override
    public List<List<Psetting>> getCnf(boolean sv, Psetting.Handler handler) {
        return Psetting.singletonCnf(getSetting(sv, handler));
    }

}