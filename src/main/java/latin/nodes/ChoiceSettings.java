
package latin.nodes;

import java.util.List;

public interface ChoiceSettings {
    public BooleanSetting getChoiceSetting(String choiceName, boolean sv);
    public List<String> allChoiceNames();
}
