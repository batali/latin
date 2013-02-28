
package latin.setter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BaseSupporter implements Supporter {

    private Set<Setter> supported;

    public BaseSupporter() {
        supported = Sets.newHashSet();
    }

    @Override
    public void addSupported(Setter setter) {
        supported.add(setter);
    }

    @Override
    public void removeSupported(Setter setter) {
        supported.remove(setter);
    }

    public void collectSupported(SupportCollector supportCollector) {
    }

    public static void retractSet(Set<Setter> supported, Supporter supporter, Propagator propagator) {
        while(!supported.isEmpty()) {
            Setter setter = supported.iterator().next();
            propagator.recordRetracted(setter, supporter);
        }
    }

    @Override
    public void retract(Propagator propagator) {
        retractSet(supported, this, propagator);
    }

    public static List<BaseSupporter> getBaseSupporters(Collection<Supporter> supporters) {
        List<BaseSupporter> baseSupporters = Lists.newArrayList();
        for (Supporter supporter : supporters) {
            if (supporter instanceof BaseSupporter) {
                baseSupporters.add((BaseSupporter) supporter);
            }
        }
        return baseSupporters;
    }

    public String toString() {
        return "Base " + supported.toString();
    }

    public void reset() {
    }

    public boolean hasSupported() {
        return !supported.isEmpty();
    }

}