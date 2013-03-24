
package latin.nodes;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class AbstractDeduceQueue implements DeduceQueue {

    private static Logger logger = LoggerFactory.getLogger(DeduceQueue.class);

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
        while ((s = peekSupported()) != null) {
            s.announceSet(this);
            rv = true;
            pollSupported();
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

    public void retractContradiction(BSRule contraRule) {
        RetractQueue erq = AbstractRetractQueue.emptyRetractQueue;
        Supported s = pollSupported();
        Preconditions.checkNotNull(s);
        logger.info("Retracting cs {}", s);
        Preconditions.checkState(s.removeSupport());
        s.announceUnset(erq, contraRule);
        while((s = pollSupported()) != null) {
            if (s.haveSupporter()) {
                logger.info("Retracting ds {}", s);
                s.removeSupport();
            }
            else {
                logger.info("No support for ds {}", s);
            }
        }
    }
}