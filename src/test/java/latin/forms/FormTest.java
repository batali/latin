package latin.forms;

import com.google.common.collect.Iterables;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import latin.choices.Chooser;

import java.util.Arrays;


public class FormTest {

    private static Logger logger = LoggerFactory.getLogger(FormTest.class);

    StringForm s1;
    StringForm s2;

    ModRule r1;
    ModRule r2;

    static Chooser firstChooser = new Chooser() {
        @Override
        public Integer get(Object key) {
            return 0;
        }
    };

    static Chooser lastChooser = new Chooser() {
        @Override
        public Integer get(Object key) {
            return -1;
        }
    };

    static StringForm makeStringForm(String cs) {
        return new StringForm(cs,cs);
    }

    static ModRule makeRule(String cs) {
        return new ModRule(cs,cs);
    }

    @Before
    public void setup() {
        s1 = makeStringForm("ant");
        s2 = makeStringForm("dog,cat");
        r1 = makeRule("eater");
        r2 = makeRule("-,s");
    }

    void checkForm(String msg, Form f, String... targetStrings) {
        Assert.assertTrue(msg, Iterables.elementsEqual(f, Arrays.asList(targetStrings)));
    }

    @Test
    public void testForms() {
        Form r1s1 = r1.apply(s1);
        Form r1s2 = r1.apply(s2);
        checkForm("r1(s1)", r1s1, "anteater");
        checkForm("r1(s2)", r1s2, "dogeater", "cateater");
        Form r2s1 = r2.apply(s1);
        checkForm("r2(s1)", r2s1, "an", "ants");
        Form r2s2 = r2.apply(s2);
        checkForm("r2(s2)", r2s2, "do", "dogs", "ca", "cats");
        Assert.assertEquals("anteater", r1s1.choose(firstChooser));
        Assert.assertEquals("dogeater", r1s2.choose(firstChooser));
        Assert.assertEquals("an", r2s1.choose(firstChooser));
        Assert.assertEquals("do", r2s2.choose(firstChooser));
        Assert.assertEquals("cateater", r1s2.choose(lastChooser));
        Assert.assertEquals("cats", r2s2.choose(lastChooser));
    }

}