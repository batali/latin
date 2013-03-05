
package latin.setter;

public interface Supporter {
    public void addSupported(Setter setter);
    public void removeSupported(Setter setter);
    public void collectSupported (SupportCollector supportCollector);
    public boolean hasSupported();
}