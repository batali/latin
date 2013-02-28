
package latin.setter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SupportCollector {

    private Set<Supporter> seen;
    private Map<Setter,Supporter> supporterOfMap;
    private Map<Supporter,Set<Setter>> supportedByMap;

    public SupportCollector() {
        this.seen = new HashSet<Supporter>();
        this.supporterOfMap = Maps.newHashMap();
        this.supportedByMap = Maps.newHashMap();
    }

    public boolean isEmpty() {
        return supporterOfMap.isEmpty() && supportedByMap.isEmpty();
    }

    public SupportCollector clear() {
        this.seen.clear();
        this.supportedByMap.clear();
        this.supporterOfMap.clear();
        return this;
    }

    public void recordSupported(Setter supported) {
        Supporter supporter = supported.getSupporter();
        recordSupported(supported, supporter);
    }

    public void recordSupporter(Supporter supporter) {
        if (seen.add(supporter)) {
            supporter.collectSupported(this);
        }
    }

    public void recordSupported(Setter supported, Supporter supporter) {
        if (supporter != null && !supporterOfMap.containsKey(supported)) {
            supporterOfMap.put(supported, supporter);
            Set<Setter> slset = supportedByMap.get(supporter);
            if (slset == null) {
                slset = Sets.newHashSet();
                supportedByMap.put(supporter, slset);
            }
            slset.add(supported);
            recordSupporter(supporter);
        }
    }

    public Set<Setter> getSupportedBy(Supporter supporter) {
        return supportedByMap.get(supporter);
    }

    public Set<Supporter> getSeen() {
        return seen;
    }

    public List<BaseSupporter> getBaseSupporters() {
        return BaseSupporter.getBaseSupporters(getSeen());
    }

    public Set<Setter> getBaseSetters() {
        Set<Setter> baseSetters = Sets.newHashSet();
        for(BaseSupporter bs : getBaseSupporters()) {
            Set<Setter> sps = supportedByMap.get(bs);
            baseSetters.addAll(sps);
        }
        return baseSetters;
    }

}