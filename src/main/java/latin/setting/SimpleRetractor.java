package latin.setting;

import com.google.common.collect.Sets;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SimpleRetractor implements Retractor {
    public Queue<Supportable> retractQueue;
    public Set<DeduceRule> rededucers;
    public SimpleRetractor () {
        this.retractQueue = new LinkedList<Supportable>();
        this.rededucers = Sets.newHashSet();
    }

    @Override
    public boolean addRetracted(Supportable supportable) {
        retractQueue.add(supportable);
        return true;
    }

    @Override
    public boolean retractLoop() {
        Supportable retracted;
        while ((retracted = retractQueue.poll()) != null) {
            retracted.announceUnset(this);
        }
        return true;
    }

    @Override
    public void addRededucer(DeduceRule deduceRule) {
        rededucers.add(deduceRule);
    }

    @Override
    public boolean atContradiction(DeduceRule deduceRule, Supportable supportable) {
        return false;
    }

    @Override
    public Set<DeduceRule> getRededucers() {
        return rededucers;
    }

}
