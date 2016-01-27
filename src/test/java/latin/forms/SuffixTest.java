
package latin.forms;

import org.junit.Assert;
import org.junit.Test;

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

    void checkAccented(char tc, char fc) {
        Assert.assertEquals(tc, Suffix.accented(fc));
        if (Suffix.isVowel(fc)) {
            Assert.assertTrue(Suffix.isAccented(tc));
        }
        else {
            Assert.assertEquals(tc, fc);
        }
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
        if (Suffix.isVowel(fc)) {
            Assert.assertFalse(Suffix.isAccented(tc));
        }
        else {
            Assert.assertEquals(tc, fc);
        }
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
        String fb1 = "alfabalf";
        Assert.assertTrue(Suffix.startMatches(fb1,"VC"));
        Assert.assertTrue(Suffix.endMatches(fb1, "V*C"));
    }

    @Test
    public void testUnaccentString() {
        Assert.assertEquals("aba", Suffix.unaccentString("abā"));
        Assert.assertEquals("a_ba", Suffix.unaccentString("a bā"));
    }

}