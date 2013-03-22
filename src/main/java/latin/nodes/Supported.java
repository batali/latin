
package latin.nodes;

import javax.annotation.Nullable;

public interface Supported {
    public @Nullable Supporter getSupporter();
    public boolean haveSupporter();
    public int getStatus();
    public boolean supportable();
    public boolean setSupport(Supporter newSupporter);
    public boolean removeSupport();
    public void announceSet(DeduceQueue deduceQueue) throws ContradictionException;
    public void announceUnset(RetractQueue retractQueue, Object stopAt);
    public boolean supportedBy(Supporter supporter);

}