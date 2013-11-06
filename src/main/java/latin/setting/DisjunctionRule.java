
package latin.setting;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;

public class DisjunctionRule extends AbstractDisjunctionRule {

    List<Setting> settings;
    Supportable supported;

    public DisjunctionRule (List<Setting> settings) {
        super(settings.size());
        this.settings = settings;
        supported = null;
        for (Setting setting : settings) {
            setting.addRule(this);
        }
    }

    @Override
    public boolean deduceCheck() {
        return disjunctionDeduceCheck();
    }

    @Override
    public boolean deduce(Propagator propagator) {
        return disjunctionDeduce(settings, propagator);
    }

    @Override
    public boolean retractCheck() {
        return disjunctionRetractCheck();
    }

    @Override
    public boolean supportingAny() {
        return supported != null;
    }

    @Override
    public boolean addSupported(Supportable supportable) {
        Preconditions.checkState(supported == null);
        supported = supportable;
        return true;
    }

    @Override
    public boolean removeSupported(Supportable supportable) {
        if (Objects.equals(supported, supportable)) {
            supported = null;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean retract(Retractor retractor) {
        return supported != null && supported.removeSupport(retractor);
    }
}