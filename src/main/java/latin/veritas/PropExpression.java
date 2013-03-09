
package latin.veritas;

import java.util.List;

public interface PropExpression {
    boolean meval(MevalEnvironment menv);
    public String asList();
    public List<List<Psetting>> getCnf(boolean bv, Psetting.Handler handler);
}