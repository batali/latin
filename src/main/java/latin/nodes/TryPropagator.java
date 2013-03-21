
package latin.nodes;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.LinkedList;

public class TryPropagator {

    private LinkedList<Supported> aqueue;
    private LinkedList<Supported> dqueue;
    private LinkedList<Supported> rqueue;
    private LinkedList<Deducer> rdqueue;
    private Pair<Supported, BSRule> contraPair;

    private DeduceQueue deduceQueue;
    private RetractQueue retractQueue;

    private boolean addDeduced(Supported supported) {
        dqueue.add(supported);
        return true;
    }

    private boolean addRetracted(Supported supported) {
        rqueue.add(supported);
        return true;
    }

    public TryPropagator () {
        this.aqueue = new LinkedList<Supported>();
        this.dqueue = new LinkedList<Supported>();
        this.rqueue = new LinkedList<Supported>();
        this.rdqueue = new LinkedList<Deducer>();
        this.contraPair = null;
        this.deduceQueue = new DeduceQueue() {
            @Override
            public boolean setSupport(Supported supported, Supporter supporter) {
                return supported.setSupport(supporter) && addDeduced(supported);
            }
        };
        this.retractQueue = new RetractQueue() {
            @Override
            public void addRededucer(Deducer deducer) {
                if (contraPair == null) {
                    rdqueue.add(deducer);
                }
            }
            @Override
            public boolean removeSupport(Supported supported) {
                return (supported != null && supported.haveSupporter() && supported.removeSupport() &&
                        (contraPair != null || addRetracted(supported)));
            }
        };
    }

    public DeduceQueue getDeduceQueue() {
        return deduceQueue;
    }

    public boolean propagateLoop() throws ContradictionException {
        Supported s = null;
        while ((s = dqueue.pollFirst()) != null) {
            try {
                s.announceSet(deduceQueue);
                aqueue.addLast(s);
            }
            catch(ContradictionException ce) {
                contraPair = Pair.of(s, ce.atRule);
                throw ce;
            }
        }
        return true;
    }

    public void retractContradiction() {
        Supported s = null;
        while((s = dqueue.pollLast()) != null) {
            System.out.println("retracting dq " + s);
            Preconditions.checkState(s.removeSupport());
        }
        if (contraPair != null) {
            s = contraPair.getLeft();
            BSRule r = contraPair.getRight();
            System.out.println("retracting cs " + s);
            Preconditions.checkState(s.removeSupport());
            s.announceUnset(retractQueue, r);
        }
        while ((s = aqueue.pollLast()) != null) {
            System.out.println("retracting aq " + s);
            Preconditions.checkState(s.removeSupport());
            s.announceUnset(retractQueue, null);
        }
        contraPair = null;
    }

    public boolean trySupport(Supported supported) throws ContradictionException {
        if (!supported.supportable()) {
            return false;
        }
        if (supported.haveSupporter()) {
            return true;
        }
        TopSupporter tsup = new TopSupporter(supported);
        tsup.deduce(deduceQueue);
        return propagateLoop();
    }

    public void clear() {
        Preconditions.checkState(contraPair == null);
        Preconditions.checkState(dqueue.isEmpty());
        Preconditions.checkState(rqueue.isEmpty());
        Preconditions.checkState(rdqueue.isEmpty());
        aqueue.clear();
    }

    public boolean isEmpty() {
        return aqueue.isEmpty() && contraPair == null && aqueue.isEmpty() && rqueue.isEmpty();
    }

    public boolean retractLoop() {
        Supported s = null;
        while((s = rqueue.pollFirst()) != null) {
            s.announceUnset(retractQueue, null);
        }
        rededuceLoop();
        return true;
    }

    public void rededuceLoop() {
        Deducer d = null;
        while ((d = rdqueue.pollFirst()) != null) {
            try {
                d.deduce(deduceQueue);
                if (!dqueue.isEmpty()) {
                    System.out.println("rededuced " + dqueue.peekFirst());
                }
                propagateLoop();
                aqueue.clear();
            }
            catch(ContradictionException ce) {
                retractContradiction();
            }
        }
    }

    public boolean retractTopSupporter(TopSupporter topSupporter) {
        Preconditions.checkState(isEmpty());
        return topSupporter != null && topSupporter.retract(retractQueue) && retractLoop();
    }

    public boolean retractAll(Collection<BooleanSetting> settings) {
        SupportCollector supportCollector = new SupportCollector();
        while(true) {
            supportCollector.clear();
            for (BooleanSetting bs : settings) {
                supportCollector.recordSupporter(bs);
            }
            if (supportCollector.isEmpty()) {
                return true;
            }
            for (TopSupporter tops : supportCollector.topSupporters()) {
                tops.retract(retractQueue);
            }
            if (!retractLoop()) {
                return false;
            }
        }
    }

    public boolean makeSupportable(BooleanSetting setting, Retractor retractor) {
        if (!setting.supportable()) {
            BooleanSetting op = setting.getOpposite();
            SupportCollector supportCollector = new SupportCollector();
            while(!setting.supportable()) {
                Preconditions.checkState(op.haveSupporter());
                supportCollector.clear();
                supportCollector.recordSupporter(op);
                TopSupporter tops = retractor.selectTopSupporter(supportCollector);
                if (tops == null) {
                    System.out.println("Failed to find top of " + op + " for " + setting);
                    return false;
                }
                System.out.println("retracting " + tops + " of " + op + " for " + setting);
                if (!retractTopSupporter(tops)) {
                    System.out.println("failed to retract top");
                    return false;
                }
            }
        }
        return setting.supportable();
    }

    public boolean trySupport(BooleanSetting setting, Retractor retractor) {
        if (setting.haveSupporter()) {
            System.out.println("already " + setting);
        }
        while (!setting.haveSupporter()) {
            if (!makeSupportable(setting, retractor)) {
                System.out.println("failed to make supportable " + setting);
                return false;
            }
            try {
                if (trySupport(setting)) {
                    System.out.println("set support for " + setting);
                    clear();
                }
            }
            catch (ContradictionException ce) {
                TopSupporter tops = retractor.selectTopSupporter(ce.atRule, setting);
                retractContradiction();
                if (tops == null) {
                    System.out.println("failed to find contra top for " + setting);
                    return false;
                }
                if (!retractTopSupporter(tops)) {
                    System.out.println("failed to retract contra top " + tops + " for " + setting);
                    return false;
                }
            }
        }
        return setting.haveSupporter();
    }

    public boolean trySupport(Collection<BooleanSetting> tsettings, Retractor retractor) {
        for (BooleanSetting tsetting : tsettings) {
            if (trySupport(tsetting, retractor)) {
                retractor.addTsetting(tsetting);
            }
            else {
                return false;
            }
        }
        return true;
    }

    public boolean trySupport(Collection<BooleanSetting> tsettings) {
        return trySupport(tsettings, new Retractor());
    }

    public boolean retractSupport(BooleanSetting setting, Retractor retractor, SupportCollector supportCollector) {
        Supporter supporter;
        while ((supporter = setting.getSupporter()) != null) {
            supportCollector.clear();
            supporter.collectSupport(supportCollector);
            TopSupporter tops = retractor.selectTopSupporter(supportCollector);
            if (tops == null || !retractTopSupporter(tops)) {
                break;
            }
        }
        return !setting.haveSupporter();
    }

    public boolean retractSupports(Collection<BooleanSetting> settings, Retractor retractor) {
        SupportCollector supportCollector = new SupportCollector();
        for (BooleanSetting setting : settings) {
            if (!retractSupport(setting, retractor, supportCollector)) {
                return false;
            }
        }
        return true;
    }

}
