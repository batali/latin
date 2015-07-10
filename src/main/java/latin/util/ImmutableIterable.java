package latin.util;

import com.google.common.collect.UnmodifiableIterator;

public interface ImmutableIterable<T> extends Iterable<T> {
    @Override
    public UnmodifiableIterator<T> iterator();
}