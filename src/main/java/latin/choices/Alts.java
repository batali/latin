
package latin.choices;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.function.BiConsumer;

public interface Alts<T> extends RecordAlts, Chooser.Choose<T> {

    Object getId();
    int size();
    T get(int index);

    @Override
    default void recordAlts(BiConsumer<Object,Integer> bic) {
        bic.accept(getId(), size());
    }

    @Override
    default T choose(Chooser chooser) {
        if (size() == 1) {
            return get(0);
        }
        else {
            Integer i = chooser.get(getId());
            return get(i);
        }
    }

    public static <T> T chooseElement(List<T> tlist, Object id, AltChooser altChooser) {
        int s = (tlist == null) ? 0 : tlist.size();
        if (s == 0) {
            return null;
        }
        else {
            return tlist.get((s == 1) ? 0 : altChooser.getAltsIndex(id, s));
        }
    }



    public interface AltChooser {
        int getAltsIndex(Object id, int n);
    }

    public static final AltChooser firstAlt = new AltChooser() {
        @Override
        public int getAltsIndex(Object id, int n) {
            Preconditions.checkArgument(n > 0);
            return 0;
        }
    };



}
