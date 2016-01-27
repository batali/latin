package latin.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.function.BiConsumer;

public class Splitters {

    public static FluentIterable<String> csplitter(String cs) {
        cs = cs.trim();
        if (cs.isEmpty()) {
            return FluentIterable.from(Collections.emptyList());
        } else {
            return FluentIterable.from(Splitter.on(',').trimResults().split(cs));
        }
    }

    public static ImmutableList<String> csplit(String cs) {
        return csplitter(cs).toList();
    }

    public static FluentIterable<String> ssplitter(String str) {
        return FluentIterable.from(Splitter.on(CharMatcher.BREAKING_WHITESPACE).omitEmptyStrings()
                                           .split(str));
    }

    public static ImmutableList<String> ssplit(String ss) {
        return ssplitter(ss).toList();
    }

    public static void esplit(String es, BiConsumer<String,String> consumer) {
        int p = es.indexOf('=');
        Preconditions.checkState(p > 0);
        consumer.accept(es.substring(0, p), es.substring(p+1));
    }

    public static void essplit(String ss, BiConsumer<String,String> consumer) {
        for (String es : ssplitter(ss)) {
            esplit(es, consumer);
        }
    }

}




