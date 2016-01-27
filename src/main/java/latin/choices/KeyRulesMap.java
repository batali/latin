package latin.choices;

import latin.forms.ModRule;
import latin.util.PathId;

import java.util.EnumMap;

public class KeyRulesMap<K extends Enum<K>> extends EnumMap<K, ModRule> implements KeyRules<K> {

    PathId pathId;

    public KeyRulesMap(Class<K> kClass, PathId pathId) {
        super(kClass);
        this.pathId = pathId;
    }

    @Override
    public PathId getPathId() {
        return pathId;
    }

    ModRule makeRule(K key, String rs) {
        return new ModRule(pathId.makeChild(key), rs);
    }

    ModRule addRule(K key, String rs) {
        ModRule r = makeRule(key, rs);
        put (key, r);
        return r;
    }

}

