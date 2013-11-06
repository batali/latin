
package latin.setting;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractSetting implements Setting {
    public final String spec;
    Supporter supporter;
    protected AbstractSetting opposite;
    Set<Rule> rules;
    public AbstractSetting(String spec, AbstractSetting opposite) {
        this.spec = spec;
        this.opposite = opposite;
        this.supporter = null;
        this.rules = Sets.newHashSet();
    }

    public String toString() {
        return spec;
    }

    public boolean getPolarity() {
        return true;
    }

    @Override
    public AbstractSetting getOpposite() {
        return opposite;
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public boolean addRule(Rule rule) {
        return rules.add(rule);
    }

    public boolean removeRule(Rule rule) {
        return rules.remove(rule);
    }

    @Override
    public boolean isSupported() {
        return supporter != null;
    }

    @Override
    public Supporter getSupporter() {
        return supporter;
    }


    public AbstractSetting getSupported() {
        if (isSupported()) {
            return this;
        }
        else if (opposite.isSupported()) {
            return opposite;
        }
        else {
            return null;
        }
    }

    @Override
    public boolean setSupport(Supporter supporter, Propagator propagator) {
        Preconditions.checkState(isSatisfiable());
        if (!isSupported()) {
            this.supporter = supporter;
            supporter.addSupported(this);
            return propagator.addDeduced(this);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean removeSupport(Retractor retractor) {
        Supporter oldSupporter = supporter;
        supporter = null;
        return (oldSupporter != null &&
                oldSupporter.removeSupported(this) &&
                retractor.addRetracted(this));
    }

    public boolean announceSetToRule(boolean v, Rule r, Propagator p) {
        r.recordSet(this, v);
        if (r.deduceCheck()) {
            r.deduce(p);
            if (p.haveContradiction()) {
                return false;
            }
        }
        return true;
    }

    public boolean announceSetToRules(boolean v, Collection<Rule> rules, Propagator p) {
        for (Rule r : rules) {
            if (!announceSetToRule(v, r, p)) {
                return false;
            }
        }
        return true;
    }

    boolean announceValueSet(boolean v, Propagator propagator) {
        return announceSetToRules(v, rules, propagator);
    }

    @Override
    public boolean announceSet(Propagator propagator) {
        return (announceValueSet(true, propagator) &&
                getOpposite().announceValueSet(false, propagator));
    }

    public boolean announceUnsetToRule(boolean v, Rule r, Retractor p) {
        r.recordUnset(this, v);
        boolean rc = r.retractCheck();
        if (rc) {
            if (r.supportingAny()) {
                r.retract(p);
            }
        }
        if (p.atContradiction(r, this)) {
            return false;
        }
        if (!rc && r.deduceCheck()) {
            p.addRededucer(r);
        }
        return true;
    }

    public boolean announceUnsetToRules(boolean v, Collection<Rule> rules, Retractor p) {
        for (Rule r : rules) {
            if (!announceUnsetToRule(v, r, p)) {
                return false;
            }
        }
        return true;
    }

    boolean announceValueUnset(boolean v, Retractor retractor) {
        return announceUnsetToRules(v, rules, retractor);
    }

    @Override
    public boolean announceUnset(Retractor retractor) {
        return (announceValueUnset(true, retractor) &&
                getOpposite().announceValueUnset(false, retractor));
    }

    @Override
    public int getStatus() {
        if (isSupported()) {
            return 1;
        }
        else if (opposite.isSupported()) {
            return -1;
        }
        else {
            return 0;
        }
    }

    @Override
    public boolean isSatisfiable() {
        return isSupported() || !opposite.isSupported();
    }


}


