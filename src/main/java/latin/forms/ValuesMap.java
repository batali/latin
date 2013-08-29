
package latin.forms;

import latin.choices.IdValuesList;
import latin.choices.Values;
import latin.choices.ValuesList;
import latin.util.PathId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValuesMap {

    public static class Builder<KT extends Enum<KT>,VT> {
        final Map<KT,Values<VT>> m;
        final Class<KT> ktClass;
        final PathId.Element path;
        public Builder(PathId.Element path, Class<KT> ktClass, Map<KT,Values<VT>> m) {
            this.m = m;
            this.ktClass = ktClass;
            this.path = path;
        }
        public Builder(PathId.Element path, Class<KT> ktClass) {
            this(path, ktClass, new HashMap<KT,Values<VT>>());
        }
        public Map<KT,Values<VT>> getMap() {
            return m;
        }
        public PathId.Element getPath() {
            return path;
        }
        KT getKey(Object x) {
            if (ktClass.isInstance(x)) {
                return ktClass.cast(x);
            }
            else {
                return Enum.valueOf(ktClass, x.toString());
            }
        }
        public ValuesList<VT> makeValuesList(Object n, List<VT> vals) {
            return new IdValuesList<VT>(path.makeChild(n), vals);
        }
        protected Builder put(Object k, List<VT> values) {
            KT key = getKey(k);
            m.put(key, makeValuesList(key, values));
            return this;
        }
        protected Builder putAll(Map<KT,Values<VT>> vm) {
            m.putAll(vm);
            return this;
        }
        protected Builder putAll(Builder<KT,VT> b) {
            return putAll(b.getMap());
        }
    }
}
