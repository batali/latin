
package latin.nodes;

public interface Node<T> {
    public String getPathString();
    public int setterCount();
    public BooleanSetting getIndexSetting(int index);
    public BooleanSetting getSupportedSetting();
}
