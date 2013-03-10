
package latin.nodes;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SupportCollector {

    Set<Supporter> seen;

    public SupportCollector() {
        this.seen = Sets.newHashSet();
    }

    public SupportCollector recordSupporter(Supported supported) {
        return recordSupporter(supported, supported.getSupporter());
    }

    public SupportCollector recordSupporter(Supported supported, Supporter supporter) {
        if (supporter != null && seen.add(supporter)) {
            supporter.collectSupport(this);
        }
        return this;
    }

    public static Iterator<TopSupporter> topSupporterIterator(Iterable<Supporter> supporters) {
        final Iterator<Supporter> sit = supporters.iterator();
        return new AbstractIterator<TopSupporter> () {
            @Override
            protected TopSupporter computeNext() {
                while (sit.hasNext()) {
                    Supporter s = sit.next();
                    if (s instanceof TopSupporter) {
                        return (TopSupporter) s;
                    }
                }
                return endOfData();
            }
        };
    }

    public void clear() {
        seen.clear();
    }

    public boolean isEmpty() {
        return seen.isEmpty();
    }

    public static Set<Supported> collectBlockers (Collection<? extends Supported> wantSupported,
                                                  Set<Supported> blockers) {
        for (Supported w : wantSupported) {
            w.supportedBlockers(blockers);
        }
        return blockers;
    }

    public static Set<Supported> collectBlockers (Collection<? extends Supported> wantSupported) {
        return collectBlockers(wantSupported, new HashSet<Supported>());
    }

}


