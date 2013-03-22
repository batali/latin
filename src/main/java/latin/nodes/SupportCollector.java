
package latin.nodes;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Set;

public class SupportCollector {

    public final Set<Supporter> seen;

    public SupportCollector() {
        this.seen = Sets.newHashSet();
    }

    public SupportCollector recordSupporter(Supported supported) {
        return recordSupporter(supported.getSupporter());
    }

    public SupportCollector recordSupporter(Supporter supporter) {
        if (supporter != null && seen.add(supporter)) {
            supporter.collectSupport(this);
        }
        return this;
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

}


