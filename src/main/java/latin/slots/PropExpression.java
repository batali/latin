
package latin.slots;

import java.util.List;

public interface PropExpression {
    boolean meval(MevalEnvironment menv);
    public String asList();
    public List<List<ISetting>> getCnf(boolean bv, SettingHandler<ISetting> settingHandler) throws SettingSpecException;
}