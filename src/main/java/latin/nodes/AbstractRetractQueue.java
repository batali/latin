package latin.nodes;

import latin.util.EmptyQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class AbstractRetractQueue implements RetractQueue {

    private static Logger logger = LoggerFactory.getLogger(RetractQueue.class);

    public static final AbstractRetractQueue emptyRetractQueue =
            new AbstractRetractQueue(new EmptyQueue<Supported>(), new EmptyQueue<Deducer>()) {
                @Override
                public boolean addRetracted(Supported supported) {
                    return true;
                }
                @Override
                public void addRededucer(Deducer deducer) {
                }
            };

    private Queue<Supported> rqueue;
    private Queue<Deducer> dqueue;

    public AbstractRetractQueue(Queue<Supported> rqueue, Queue<Deducer> dqueue) {
        this.rqueue = rqueue;
        this.dqueue = dqueue;
    }

    @Override
    public void addRededucer(Deducer deducer) {
        dqueue.add(deducer);
    }

    @Override
    public boolean addRetracted(Supported supported) {
        rqueue.add(supported);
        return true;
    }

    @Override
    public boolean removeSupport(Supported supported) {
        return supported.removeSupport() & addRetracted(supported);
    }

    @Override
    public Supported peekRetracted() {
        return rqueue.peek();
    }

    @Override
    public Supported pollRetracted() {
        return rqueue.poll();
    }

    @Override
    public Deducer peekRededucer() {
        return dqueue.peek();
    }

    @Override
    public Deducer pollRededucer() {
        return dqueue.poll();
    }

    public boolean haveRetracted() {
        return peekRetracted() != null;
    }

    public boolean haveRededucer() {
        return peekRededucer() != null;
    }

    public boolean retractLoop() {
        Supported s;
        boolean rv = false;
        while((s = pollRetracted()) != null) {
            s.announceUnset(this, null);
            rv = true;
        }
        return rv;
    }

    public boolean rededuceLoop(DeduceQueue deduceQueue) throws ContradictionException {
        Deducer deducer;
        boolean rv = false;
        while ((deducer = pollRededucer()) != null) {
            deducer.deduce(deduceQueue);
            if (deduceQueue.haveSupported()) {
                rv = true;
                logger.info("Rededuced {}", deduceQueue.peekSupported());
                deduceQueue.propagateLoop();
            }
        }
        return rv;
    }

    public boolean retractTop(TopSupporter tops, DeduceQueue deduceQueue) throws ContradictionException {
        boolean rv = tops.retract(this) && retractLoop();
        if (rv && haveRededucer()) {
            rededuceLoop(deduceQueue);
        }
        return rv;
    }
}