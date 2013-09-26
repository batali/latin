
package latin.forms;

import com.google.common.base.Function;

import latin.choices.IdValuesList;
import latin.choices.KeyValues;
import latin.choices.Value;
import latin.choices.Values;
import latin.util.PathId;

import java.util.List;
import java.util.Map;

public class ValueMapBuilder<KT,VT> {

    public final PathId.Element path;
    public final Map<KT,Values<VT>> valuesMap;
    final Function<Object, KT> toKeyFunction;
    final Function<? super String, VT> toValueFunction;

    public ValueMapBuilder(PathId.Element path,
                           Map<KT,Values<VT>> valuesMap,
                           Function<Object, KT> toKeyFunction,
                           Function<? super String, VT> toValueFunction) {
        this.path = path;
        this.valuesMap = valuesMap;
        this.toKeyFunction = toKeyFunction;
        this.toValueFunction = toValueFunction;
    }

    public void putValues(Object k, List<VT> valuesList) {
        KT key = toKeyFunction.apply(k);
        IdValuesList<VT> ivl = new IdValuesList<VT>(path.makeChild(key), valuesList);
        valuesMap.put(key, ivl);
    }

    public void putValues(Object k, String vs) {
        putValues(k, Suffix.csplit(vs, toValueFunction));
    }

    public void putAll(Map<KT,Values<VT>> om) {
        valuesMap.putAll(om);
    }

    public static <KT,VT> KeyValues<KT,VT> makeKeyValues(final Map<KT,Values<VT>> m) {
        return new KeyValues<KT, VT>() {
            @Override
            public Value<VT> getValue(KT key) {
                return m.get(key);
            }
            @Override
            public boolean containsKey(KT key) {
                return m.containsKey(key);
            }
        };
    }

}
