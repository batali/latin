
package latin.forms;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenTest {

    void checkToString(Token tok, String target) {
        String toks = tok.toString();
        assertEquals(target, toks);
        for (int i = 0; i < toks.length(); i++) {
            assertEquals(toks.charAt(i), tok.charAt(i));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tok);
        assertEquals(target, sb.toString());
    }

    @Test
    public void testAddedStringToken() {
        String e = "boof";
        StringToken at = new StringToken(e);
        assertEquals(4, at.length());
        checkToString(at, e);
        Token bt = new AddedStringToken(at, "er");
        checkToString(bt, "boofer");
    }

    @Test
    public void testAddStringFunction() {
        StringToken at = new StringToken("barf");
        StringToken r1 = new StringToken("ing");
        StringToken r2 = new StringToken("ly");
        Token t1 = r1.apply(at);
        checkToString(t1, "barfing");
        Token t2 = r2.apply(t1);
        checkToString(t2, "barfingly");
    }

    @Test
    public void testSubToken() {
        String s = "abalfa";
        StringToken at = new StringToken(s);
        SubToken st = new SubToken(at, 1, at.endOffset(2));
        checkToString(st, "bal");
    }

    @Test
    public void testReplacedCharToken() {
        String s = "dubstep";
        StringToken at = new StringToken(s);
        ReplacedCharToken rt = new ReplacedCharToken(at, 1, 'a');
        checkToString(rt, "dabstep");
    }

    @Test
    public void testSubReplacedCharToken() {
        String s = "duboobstep";
        StringToken at = new StringToken(s);
        ReplacedCharToken rt = new ReplacedCharToken(at, 4, 'a');
        SubToken st1 = new SubToken(rt, 1, rt.endOffset(1));
        checkToString(st1, "uboabste");
        SubToken st2 = new SubToken(rt, 5, rt.length());
        checkToString(st2, "bstep");
        SubToken st3 = new SubToken(rt, 0, 5);
        checkToString(st3, "duboa");
    }

    @Test
    public void testSubAdded() {
        String s = "abalfa";
        Token at = new StringToken(s);
        Token bt = new AddedStringToken(at, "cba");
        SubToken st1 = new SubToken(bt, 1, bt.endOffset(1));
        checkToString(st1, "balfacb");
        ReplacedCharToken rt = new ReplacedCharToken(st1, st1.endOffset(1), 'w');
        checkToString(rt, "balfacw");
    }

    @Test
    public void testMatches() {
        String s = "abalfaa";
        Token at = new StringToken(s);
        assertTrue(Suffix.endMatches(at, "CaV"));
        assertTrue(Suffix.startMatches(at, "VCV"));
    }

    @Test
    public void testAccenter() {
        Token u = new StringToken("ama");
        Token a = new StringToken("amā");
        Token au = Suffix.accentLastVowel(u);
        checkToString(au, "amā");
        Token ua = Suffix.unaccentLastVowel(a);
        checkToString(ua, "ama");
        Token uu = Suffix.unaccentLastVowel(u);
        checkToString(uu, "ama");
        Token aa = Suffix.accentLastVowel(a);
        checkToString(aa, "amā");
        Token an = new StringToken("animal");
        Token av = Suffix.accenterFunction.apply(an);
        checkToString(av, "animāl");
    }

    @Test
    public void testRemoveLast() {
        Token t = new StringToken("foo");
        Token s1 = TokenRules.removeLastRule.apply(t);
        Token s2 = TokenRules.removeLastRule.apply(s1);
        checkToString(s1, "fo");
        checkToString(s2, "f");
    }

    @Test
    public void testSequenceRule() {
        StringToken st = new StringToken("ies");
        TokenRule tf = TokenRules.makeRule(Arrays.asList(TokenRules.removeLastRule, st));
        StringToken at = new StringToken("try");
        Token rt = tf.apply(at);
        checkToString(rt, "tries");
    }

}