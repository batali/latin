
package latin.nodes;

public interface Node<T> {
    public Setter<T> getValueSetter(T value);
    public Setter<T> getSupportedSetting();
    public String getPathString();
    public int setterCount();
    public Setter<T> getIndexSetter(int index);
}
