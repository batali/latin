package latin.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Splitters {

    public static <V> Iterable<V> csplitter(String cs,
                                            com.google.common.base.Function<String,V> toValue) {
        cs = cs.trim();
        if (cs.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Iterables.transform(Splitter.on(',').trimResults().split(cs), toValue);
        }
    }

    public static Iterable<String> csplitter(String cs) {
        return csplitter(cs, com.google.common.base.Functions.identity());
    }

    public static <V> ImmutableList<V> csplit(String cs, Function<String, V> toValue) {
        ImmutableList.Builder<V> builder = ImmutableList.builder();
        for (String c : csplitter(cs)) {
            builder.add(toValue.apply(c));
        }
        return builder.build();
    }

    public static Iterable<String> ssplitter(String str) {
        return Splitter.on(CharMatcher.BREAKING_WHITESPACE).omitEmptyStrings().split(str);
    }

    public static void esplit(String es, BiConsumer<String,String> consumer) {
        int p = es.indexOf('=');
        Preconditions.checkState(p > 0);
        consumer.accept(es.substring(0, p), es.substring(p+1));
    }

    public static <K, V> void ecsplit(String ss,
                                      Function<String, K> toKey,
                                      Function<String, V> toVal,
                                      BiConsumer<K, ImmutableList<V>> bcc) {
        for (String s : ssplitter(ss)) {
            int p = s.indexOf('=');
            Preconditions.checkState(p > 0);
            bcc.accept(toKey.apply(s.substring(0, p)), csplit(s.substring(p + 1), toVal));
        }
    }
}




