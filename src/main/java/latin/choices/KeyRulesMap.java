package latin.choices;

import com.google.common.base.Preconditions;

import latin.forms.ModRule;
import latin.forms.Rule;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

public class KeyRulesMap<K extends Enum<K>> extends EnumMap<K, Rule> implements KeyRules<K> {

    PathId pathId;
    public KeyRulesMap(Class<K> kClass, PathId pathId) {
        super(kClass);
        this.pathId = pathId;
    }

    @Override
    public PathId getPathId() {
        return pathId;
    }

    Rule makeRule(K key, String vs) {
        return new ModRule(pathId.makeChild(key), vs);
    }

    void addRule(String ks, String vs, Function<String,K> toKey) {
        K key = toKey.apply(ks);
        Rule r = makeRule(key, vs);
        put(key, r);
    }

    KeyRulesMap<K> use(@Nullable String ruleNames, Function<String, Map<K,Rule>> toRules) {
        if (ruleNames != null) {
            for (String rn : Splitters.ssplitter(ruleNames)) {
                Map<K, Rule> fromMap = toRules.apply(rn);
                Preconditions.checkNotNull(fromMap);
                putAll(fromMap);
            }
        }
        return this;
    }

    KeyRulesMap<K> add(String adds, final Function<String,K> toKey) {
        BiConsumer<String, String> sc = new BiConsumer<String, String>() {
            @Override
            public void accept(String ks, String vs) {
                addRule(ks, vs, toKey);
            }
        };
        Splitters.essplit(adds, sc);
        return this;
    }

}

