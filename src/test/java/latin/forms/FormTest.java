package latin.forms;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import latin.choices.Chooser;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;


public class FormTest {

    private static Logger logger = LoggerFactory.getLogger(FormTest.class);

    Form s1;
    Form s2;

    Rule r1;
    Rule r2;

    static Chooser firstChooser = new Chooser() {
        @Override
        public Integer get(Object key) {
            return 0;
        }
    };

    static class LastChooser implements Chooser, BiConsumer<Object,Integer> {
        Map<Object,Integer> sizeMap;
        public LastChooser() {
            this.sizeMap = Maps.newHashMap();
        }
        public void accept(Object id, Integer size) {
            if (size > 1) {
                sizeMap.put(id, size - 1);
            }
        }
        public Integer get(Object id) {
            return sizeMap.get(id);
        }
    }

    @Before
    public void setup() {
        s1 = new StringForm("s1", "ant");
        s2 = new StringForm("s2", "dog,cat");
        r1 = new ModRule("r1", "eater");
        r2 = new ModRule("r2", "-,s");
    }

    void checkForm(String msg, Form f, String... targetStrings) {
        Assert.assertTrue(msg, Iterables.elementsEqual(f, Arrays.asList(targetStrings)));
    }

    String chooseLast(Form form) {
        LastChooser lastChooser = new LastChooser();
        form.recordAlts(lastChooser);
        return form.choose(lastChooser);
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
        Assert.assertEquals("cateater", chooseLast(r1s2));
        Assert.assertEquals("cats", chooseLast(r2s2));

    }



}