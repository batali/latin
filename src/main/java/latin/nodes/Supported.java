
package latin.nodes;

import javax.annotation.Nullable;
import java.util.Set;

public interface Supported {
    public @Nullable Supporter getSupporter();
    public boolean haveSupporter();
    public int getStatus();
    public boolean supportable();
    public void setSupport(Supporter newSupporter);
    public boolean removeSupport();
    public void announceSet(DeduceQueue deduceQueue) throws ContradictionException;
    public void announceUnset(RetractQueue retractQueue, BSRule stopAt);
    public void supportedBlockers(Set<Supported> blockers);
    public boolean supportedBy(Supporter supporter);

}