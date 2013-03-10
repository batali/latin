
package latin.nodes;

import javax.annotation.Nullable;
import java.util.Set;

public interface Supported {
    public @Nullable Supporter getSupporter();
    public boolean haveSupporter();
    public int getStatus();
    public boolean supportable();
    public boolean setSupporter(Supporter newSupporter) throws ContradictionException;
    public boolean unsetSupporter();
    public void announceSet(DeduceQueue deduceQueue) throws ContradictionException;
    public void announceUnset(RetractQueue retractQueue, Supporter stopAt);
    public void supportedBlockers(Set<Supported> blockers);
    public void handleSupport(SupportHandler handler);
}