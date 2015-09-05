package latin.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

public interface ImmutableIterable<T> extends Iterable<T> {
    @Override
    public UnmodifiableIterator<T> iterator();

    default
    ImmutableList<T> asList() {
        return ImmutableList.copyOf(this);
    }

}