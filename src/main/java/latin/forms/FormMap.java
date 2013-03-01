
package latin.forms;

import com.google.common.base.Preconditions;

import java.util.EnumMap;
import java.util.List;

public class FormMap<KT extends Enum<KT>> extends EnumMap<KT, Formf> implements StoredForms<KT> {

    public FormMap (Class<KT> ktClass) {
        super(ktClass);
    }

    public Formf getStored(KT key) {
        return get(key);
    }

    public void putForm(String prefix, KT key, List<String> sl) {
        put (key, Suffix.makeFormf(prefix, key, sl));
    }

    public void putForm(String prefix, KT key, String fs) {
        putForm(prefix, key, Suffix.csplit(fs));
    }

    public void putForms(String prefix, KT[] values, List<String> sl) {
        int n = sl.size();
        Preconditions.checkState(n == values.length);
        for (int i = 0; i < n; i++) {
            putForm(prefix, values[i], sl.get(i));
        }
    }

}
