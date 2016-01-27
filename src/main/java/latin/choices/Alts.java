package latin.choices;

import com.google.common.math.IntMath;

import java.util.function.BiConsumer;

public interface Alts<T> extends RecordAlts, Chooser.Choose<T> {

    public int size();
    public T get(int index);
    public Object getId();

    @Override
    default void recordAlts(BiConsumer<Object,Integer> bic) {
        bic.accept(getId(), size());
    }

    @Override
    default T choose(Chooser chooser) {
        int s = size();
        if (s == 1) {
            return get(0);
        }
        else {
            return get(IntMath.mod(chooser.get(getId()), s));
        }
    }
}