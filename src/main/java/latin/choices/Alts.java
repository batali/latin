
package latin.choices;

import com.google.common.base.Preconditions;

import latin.util.Identified;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Alts<T> extends RecordAlts, Chooser.Choose<T> {

    Object getId();
    int size();
    T get(int index);

    @Override
    default void recordAlts(BiConsumer<Object,Integer> bic) {
        bic.accept(getId(), size());
    }

    @Override
    default void recordAlts(Consumer<Alts<?>> aic) {
        aic.accept(this);
    }

    @Override
    default T choose(latin.choices.Chooser chooser) {
        if (size() == 1) {
            return get(0);
        }
        else {
            Integer i = chooser.get(getId());
            return get(i);
        }
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
    public static <T> T chooseElement(List<T> values, Identified identified, Chooser chooser) {
        int s = (values == null) ? 0 : values.size();
        if (s == 0) {
            return null;
        }
        else if (s == 1) {
            return values.get(0);
        }
        else {
            return values.get(chooser.getAltsIndex(identified.getId(), s));
        }
    }

    public static <T> T chooseElement(Values<T> values, Chooser chooser) {
        return chooseElement(values, values, chooser);
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
