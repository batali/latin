
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Collection;

public class TopSupporter implements Supporter {

    private @Nullable Supported supported;

    public TopSupporter(@Nullable Supported supported) {
        this.supported = supported;
    }

    public TopSupporter() {
        this(null);
    }

    public boolean contains(Supported s) {
        return Objects.equal(supported, s);
    }

    public boolean containsAny(Collection<? extends Supported> supporteds) {
        for (Supported supported : supporteds) {
           if (contains(supported)) {
               return true;
           }
        }
        return false;
    }

    public Supported peekSupported() {
        return supported;
    }

    public boolean deduce(DeduceQueue deduceQueue) throws ContradictionException {
        if (supported != null && supported.supportable()) {
            return deduceQueue.setSupport(supported, this);
        }
        else {
            return false;
        }
    }

    public boolean doesSupport() {
        return supported != null && supported.supportedBy(this);
    }

    public boolean addSupported(Supported s) {
        supported = s;
        return true;
    }

    public boolean removeSupported(Supported s) {
        Preconditions.checkState(Objects.equal(s, supported));
        supported = null;
        return true;
    }

    public boolean retract(RetractQueue rq) {
        return (supported != null && supported.supportedBy(this) && rq.removeSupport(supported));
    }

    public String toString() {
        String ss = supported != null ? supported.toString() : "";
        return "top[" + ss + "]";
    }

    public SupportCollector collectSupport(SupportCollector supportCollector) {
        supportCollector.seen.add(this);
        return supportCollector;
    }

    public boolean haveSupported() {
        return supported != null;
    }

}