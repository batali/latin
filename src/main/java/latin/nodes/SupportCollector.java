
package latin.nodes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class SupportCollector {

    Set<Supporter> seen;
    Map<Supported,Supporter> ofMap;

    public SupportCollector() {
        this.seen = Sets.newHashSet();
        this.ofMap = Maps.newHashMap();
    }

    public void recordSupporter(Supported supported) {
        recordSupporter(supported, supported.getSupporter());
    }

    public void recordSupporter(Supported supported, Supporter supporter) {
        if (supporter != null) {
            ofMap.put(supported, supporter);
            if (seen.add(supporter)) {
                supporter.collectSupport(this);
            }
        }
    }

    public Set<TopSupporter> getTopSupporters() {
        Set<TopSupporter> topSupporters = Sets.newHashSet();
        for (Supporter s : seen) {
            if (s instanceof TopSupporter) {
                topSupporters.add((TopSupporter)s);
            }
        }
        return topSupporters;
    }

}