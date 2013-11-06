
package latin.setting;

public interface Supportable {
    public boolean isSupported();
    public boolean setSupport(Supporter supporter, Propagator propagator);
    public Supporter getSupporter();
    public boolean removeSupport(Retractor retractor);
    public boolean announceSet(Propagator propagator);
    public boolean announceUnset(Retractor retractor);
}