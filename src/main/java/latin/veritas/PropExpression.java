
package latin.veritas;

import java.util.List;
import java.util.Set;

public interface PropExpression {
    boolean meval(MevalEnvironment menv);
    void collectPaths(Set<String> pathStrings);
    public String asList();
    public List<List<Psetting>> getCnf(boolean bv, Psetting.Handler handler);
    public String prettyPrint(boolean top);
}