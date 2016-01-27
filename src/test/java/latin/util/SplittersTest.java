package latin.util;

import com.google.common.collect.Iterables;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class SplittersTest {

    static List<String> stringList(String... strings) {
        return Arrays.asList(strings);
    }

    static <T> void assertListsEqual(Iterable<T> expected, Iterable<T> value) {
        Assert.assertTrue(Iterables.elementsEqual(expected, value));
    }

    static <T> void assertListsEqual(String msg, Iterable<T> expected, Iterable<T> value) {
        Assert.assertTrue(msg, Iterables.elementsEqual(expected, value));
    }

    @Test
    public void testCsplitter () {
        assertListsEqual(stringList("a"), Splitters.csplit("a"));
        assertListsEqual(stringList("a", "b", "c"), Splitters.csplit("a,b ,c"));
        assertListsEqual(stringList(), Splitters.csplit(" "));
    }

    static void checkEsplit(String es, final String tks, final String tvs) {
        BiConsumer<String,String> biConsumer = new BiConsumer<String, String>() {
            @Override
            public void accept(String vks, String vvs) {
                Assert.assertEquals("key mismatch", tks, vks);
                Assert.assertEquals("val mismatch", tvs, vvs);
            }
        };
        Splitters.esplit(es, biConsumer);
    }

    @Test
    public void testEsplit() {
        checkEsplit("foo=bar", "foo", "bar");
        checkEsplit("alf=", "alf", "");
    }

    @Test
    public void testSplitter() {
        assertListsEqual("single", stringList("a"), Splitters.ssplit("a"));
        assertListsEqual("trim end", stringList("a"), Splitters.ssplit("a "));
        assertListsEqual("empty", stringList(), Splitters.ssplit(""));
        assertListsEqual("multi", stringList("a", "b"), Splitters.ssplit("a b"));
        assertListsEqual("multi trim", stringList("a", "b"), Splitters.ssplit(" a  b "));
    }

}