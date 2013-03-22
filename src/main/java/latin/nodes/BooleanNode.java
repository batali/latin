
package latin.nodes;

public class BooleanNode extends Prop
        implements Node<Boolean>, BooleanSettings {

    public final String pathString;

    public BooleanNode(String pathString) {
        super();
        this.pathString = pathString;
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

    public String getSettingString(boolean sv) {
        if (sv) {
            return pathString;
        }
        else {
            return "!" + pathString;
        }
    }

}
