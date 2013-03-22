
package latin.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Retractor {

    public Set<BooleanSetting> tsettings;

    public Retractor (Set<BooleanSetting> tsettings) {
        this.tsettings = tsettings;
    }

    public Retractor() {
        this(new HashSet<BooleanSetting>());
    }

    public Retractor(Collection<BooleanSetting> bsc) {
        this(new HashSet<BooleanSetting>(bsc));
    }

    public Retractor addTsetting(BooleanSetting s) {
        tsettings.add(s);
        return this;
    }

    public TopSupporter selectTopSupporter(Iterable<TopSupporter> topSupporters) {
        for (TopSupporter ts : topSupporters) {
            if (ts.doesSupport() && !ts.containsAny(tsettings)) {
                return ts;
            }
        }
        return null;
    }

    public TopSupporter selectTopSupporter(Iterable<TopSupporter> topSupporters, Supported tst) {
        for (TopSupporter ts : topSupporters) {
            if (ts.doesSupport() && !ts.containsAny(tsettings) && !ts.contains(tst)) {
                return ts;
            }
        }
        return null;
    }

    public TopSupporter selectTopSupporter (SupportCollector supportCollector) {
        return selectTopSupporter(supportCollector.topSupporters());
    }

    public TopSupporter selectTopSupporter (Supported supported) {
        SupportCollector sc = new SupportCollector();
        sc.recordSupporter(supported);
        return selectTopSupporter(sc);
    }

    public TopSupporter selectTopSupporter (BSRule rule, Supported tst) {
        SupportCollector sc = new SupportCollector();
        sc.recordSupporter(rule);
        return selectTopSupporter(sc.topSupporters(), tst);
    }

}
