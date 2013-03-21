
package latin.nodes;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SupportCollector {

    public final Set<Supporter> seen;

    public SupportCollector() {
        this.seen = Sets.newHashSet();
    }

    public SupportCollector recordSupporter(Supported supported) {
        Supporter supporter = supported.getSupporter();
        if (supporter != null && seen.add(supporter)) {
            supporter.collectSupport(this);
        }
        return this;
    }

    public boolean collectSupporter(Supporter supporter) {
        return supporter != null && seen.add(supporter);
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


