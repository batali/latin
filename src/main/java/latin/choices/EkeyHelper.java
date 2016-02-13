
package latin.choices;

import com.google.common.collect.Maps;

import java.util.Map;

public class EkeyHelper {

    private EkeyHelper() {
    }

    public static <E extends Enum<E>> Map<String, E> stringToEnumMap(Class<E> eClass) {
        Map<String,E> imap = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
        for (E e : eClass.getEnumConstants()) {
            imap.put(e.toString(), e);
        }
        return imap;
    }

    public static <E extends Enum<E>> int indexFromString(E[] ekeys, String ks) {
        for (int i = 0; i < ekeys.length; i++) {
            if (ekeys[i].name().equalsIgnoreCase(ks)) {
                return i;
            }
        }
        return -1;
    }

    public static <E extends Enum<E>> E ekeyFromString(Class<E> cls, String ks, boolean errorp) {
        try {
            return Enum.valueOf(cls, ks);
        }
        catch(IllegalArgumentException iae) {
            E[] values = cls.getEnumConstants();
            int i = indexFromString(values, ks);
            if (i >= 0) {
                return values[i];
            }
            else if (errorp) {
                throw iae;
            }
            else {
                return null;
            }
        }
    }

    public static <E extends Enum<E>> E ekeyFromString(Class<E> cls, String ks) {
        return ekeyFromString(cls, ks, true);
    }

}