
package latin.nodes;

public class BooleanNode extends BooleanSetting
        implements Node<Boolean>, Setter<Boolean>, BooleanSettings {

    public BooleanNode getBooleanNode() {
        return this;
    }

    class FalseSetter extends BooleanSetting implements Setter<Boolean> {
        FalseSetter() {
            super();
        }
        public boolean booleanValue() {
            return false;
        }
        public BooleanNode getOpposite() {
            return getBooleanNode();
        }
        public Boolean setterValue() {
            return booleanValue();
        }
        public String toString() {
            return "!" + getPathString();
        }
    }

    private final FalseSetter falseSetting = new FalseSetter();

    public FalseSetter getOpposite() {
        return falseSetting;
    }

    public final String pathString;

    public BooleanNode(String pathString) {
        super();
        this.pathString = pathString;
    }

    public boolean booleanValue() {
        return true;
    }

    public BooleanNode() {
        this("");
    }

    public String getPathString() {
        return pathString;
    }

    public String toString() {
        return pathString;
    }

    public Boolean setterValue() {
        return booleanValue();
    }

    @Override
    public Setter<Boolean> getValueSetter(Boolean bv) {
        return bv ? this : falseSetting;
    }

    public BooleanSetting getBooleanSetting(boolean sv) {
        return sv ? this : falseSetting;
    }

    @Override
    public Setter<Boolean> getSupportedSetting() {
        if (supporter != null) {
            return this;
        }
        else if (falseSetting.supporter != null) {
            return falseSetting;
        }
        else {
            return null;
        }
    }

}
