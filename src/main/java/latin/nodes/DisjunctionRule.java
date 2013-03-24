
package latin.nodes;

import latin.veritas.Psetting;

import javax.annotation.Nullable;
import java.util.List;

public class DisjunctionRule extends AbstractDisjunctionRule {

    public final String name;
    public final List<? extends BooleanSetting> settings;
    private @Nullable Supported supported;

    public DisjunctionRule(String name, List<? extends BooleanSetting> settings) {
        super(settings.size());
        this.name = name;
        this.settings = settings;
        this.supported = null;
        for (BooleanSetting setting : settings) {
            setting.addRule(this);
        }
    }

    public DisjunctionRule(String name, List<Psetting> psettings, Psetting.GetSetting<BooleanSetting> handler) {
        this(name, Psetting.transformPsettings(psettings, handler));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean addSupported(Supported newSupported) {
        supported = newSupported;
        return true;
    }

    @Override
    public boolean removeSupported(Supported wasSupported) {
        supported = null;
        return true;
    }

    @Override
    public boolean doesSupport() {
        return supported != null;
    }

    public BooleanSetting getSetting(int p) {
        return settings.get(p);
    }

    @Override
    public void retractSupported(RetractQueue rq) {
       if (supported != null) {
           rq.removeSupport(supported);
       }
    }
}