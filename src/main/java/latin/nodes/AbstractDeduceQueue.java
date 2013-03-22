
package latin.nodes;

import com.google.common.base.Preconditions;

import java.util.Queue;

public class AbstractDeduceQueue implements DeduceQueue {

    private Queue<Supported> supportedQueue;

    AbstractDeduceQueue(Queue<Supported> supportedQueue) {
        this.supportedQueue = supportedQueue;
    }

    @Override
    public boolean addSupported(Supported supported) {
        Preconditions.checkArgument(supported.haveSupporter());
        supportedQueue.add(supported);
        return true;
    }

    @Override
    public boolean setSupport(Supported supported, Supporter supporter) {
        return supported.setSupport(supporter) && addSupported(supported);
    }

    public Supported peekSupported() {
        return supportedQueue.peek();
    }

    public Supported pollSupported() {
        return supportedQueue.poll();
    }

    public boolean propagateLoop() throws ContradictionException {
        Supported s;
        boolean rv = false;
        while ((s = pollSupported()) != null) {
            s.announceSet(this);
            rv = true;
        }
        return rv && finish();
    }

    public boolean haveSupported() {
        return peekSupported() != null;
    }

    public boolean finish() {
        Preconditions.checkState(!haveSupported());
        return true;
    }

}