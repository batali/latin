
package latin.slots;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class ParsePropTest {

    @SuppressWarnings("unchecked")
    private static List<Pair<String,String>> parsePropPairs = Lists.newArrayList(
            Pair.of("p & q", "[&, p, q]"),
            Pair.of("p & q | r ", "[|, [&, p, q], r]"),
            Pair.of("p | q & r", "[|, p, [&, q, r]]"),
            Pair.of("p ->q", "[->, p, q]"),
            Pair.of(" p ^ q ", "[^, p, q]"),
            Pair.of("x= 4", "x=4"),
            Pair.of("x = c", "x=c"),
            Pair.of("x!=4", "[!, x=4]"),
            Pair.of("p==q", "[==, p, q]"),
            Pair.of("p!=q", "[!, p=q]"),
            Pair.of("p=q", "p=q"),
            Pair.of("(p & q & r) -> s","[->, [&, p, q, r], s]"),
            Pair.of("p & q | r | s&t", "[|, [&, p, q], r, [&, s, t]]"),
            Pair.of("((p))","p")
    );

    @Test
    public void testParseProp() throws Exception {
        for(Pair<String,String> parsePropPair : parsePropPairs) {
            String ps = parsePropPair.getLeft();
            StringParser stringParser = new StringParser(ps);
            PropExpression pe = PropParser.parseProp(stringParser);
            String tls = parsePropPair.getRight();
            String cls = pe.asList();
            Assert.assertEquals(ps, tls, cls);
        }
    }

}