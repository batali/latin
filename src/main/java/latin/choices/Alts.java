
package latin.choices;

import com.google.common.base.Preconditions;

import java.util.List;

public abstract class Alts {

    private Alts() {
    }

    public static <T> T chooseElement(List<T> tlist, Object id, Chooser chooser) {
        int s = (tlist == null) ? 0 : tlist.size();
        if (s == 0) {
            return null;
        }
        else {
            return tlist.get((s == 1) ? 0 : chooser.getAltsIndex(id, s));
        }
    }

    public static <T> T chooseElement(Values<T> values, Chooser chooser) {
        int s = (values == null) ? 0 : values.size();
        if (s == 0) {
            return null;
        }
        else if (s == 1) {
            return values.get(0);
        }
        else {
            return values.get(chooser.getAltsIndex(values.getId(), s));
        }
    }

    public interface Chooser {
        int getAltsIndex(Object id, int n);
    }

    public static final Chooser firstAlt = new Chooser() {
        @Override
        public int getAltsIndex(Object id, int n) {
            Preconditions.checkArgument(n > 0);
            return 0;
        }
    };

    public static final Chooser lastAlt = new Chooser() {
        @Override
        public int getAltsIndex(Object id, int n) {
            Preconditions.checkArgument(n>0);
            return n-1;
        }
    };


}
