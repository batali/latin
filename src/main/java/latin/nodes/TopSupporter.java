
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Set;

public class TopSupporter implements Supporter {
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

    @Override
    public void handleSupport(SupportHandler handler) {
    }

    public boolean haveSupported() {
        return !supportedSet.isEmpty();
    }

}