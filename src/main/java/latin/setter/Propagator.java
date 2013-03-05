
package latin.setter;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Propagator {

    private Queue<Setter> deducedQueue;
    private Queue<Setter> retractedQueue;
    private Queue<Deducer> rededuceQueue;
    public Optional<SupportRule> oContradictionRule;
    public SupportCollector contradictionSupportCollector;
    public int beforePropagateCount;

    public Propagator() {
        this.deducedQueue = new LinkedBlockingQueue<Setter>();
        this.retractedQueue = new LinkedBlockingQueue<Setter>();
        this.rededuceQueue = new LinkedBlockingQueue<Deducer>();
        this.oContradictionRule = Optional.absent();
        this.contradictionSupportCollector = new SupportCollector();
        this.beforePropagateCount = 0;
    }

    public boolean wasContradiction() {
        return oContradictionRule.isPresent();
    }

    public Setter firstDeducer() {
        return deducedQueue.peek();
    }

    public SupportRule getContradictionRule() {
        return oContradictionRule.orNull();
    }

    public void clear() {
        oContradictionRule = Optional.absent();
        contradictionSupportCollector.clear();
        deducedQueue.clear();
        retractedQueue.clear();
        rededuceQueue.clear();
        beforePropagateCount = 0;
    }

    public void addDeduced(Setter setter) {
        deducedQueue.add(setter);
    }

    public void addRetracted(Setter setter) {
        if (!wasContradiction()) {
            retractedQueue.add(setter);
        }
    }

    public void addRededucer(Deducer deducer) {
        if (!wasContradiction()) {
            rededuceQueue.add(deducer);
        }
    }

    public void recordContradiction(SupportRule atRule) throws ContradictionException {
        oContradictionRule = Optional.of(atRule);
        contradictionSupportCollector.recordSupporter(atRule);
        throw new ContradictionException("rule");
    }

    public void recordContradiction(SupportRule atRule, Setter s1, Setter s2) throws ContradictionException {
        oContradictionRule = Optional.of(atRule);
        contradictionSupportCollector.recordSupported(s1);
        contradictionSupportCollector.recordSupported(s2);
        throw new ContradictionException("values");
    }

    public void recordSupported(Setter setter, Supporter supporter) {
        int status = setter.getStatus();
        Preconditions.checkState(status >= 0);
        if (status == 0) {
            setter.setSupporter(supporter);
            supporter.addSupported(setter);
            addDeduced(setter);
        }
    }

    public void recordRetracted(Setter setter, Supporter supporter) {
        Preconditions.checkState(Objects.equal(setter.removeSupporter(), supporter));
        supporter.removeSupported(setter);
        addRetracted(setter);
    }

    public void propagateLoop() throws ContradictionException {
        Setter psetter = null;
        while ((psetter = deducedQueue.peek()) != null) {
            Preconditions.checkState(psetter.haveSupporter());
            beforePropagateCount = deducedQueue.size();
            psetter.announceSupported(this);
            deducedQueue.poll();
        }
    }

    public void retractLoop() throws ContradictionException {
        Setter rsetter = null;
        while((rsetter = retractedQueue.poll()) != null) {
            rsetter.announceRetracted(this, null);
        }
        if (!rededuceQueue.isEmpty()) {
            System.out.println("have rededucers");
            Deducer deducer = null;
            while ((deducer = rededuceQueue.poll()) != null) {
                deducer.deduce(this);
                if (!deducedQueue.isEmpty()) {
                    System.out.println("rededuced " + deducedQueue.peek());
                    propagateLoop();
                }
            }
        }
    }

    public void removeUnannounced(Setter setter) {
        Supporter supporter = setter.removeSupporter();
        supporter.removeSupported(setter);
    }

    public void retractFromContradiction() {
        if (wasContradiction()) {
            int bpc = beforePropagateCount;
            System.out.println("bpc " + bpc);
            Setter bcsetter = deducedQueue.poll();
            Preconditions.checkNotNull(bcsetter);
            System.out.println("bcs " + bcsetter.toString());
            removeUnannounced(bcsetter);
            Preconditions.checkState(!bcsetter.haveSupporter());
            SupportRule crule = getContradictionRule();
            bcsetter.announceRetracted(this, crule);
            Setter csetter = null;
            while ((csetter = deducedQueue.poll()) != null) {
                if (deducedQueue.size() >= bpc) {
                    removeUnannounced(csetter);
                }
                else {
                    System.out.println("dqs " + deducedQueue.size());
                    if (csetter.haveSupporter()) {
                        System.out.println("cs " + csetter.toString());
                    }
                    Preconditions.checkState(!csetter.haveSupporter());
                }
            }
        }
        clear();
    }

}
