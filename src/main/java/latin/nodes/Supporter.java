
package latin.nodes;

import javax.annotation.Nullable;

public interface Supporter {
    public boolean addSupported(Supported supported);
    public boolean removeSupported(Supported supported);
    public @Nullable Supported peekSupported();
    public void collectSupport(SupportCollector supportCollector);
}
