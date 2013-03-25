
package latin.veritas;

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

    public abstract <T> T getSetting(boolean sv, Psetting.GetSetting<T> handler);

    @Override
    public List<List<Psetting>> getCnf(boolean sv, Psetting.Handler handler) {
        return Psetting.singletonCnf(getSetting(sv, handler));
    }

    public String prettyPrint(boolean top) {
        return toString();
    }

}