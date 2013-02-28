
package latin.forms;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RuleMapBuilder<KT extends Enum<KT>> {

    public final Class<KT> ktClass;
    public final String name;
    public final EnumMap<KT,Rulef> enumMap;

    public RuleMapBuilder(Class<KT> ktClass, String name, EnumMap<KT,Rulef> enumMap) {
        this.ktClass = ktClass;
        this.name = name;
        this.enumMap = enumMap;
    }

    public RuleMapBuilder(Class<KT> ktClass, String name) {
        this(ktClass, name, new EnumMap<KT, Rulef>(ktClass));
    }

    public KT getKey(String ks) {
        return Enum.valueOf(ktClass, ks);
    }

    public RuleMapBuilder<KT> add(KT key, Rulef rulef) {
        enumMap.put(key, rulef);
        return this;
    }

    public RuleMapBuilder<KT> add(KT key, List<?> objects) {
        enumMap.put(key, FormRule.makeRule(name, key, objects));
        return this;
    }

    public RuleMapBuilder<KT> add(KT key, String afs) {
        return add(key, Suffix.csplit(afs));
    }

    public RuleMapBuilder<KT> add(String ks, String afs) {
        return add(getKey(ks), afs);
    }

    public RuleMapBuilder<KT> add(Map<KT, Rulef> rmap) {
        enumMap.putAll(rmap);
        return this;
    }
}
