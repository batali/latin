
package latin.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Retractor implements RetractQueue {

    private LinkedList<Supported> rqueue;
    private LinkedList<Deducer> rdqueue;
    private TryPropagator tp;
    public Set<BooleanSetting> tsettings;

    public Retractor (Set<BooleanSetting> tsettings) {
        this.rdqueue = new LinkedList<Deducer>();
        this.rqueue = new LinkedList<Supported>();
        this.tp = new TryPropagator();
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

    @Override
    public void addRededucer(Deducer deducer) {
        rdqueue.add(deducer);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean addRetracted(Supported supported) {
        rqueue.addLast(supported);
        return true;
    }

    @Override
    public boolean removeSupport(Supported supported) {
       return supported != null && supported.removeSupport() && addRetracted(supported);
    }

    public void announceRetracted(Supported s) {
        s.announceUnset(this, null);
        Deducer d = null;
        while (!s.haveSupporter() && (d = rdqueue.pollFirst()) != null) {
            try {
                d.deduce(tp.getDeduceQueue());
                if (!tp.isEmpty()) {
                    tp.propagateLoop();
                    tp.clear();
                }
            }
            catch (ContradictionException ce) {
                tp.retract();
            }
        }
        rdqueue.clear();
    }

    public void retractLoop() {
        Supported s = null;
        while ((s = rqueue.pollFirst()) != null) {
            announceRetracted(s);
        }
    }

    public TopSupporter selectTopSupporter(Iterable<TopSupporter> topSupporters) {
        for (TopSupporter ts : topSupporters) {
            if (ts.doesSupport() && !ts.containsAny(tsettings)) {
                return ts;
            }
        }
        return null;
    }

    public TopSupporter selectTopSupporter (SupportCollector supportCollector) {
        return selectTopSupporter(supportCollector.topSupporters());
    }

    public TopSupporter selectTopSupporter (BooleanSetting setting) {
        SupportCollector sc = new SupportCollector();
        sc.recordSupporter(setting);
        return selectTopSupporter(sc);
    }

    public boolean retractTopSupporter(TopSupporter tops) {
        if (tops != null) {
            tops.retract(this);
            retractLoop();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean retractTopSupporter(SupportCollector supportCollector) {
        return retractTopSupporter(selectTopSupporter(supportCollector));
    }

    public boolean retractTopSupporter(BooleanSetting setting) {
        return retractTopSupporter(selectTopSupporter(setting));
    }

    public boolean retractSetting(BooleanSetting setting) {
        while(setting.haveSupporter() && retractTopSupporter(setting));
        return !setting.haveSupporter();
    }

}
