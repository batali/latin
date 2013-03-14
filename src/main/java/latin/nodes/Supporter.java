
package latin.nodes;

public interface Supporter {
    public boolean addSupported(Supported supported);
    public boolean removeSupported(Supported supported);
    public SupportCollector collectSupport(SupportCollector supportCollector);
    public boolean doesSupport();
}
