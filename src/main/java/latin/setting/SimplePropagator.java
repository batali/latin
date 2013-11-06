
package latin.setting;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;

public class SimplePropagator implements Propagator {
    List<Supportable> supportableList;
    Optional<DeduceRule> contradictionRule;
    Optional<Supportable> contradictionAnnouncer;
    public SimplePropagator() {
        this.supportableList = Lists.newArrayList();
        this.contradictionRule = Optional.absent();
        this.contradictionAnnouncer = Optional.absent();
    }

    @Override
    public boolean addDeduced(Supportable supportable) {
        supportableList.add(supportable);
        return true;
    }

    @Override
    public boolean deduceLoop() {
        for (int p = 0; p < supportableList.size(); p++) {
            Supportable announcer = supportableList.get(p);
            announcer.announceSet(this);
            if (contradictionRule.isPresent()) {
                contradictionAnnouncer = Optional.of(announcer);
                return false;
            }
        }
        if (!haveContradiction()) {
            supportableList.clear();
        }
        return !haveContradiction();
    }

    @Override
    public void recordContradictionRule(DeduceRule deduceRule) {
        this.contradictionRule = Optional.of(deduceRule);
    }

    @Override
    public boolean haveContradiction() {
        return contradictionRule.isPresent();
    }
}