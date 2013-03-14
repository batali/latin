
package latin.nodes;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Propagator implements DeduceQueue, RetractQueue {

    private Queue<Supported> dqueue;
    private Queue<Supported> rqueue;
    private Queue<Deducer> rdqueue;

    public Propagator() {
        this.dqueue = new LinkedBlockingQueue<Supported>();
        this.rqueue = new LinkedBlockingQueue<Supported>();
        this.rdqueue = new LinkedBlockingQueue<Deducer>();
    }

    public void addDeduced(Supported supported) {
        dqueue.add(supported);
    }

    @Override
    public void addRetracted(Supported supported) {
        rqueue.add(supported);
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
                supported.announceSet(this);
            }
            catch(ContradictionException ce) {
                throw ce;
            }
        }
    }

    @Override
    public void retractLoop() throws ContradictionException {
        Supported retracted = null;
        while ((retracted = rqueue.poll()) != null) {
            retracted.announceUnset(this, null);
        }
    }


}
