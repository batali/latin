package latin.util;

import com.google.common.collect.ImmutableList;

public interface AsImmutableList<T> extends ImmutableIterable<T> {
    ImmutableList<T> asList();
}