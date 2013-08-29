package latin.choices;

import javax.annotation.Nullable;
import javax.management.InvalidApplicationException;

public interface ValueFunction<E,V> {
    public boolean test(E e);
    public V apply(E e, Alts.Chooser chooser) throws InvalidApplicationException;
    public @Nullable V testApply(E e, Alts.Chooser chooser);
}