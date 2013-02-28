
package latin.forms;

import com.google.common.collect.Lists;
import latin.choices.Alts;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SuffixTest {

    @Test
    public void testIsAccented() {
        String shouldBe = "āĀōŪ";
        for (int i = 0; i < shouldBe.length(); i++) {
            Assert.assertTrue(Suffix.isAccented(shouldBe.charAt(i)));
        }
        String shouldNotBe = "aAoUks3";
        for (int i = 0; i < shouldNotBe.length(); i++) {
            Assert.assertFalse(Suffix.isAccented(shouldNotBe.charAt(i)));
        }
    }

    @Test
    public void testIsVowel() {
        String shouldBe = "aēIŌūU";
        for (int i = 0; i < shouldBe.length(); i++) {
            Assert.assertTrue(Suffix.isVowel(shouldBe.charAt(i)));
        }
        String shouldNotBe = "bC3.";
        for (int i = 0; i < shouldNotBe.length(); i++) {
            Assert.assertFalse(Suffix.isVowel(shouldNotBe.charAt(i)));
        }
    }

    @Test
    public void testIsConsonant() {
        String shouldBe = "bcdFYZ";
        for (int i = 0; i < shouldBe.length(); i++) {
            Assert.assertTrue(Suffix.isConsonant(shouldBe.charAt(i)));
        }
        String shouldNotBe = "AēĪo3 ";
        for (int i = 0; i < shouldNotBe.length(); i++) {
            Assert.assertFalse(Suffix.isConsonant(shouldNotBe.charAt(i)));
        }
    }

    public void checkAccented(char tc, char fc) {
        Assert.assertEquals(tc, Suffix.lengthener.transform(fc));
    }

    @Test
    public void testAccented() {
        checkAccented('ā', 'a');
        checkAccented('Ā', 'A');
        checkAccented('b', 'b');
        checkAccented('ō', 'ō');
    }

    public void checkUnaccented(char tc, char fc) {
        Assert.assertEquals(tc, Suffix.unaccented(fc));
    }

    @Test
    public void testUnaccented() {
        checkUnaccented('e', 'ē');
        checkUnaccented('I', 'Ī');
        checkUnaccented('o', 'ō');
        checkUnaccented('u', 'u');
        checkUnaccented('b', 'b');
    }

    @Test
    public void testStartAndEnd() {
        FormBuilder fb1 = new FormBuilder().add("alfabalf");
        Assert.assertTrue(Suffix.startMatches(fb1,"VC"));
        Assert.assertTrue(Suffix.endMatches(fb1, "V*C"));
    }

    public void checkFormBuilder(String msg, String ts, IFormBuilder formBuilder) {
        Assert.assertEquals(msg, ts, formBuilder.getForm());
    }

    @Test
    public void testFormBuilder() {
        FormBuilder fb1 = new FormBuilder().add("boo");
        fb1.removeLast(2);
        checkFormBuilder("removeLast2", "b", fb1);
    }

    public void checkModString(String fs, String ms, String ts) {
        FormBuilder fb = new FormBuilder();
        fb.add(fs);
        Rulef rulef = FormRule.parseRule("", "", ms);
        rulef.apply(fb, Alts.firstAlt);
        Assert.assertEquals(fs + ":" + ms, ts, fb.getForm());
    }

    @Test
    public void testModString() {
        checkModString("foo", "baramatic", "foobaramatic");
        checkModString("ai", "<b", "aīb");
        checkModString("aī", "<b", "aīb");
        checkModString("aŪ", ">", "aU");
        checkModString("au", ">", "au");
        checkModString("pat", "+ed", "patted");
        checkModString("fly", "-ew", "flew");
        checkModString("x", "-", "");
        try {
            checkModString("ab", "=c", "abc");
            Assert.fail();
        }
        catch(IllegalArgumentException iae) {
            Assert.assertTrue(iae.getMessage().startsWith("bad mod string"));
        }
    }

    @Test
    public void testCsplit() {
        Assert.assertEquals("[a]", Suffix.csplit("a").toString());
        Assert.assertEquals("[ant, cow]", Suffix.csplit("ant,cow").toString());
        Assert.assertEquals("[]", Suffix.csplit("").toString());
        Assert.assertTrue(Suffix.csplit("  ").isEmpty());
        Assert.assertTrue(Suffix.csplit("").isEmpty());
        Assert.assertEquals("[, b]", Suffix.csplit(",b").toString());
        Assert.assertEquals("[foo bar]", Suffix.csplit("foo_bar").toString());
    }

    @Test
    public void testMakeFormf() {
        Formf formf = Suffix.makeFormf("", "", " ab_c");
        Assert.assertEquals("[ab c]", Suffix.getStrings(formf).toString());
    }

    @Test
    public void testSsplit() {
        Assert.assertEquals("[a, b]", Suffix.ssplit("a b").toString());
        Assert.assertEquals("[a, b]", Suffix.ssplit("a  b  ").toString());
        Assert.assertTrue(Suffix.ssplit("  ").isEmpty());
        Assert.assertTrue(Suffix.ssplit("").isEmpty());
        Assert.assertEquals("[a]", Suffix.ssplit("a ").toString());
        Assert.assertEquals("[a]", Suffix.ssplit(" a").toString());
    }

    @Test
    public void testEsplit() {
        List<Pair<String,String>> pairList = Lists.newArrayList(Suffix.esplitter("foo=3 bar=4,34 noog"));
        Assert.assertEquals("[(foo,3), (bar,4,34), (noog,)]", pairList.toString());
    }

}