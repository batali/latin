package latin.forms;

import com.google.common.collect.Lists;

import org.junit.Test;

import junit.framework.Assert;

public class ModTest {

    void checkMod(Mod m, String s, String t) {
        Assert.assertEquals(m.getSpec(), t, m.apply(s));
    }

    static final Mod.Ending adda = new Mod.Ending("a");
    static final Mod.Ending addb = new Mod.Ending("b");

    @Test
    public void testSimpleMods() {
        checkMod(Mod.noop, "foo", "foo");
        checkMod(Mod.butOne, "foo", "fo");
    }

    @Test
    public void testEndings() {
        checkMod(adda, "foo", "fooa");
        checkMod(addb, "foo", "foob");
    }

    @Test
    public void testSequence() {
        Mod.ModSeq modSeq = new Mod.ModSeq(Lists.newArrayList(adda, addb));
        checkMod(modSeq, "foo", "fooab");
    }

    @Test
    public void testAccentMods() {
        checkMod(Mod.accentLast, "foo", "foō");
        checkMod(Mod.accentLast, "FOO", "FOŌ");
        checkMod(Mod.accentLast, "foā", "foā");
        checkMod(Mod.accentLast, "FOĒ", "FOĒ");
        checkMod(Mod.accentLast, "bar", "bar");
        checkMod(Mod.unaccentLast, "baā", "baa");
        checkMod(Mod.unaccentLast, "BAĒ", "BAE");
        checkMod(Mod.unaccentLast, "baa", "baa");
        checkMod(Mod.unaccentLast, "BAI", "BAI");
        checkMod(Mod.unaccentLast, "bar", "bar");
    }

    Mod checkParseMod(String ms) {
        Mod m = Mod.parseMod(ms);
        Assert.assertEquals(ms, m.getSpec());
        return m;
    }

    @Test
    public void testParsedMod() {
        Mod m1 = checkParseMod("a");
        checkMod(m1, "eep", "eepa");
        Mod m2 = checkParseMod("<b");
        checkMod(m2, "boa", "boāb");
        Mod m3 = checkParseMod(">c");
        checkMod(m3, "evē", "evec");
        Mod m4 = checkParseMod("-er");
        checkMod(m4, "patr", "pater");
        Mod m5 = checkParseMod("---a");
        checkMod(m5, "alfa", "aa");
        Mod m6 = checkParseMod("+ed");
        checkMod(m6, "pop", "popped");
    }

}