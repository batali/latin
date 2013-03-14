
package latin.nodes;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SupportCollector {

    public final Set<Supporter> seen;

    public SupportCollector() {
        this.seen = Sets.newHashSet();
    }

    public SupportCollector recordSupporter(Supported supported) {
        return recordSupporter(supported, supported.getSupporter());
    }

    public boolean collectSupporter(Supporter supporter) {
        return supporter != null && seen.add(supporter);
    }

    public SupportCollector recordSupporter(Supported supported, Supporter supporter) {
        if (collectSupporter(supporter)) {
            supporter.collectSupport(this);
        }
        return this;
    }

    public static final Predicate<Supporter> isTopSupporter = new Predicate<Supporter>() {
        @Override
        public boolean apply(Supporter supporter) {
            return supporter instanceof TopSupporter;
        }
    };

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

    public Iterable<TopSupporter> topSupporters() {
        return Iterables.filter(seen, TopSupporter.class);
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


