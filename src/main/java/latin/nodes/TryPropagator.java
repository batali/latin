
package latin.nodes;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

public class TryPropagator {

    private LinkedList<Supported> aqueue;
    private LinkedList<Supported> dqueue;
    private LinkedList<Supported> rqueue;
    private Pair<Supported, BSRule> contraPair;

    private DeduceQueue deduceQueue;
    private RetractQueue retractQueue;

    public TryPropagator () {
        this.aqueue = new LinkedList<Supported>();
        this.dqueue = new LinkedList<Supported>();
        this.rqueue = new LinkedList<Supported>();
        this.contraPair = null;
        this.deduceQueue = new DeduceQueue() {
            @Override
            public boolean setSupport(Supported supported, Supporter supporter) {
                Preconditions.checkNotNull(supported);
                Preconditions.checkState(supported.supportable());
                if (supported.haveSupporter()) {
                    return true;
                }
                else {
                    supported.setSupport(supporter);
                    supporter.addSupported(supported);
                    dqueue.addLast(supported);
                    return true;
                }
            }
        };
        this.retractQueue = new RetractQueue() {
            @Override
            public void addRededucer(Deducer deducer) {
            }
            @Override
            public boolean removeSupport(Supported supported) {
                return supported != null && supported.haveSupporter() && supported.removeSupport();
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

    public void retract() {
        Supported s = null;
        while((s = dqueue.pollLast()) != null) {
            System.out.println("retracting dq " + s);
            Preconditions.checkState(s.removeSupport());
        }
        if (contraPair != null) {
            s = contraPair.getLeft();
            BSRule r = contraPair.getRight();
            contraPair = null;
            System.out.println("retracting cs " + s);
            Preconditions.checkState(s.removeSupport());
            s.announceUnset(retractQueue, r);
        }
        while ((s = aqueue.pollLast()) != null) {
            System.out.println("retracting aq " + s);
            Preconditions.checkState(s.removeSupport());
            s.announceUnset(retractQueue, null);
        }
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
        aqueue.clear();
    }

    public boolean isEmpty() {
        return aqueue.isEmpty() && contraPair == null && aqueue.isEmpty();
    }

}
