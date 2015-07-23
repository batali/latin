package latin.choices;

import com.google.common.math.IntMath;

import java.util.List;

public interface Chooser {

    Integer get(Object key);

    default <T> T choose (List<T> tList, Object id) {
        int s = tList.size();
        if (s == 1) {
            return tList.get(0);
        }
        else {
            return tList.get(IntMath.mod(get(id), s));
        }
    }

    interface Choose<T> {
        T choose(Chooser chooser);
    }
}