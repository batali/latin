
package latin.setting;

import com.google.common.base.Objects;

import java.util.List;

public abstract class AbstractDisjunctionRule extends RuleCounts implements Setting.Rule {

    public AbstractDisjunctionRule(int settingCount) {
        super(settingCount);
    }

    @Override
    public void recordSet(Setting setting, boolean bv) {
        inc(bv);
    }

    @Override
    public void recordUnset(Setting setting, boolean bv) {
        dec(bv);
    }

    public boolean disjunctionDeduce(List<? extends Setting> settings, Propagator propagator) {
        Setting supportable = null;
        for (Setting setting : settings) {
            if (setting.isSupported()) {
                return true;
            }
            else if (setting.isSatisfiable()) {
                supportable = setting;
                break;
            }
        }
        if (supportable == null) {
            propagator.recordContradictionRule(this);
            return false;
        }
        else {
            if (!supportable.isSupported()) {
                supportable.setSupport(this, propagator);
            }
            return true;
        }
    }

    public boolean supporting(Supportable s) {
        return s != null && Objects.equal(s.getSupporter(), this);
    }
}

