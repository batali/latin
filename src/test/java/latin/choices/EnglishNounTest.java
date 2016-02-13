package latin.choices;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class EnglishNounTest {

    private static Logger logger = LoggerFactory.getLogger(EnglishNounTest.class);

    void compareForms(English.CountNounEntry e, String ts, String tp) {
        Assert.assertEquals(ts, e.getSingular());
        Assert.assertEquals(tp, e.getPlural());
    }

    void compareForms(String es, String ts, String tp) {
        compareForms(English.parseCountNounEntry(es), ts, tp);
    }

    @Test
    public void testRegularEntry() {
        compareForms("dog", "dog", "dogs");
        compareForms("try", "try", "tries");
        compareForms("kiss", "kiss", "kisses");
        compareForms("match", "match", "matches");
        English.CountNounEntry e1 = English.parseCountNounEntry("dog,dogs");
        Assert.assertTrue(e1 instanceof English.RegularCountNounEntry);
    }

    @Test
    public void testUnchanged() {
        English.CountNounEntry e1 = English.parseCountNounEntry("fish,fish");
        Assert.assertTrue(e1 instanceof English.UnchangedCountNounEntry);
        compareForms(e1, "fish", "fish");
    }

    @Test
    public void testIrregular() {
        compareForms("man,men", "man", "men");
        compareForms("wolf,wolves", "wolf", "wolves");
    }
}