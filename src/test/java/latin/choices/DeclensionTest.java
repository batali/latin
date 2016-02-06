package latin.choices;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class DeclensionTest {

    private static Logger logger = LoggerFactory.getLogger(DeclensionTest.class);

    @Test
    public void testRules () {
        Declension.Rules firsta = Declension.First.getRules("a");
        Assert.assertEquals(Declension.First, firsta.getDeclension());
        Assert.assertEquals("a", firsta.getSubname());
    }

}