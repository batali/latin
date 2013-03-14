
package latin.nodes;

import com.google.common.base.Preconditions;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Propagator implements DeduceQueue, RetractQueue {

    private Queue<Supported> dqueue;
    private Queue<Supported> rqueue;
    private Queue<Deducer> rdqueue;
    private RetractQueue crq;

    public Propagator() {
        this.dqueue = new LinkedBlockingQueue<Supported>();
        this.rqueue = new LinkedBlockingQueue<Supported>();
        this.rdqueue = new LinkedBlockingQueue<Deducer>();
        this.crq = new RetractQueue() {
            @Override
            public boolean addRetracted(Supported supported) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void addRededucer(Deducer deducer) {
            }
            @Override
            public void retractLoop() throws ContradictionException {
            }
            @Override
            public boolean removeSupport(Supported supported) {
                return supported.removeSupport();
            }
        };
    }

    public void addDeduced(Supported supported) {
        Preconditions.checkState(supported.haveSupporter());
        dqueue.add(supported);
    }

    public boolean setSupport(Supported supported, Supporter supporter) {
        Preconditions.checkState(supported.supportable());
        if (!supported.haveSupporter()) {
            supported.setSupport(supporter);
            supporter.addSupported(supported);
            addDeduced(supported);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean addRetracted(Supported supported) {
        rqueue.add(supported);
        return true;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addRededucer(Deducer deducer) {
        rdqueue.add(deducer);
    }

    public void deduceLoop() throws ContradictionException {
        Supported supported = null;
        while ((supported = dqueue.poll()) != null) {
            try {
                Preconditions.checkState(supported.haveSupporter());
                supported.announceSet(this);
            }
            catch(ContradictionException ce) {
                System.out.println("ce " + ce.getMessage() + " " + supported);
                BSRule atRule = ce.atRule;
                Preconditions.checkState(supported.removeSupport());
                System.out.println("at " + supported.toString());
                supported.announceUnset(crq, atRule);
                while ((supported = dqueue.poll()) != null) {
                    if (!supported.haveSupporter()) {
                        System.out.println("no supporter " + supported.toString());
                    }
                    Preconditions.checkState(supported.removeSupport());
                }
                throw ce;
            }
        }
    }

    public boolean removeSupport(Supported supported) {
        return supported != null && supported.removeSupport() && addRetracted(supported);
    }

    public void rededuceLoop() throws ContradictionException {
        Deducer rd = null;
        while ((rd = rdqueue.poll()) != null) {
            rd.deduce(this);
            deduceLoop();
        }
    }

    @Override
    public void retractLoop() throws ContradictionException {
        Supported retracted = null;
        while ((retracted = rqueue.poll()) != null) {
            retracted.announceUnset(this, null);
        }
        rededuceLoop();
    }
}
