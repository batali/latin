
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public class TopSupporter implements Supporter {

    public interface Selector {
        public TopSupporter select(Collection<TopSupporter> topSupporters);
    }

    private Set<Supported> supportedSet;
    public TopSupporter() {
        this.supportedSet = Sets.newHashSet();
    }
    public Supported peekSupported() {
        if (supportedSet.isEmpty()) {
            return null;
        }
        else {
            return supportedSet.iterator().next();
        }
    }

    public boolean contains(Supported supported) {
        return supportedSet.contains(supported);
    }

    public boolean containsAny(Collection<? extends Supported> supporteds) {
        for (Supported supported : supporteds) {
           if (contains(supported)) {
               return true;
           }
        }
        return false;
    }

    public boolean addSupported(Supported supported) {
        return supportedSet.add(supported);
    }

    public boolean removeSupported(Supported supported) {
        return supportedSet.remove(supported);
    }

    public void retractAll(RetractQueue retractQueue) {
        Supported s = null;
        while ((s = peekSupported())!=null) {
           if (s.unsetSupporter()) {
               retractQueue.addRetracted(s);
           }
        }
        Preconditions.checkState(supportedSet.isEmpty());
    }

    public String toString() {
        return "top:" + supportedSet.toString();
    }

    public SupportCollector collectSupport(SupportCollector supportCollector) {
        return supportCollector;
    }

    public boolean haveSupported() {
        return !supportedSet.isEmpty();
    }

}