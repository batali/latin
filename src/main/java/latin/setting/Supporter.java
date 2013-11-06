
package latin.setting;

public interface Supporter {
    public boolean supportingAny();
    public boolean supporting(Supportable supportable);
    public boolean addSupported(Supportable supportable);
    public boolean removeSupported(Supportable supportable);
    public boolean retract(Retractor retractor);
}