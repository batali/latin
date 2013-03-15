
package latin.nodes;

public interface Node<T> {
    public Setter<T> getValueSetter(T value);
    public Setter<T> getSupportedSetter();
    public String getPathString();
    public int setterCount();
    public BooleanSetting getIndexSetter(int index);
    public BooleanSetting getSupportedSetting();
}
