package latin.choices;

import com.google.common.collect.ImmutableList;

import latin.util.Splitters;

import java.util.Map;
import java.util.function.Function;

public class KeyAltsBuilder<K,V> {

    final Function<String, K> toKey;
    final Function<String, V> toVal;
    final Function<K, Object> toId;
    final Map<K,Alts<V>> kvmap;

    public KeyAltsBuilder(Function<String, K> toKey,
                          Function<String, V> toVal,
                          Function<K, Object> toId,
                          Map<K,Alts<V>> kvmap) {
        this.toKey = toKey;
        this.toVal = toVal;
        this.toId = toId;
        this.kvmap = kvmap;
    }

    public KeyAltsBuilder<K,V> put(K key, Alts<V> val) {
        kvmap.put(key, val);
        return this;
    }

    public KeyAltsBuilder<K,V> putAll(Map<K,Alts<V>> omap) {
        kvmap.putAll(omap);
        return this;
    }

    private Alts<V> makeAlts(K key, ImmutableList<V> vl) {
        return new AltsList<>(toId.apply(key), vl);
    }

    public void putMade(K key, ImmutableList<V> vl) {
        put(key, makeAlts(key, vl));
    }

    public KeyAltsBuilder<K,V> add(String ss) {
        Splitters.ecsplit(ss, toKey, toVal, this::putMade);
        return this;
    }
}

