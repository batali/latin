
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

public class BinaryChoiceNode<T> implements Node<T>, ChoiceSettings, BooleanSettings {

    public abstract class BinarySetting extends BooleanSetting implements Setter<T> {
        T value;
        public T setterValue() {
            return value;
        }
        public BinarySetting(T value) {
            this.value = value;
        }
        public String toString() {
            return pathString + "=" + value.toString();
        }
    }

    public List<String> allChoiceNames() {
        return Lists.newArrayList(falseSetting.value.toString(), trueSetting.value.toString());
    }

    public int setterCount() {
        return 2;
    }

    public BinarySetting getIndexSetter(int i) {
        Preconditions.checkElementIndex(i, 2);
        return (i == 0)? falseSetting : trueSetting;
    }

    public final BinarySetting falseSetting;
    public final BinarySetting trueSetting;
    public final String pathString;

    public String getPathString() {
        return pathString;
    }

    public String toString() {
        return pathString;
    }

    public BinaryChoiceNode(String pathString, T falseValue, T trueValue) {
        this.pathString = pathString;
        this.falseSetting = new BinarySetting(falseValue) {
            @Override
            public boolean booleanValue() {
                return false;
            }
            public BinarySetting getOpposite() {
                return trueSetting;
            }
        };
        this.trueSetting = new BinarySetting(trueValue) {
            public boolean booleanValue() {
                return true;
            }
            public BinarySetting getOpposite() {
                return falseSetting;
            }
        };
    }

    public BinaryChoiceNode(T falseValue, T trueValue) {
        this("", falseValue, trueValue);
    }

    public BinaryChoiceNode(String pathString, List<T> values) {
        this(pathString, values.get(0), values.get(1));
        Preconditions.checkArgument(values.size() == 2);
    }

    public BinarySetting getValueSetter(T v) {
        if (v.equals(falseSetting.value)) {
            return falseSetting;
        }
        else if (v.equals(trueSetting.value)) {
            return trueSetting;
        }
        else {
            return null;
        }
    }

    public BooleanSetting getSupportedSetting() {
        return getSupportedSetter();
    }

    public BinarySetting getSupportedSetter() {
        if (trueSetting.supporter != null) {
            return trueSetting;
        }
        else if (falseSetting.supporter != null) {
            return falseSetting;
        }
        else {
            return null;
        }
    }

    public BinarySetting getChoiceSetting(String cn, boolean sv) {
        if (trueSetting.value.toString().equalsIgnoreCase(cn)) {
            return sv ? trueSetting : falseSetting;
        }
        else if (falseSetting.value.toString().equalsIgnoreCase(cn)) {
            return sv ? falseSetting : trueSetting;
        }
        return null;
    }

    public BinarySetting getBooleanSetting(boolean sv) {
        return sv ? trueSetting : falseSetting;
    }
}
