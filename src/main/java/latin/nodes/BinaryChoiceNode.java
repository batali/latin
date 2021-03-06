
package latin.nodes;

import com.google.common.base.Preconditions;

import java.util.List;

public class BinaryChoiceNode<T> extends Prop implements Node<T>, ChoiceSettings, BooleanSettings {

    public final List<T> values;
    public final String pathString;

    public BinaryChoiceNode(String pathString, List<T> values) {
        super();
        this.values = values;
        this.pathString = pathString;
        Preconditions.checkArgument(values.size() == 2);
    }

    public String getPathString() {
        return pathString;
    }

    public String toString() {
        return pathString;
    }

    @Override
    public BooleanSetting getChoiceSetting(String choiceName, boolean sv) {
        if (values.get(0).toString().equalsIgnoreCase(choiceName)) {
            return getBooleanSetting(!sv);
        }
        else if (values.get(1).toString().equalsIgnoreCase(choiceName)) {
            return getBooleanSetting(sv);
        }
        else {
            return null;
        }
    }

    public String getSettingString(boolean sv) {
        return pathString + "=" + (sv ? values.get(1) : values.get(0)).toString();
    }

}
