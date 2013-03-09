
package latin.veritas;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PsettingTest {

    @Test
    public void testPsettingList() {
        String s1 = "(A & B) -> C";
        StringParser sp = new StringParser(s1);
        PropExpression pe = PropParser.parseProp(sp);
        System.out.println(pe.asList().toString());
        List<List<Psetting>> psl = pe.getCnf(true, Psetting.simpleHandler);
        System.out.println(psl.toString());
    }

    public List<Psetting> parseSettingList(String sls) throws Exception {
        StringParser stringParser = new StringParser(sls);
        List<Psetting> settings = PropParser.parseSettingList(stringParser, Psetting.simpleHandler);
        return Psetting.sortSettingList(settings);
    }

    public Psetting parseSetting(String ss) throws Exception {
        StringParser stringParser = new StringParser(ss);
        return PropParser.parseSetting(stringParser, Psetting.simpleHandler);
    }

    @Test
    public void testParseSettingList() throws Exception {
        List<Psetting> settings = parseSettingList("p q !p a!=4 a=4 a=3 !b=v !b!=v");
        Assert.assertEquals("[a=3, a=4, a!=4, b=v, b!=v, p, !p, q]", settings.toString());
    }

    @Test
    public void testMergeSettings() throws Exception {
        List<Psetting> sl1 = parseSettingList("p q");
        List<Psetting> sl2 = parseSettingList("q r");
        List<Psetting> sl3 = parseSettingList("!q r");
        List<Psetting> sl4 = parseSettingList("!q s t");
        List<Psetting> sl12 = Psetting.mergeSettingLists(sl1, sl2);
        Assert.assertEquals("[p, q, r]", sl12.toString());
        List<Psetting> sl13 = Psetting.mergeSettingLists(sl1, sl3);
        Assert.assertNull(sl13);
        List<Psetting> sl34 = Psetting.mergeSettingLists(sl3, sl4);
        Assert.assertEquals("[!q, r, s, t]", sl34.toString());
    }

    @Test
    public void testFindProp() throws Exception {
        List<Psetting> sl = parseSettingList("p !q r s !t");
        int sls = sl.size();
        Psetting pp = parseSetting("p");
        Psetting pn = parseSetting("!p");
        Assert.assertEquals(0, Psetting.findProp(pp, sl, 0, sls));
        Assert.assertEquals(0, Psetting.findProp(pn, sl, 0, sls));
        Psetting rp = parseSetting("r");
        Assert.assertEquals(2, Psetting.findProp(rp, sl, 0, sls));
        Assert.assertEquals(2, Psetting.findProp(rp, sl, 2, sls));
        Assert.assertEquals(-1, Psetting.findProp(rp, sl, 2, 2));
    }

    @Test
    public void testSubset() throws Exception {
        List<Psetting> sl1 = parseSettingList("p r t");
        Assert.assertTrue(Psetting.isSubset(parseSettingList("p r t"), sl1));
        Assert.assertTrue(Psetting.isSubset(parseSettingList("p r"), sl1));
        Assert.assertTrue(Psetting.isSubset(parseSettingList("p t"), sl1));
        Assert.assertTrue(Psetting.isSubset(parseSettingList("r t"), sl1));
        Assert.assertTrue(Psetting.isSubset(parseSettingList("p"), sl1));
        Assert.assertFalse(Psetting.isSubset(parseSettingList("p q"), sl1));
        Assert.assertFalse(Psetting.isSubset(parseSettingList("!p r t"), sl1));
        Assert.assertFalse(Psetting.isSubset(parseSettingList("t z"), sl1));
    }

    @Test
    public void testAdjoin() throws Exception {
        List<Psetting> sl1 = parseSettingList("p q r");
        List<Psetting> sl2 = parseSettingList("r s t");
        List<Psetting> sl3 = parseSettingList("t u v");
        List<List<Psetting>> sll1 = Lists.newArrayList();
        Psetting.adjoinSettingList(sl1, sll1);
        Psetting.adjoinSettingList(sl2, sll1);
        Psetting.adjoinSettingList(sl3, sll1);
        Assert.assertEquals("[[p, q, r], [r, s, t], [t, u, v]]", sll1.toString());
        List<Psetting> sl4 = parseSettingList("p q r s");
        Psetting.adjoinSettingList(sl4, sll1);
        Assert.assertEquals("[[p, q, r], [r, s, t], [t, u, v]]", sll1.toString());
        List<Psetting> sl5 = parseSettingList("p q");
        Psetting.adjoinSettingList(sl5, sll1);
        Psetting.sortNormalForm(sll1);
        Assert.assertEquals("[[p, q], [r, s, t], [t, u, v]]", sll1.toString());
    }

    @SuppressWarnings("unchecked")
    private static List<Pair<String,String>> cnfPairs = Lists.newArrayList(
            Pair.of("p & q", "[[p], [q]]"),
            Pair.of("p | q", "[[p, q]]"),
            Pair.of("p->q", "[[!p, q]]"),
            Pair.of("!(p->q)", "[[p], [!q]]"),
            Pair.of("p==q", "[[p, !q], [!p, q]]"),
            Pair.of("p^q", "[[p, q], [!p, !q]]"),
            Pair.of("(p & q) == r", "[[p, !r], [!p, !q, r], [q, !r]]"),
            Pair.of("p->(q & r)", "[[!p, q], [!p, r]]"),
            Pair.of("(p & q) -> r", "[[!p, !q, r]]"),
            Pair.of("p->(q->r)", "[[!p, !q, r]]"),
            Pair.of("p & q | r & s | !q & (p | t)",
                    "[[p, !q, r], [p, !q, s], [p, r, t], [p, s, t]]"),
            Pair.of("p & q | p & !q", "[[p]]"),
            Pair.of("!(p | !q)", "[[!p], [q]]"),
            Pair.of("(p & q) -> r", "[[!p, !q, r]]")
    );

    @Test
    public void testNormalForms() throws Exception {
        for (Pair<String,String> cnfPair : cnfPairs) {
            String propString = cnfPair.getLeft();
            String targetString = cnfPair.getRight();
            StringParser stringParser = new StringParser(propString);
            PropExpression pe = PropParser.parseProp(stringParser);
            List<List<Psetting>> sll = pe.getCnf(true, Psetting.simpleHandler);
            Assert.assertEquals(propString, targetString, sll.toString());
        }
    }

    public static Psetting.GetSetting<String> shandler = new Psetting.GetSetting<String>() {

        public String bstring(boolean bv) {
            return bv ? "T" : "F";
        }

        @Override
        public String getBooleanSetting(String pathString, boolean sv) {
            return String.format("%s:%s", pathString, bstring(sv));
        }

        @Override
        public String getValueSetting(String pathString, String choiceName, boolean sv) {
            return String.format("%s=%s:%s", pathString, choiceName, bstring(sv));
        }
    };

    @SuppressWarnings("unchecked")
    private static List<Pair<String,String>> trPairs = Lists.newArrayList(
            Pair.of("p & q", "[[p], [q]]"),
            Pair.of("p->q", "[[!p, q]]"),
            Pair.of("f=3 & g!=e", "[[f=3], [g!=e]]")
    );

    @Test
    public void testTransform() throws Exception {
        for (Pair<String,String> cnfPair : trPairs) {
            String propString = cnfPair.getLeft();
            String targetString = cnfPair.getRight();
            StringParser stringParser = new StringParser(propString);
            PropExpression pe = PropParser.parseProp(stringParser);
            List<List<Psetting>> sll = pe.getCnf(true, Psetting.simpleHandler);
            List<List<String>> tll = Psetting.transformCnf(sll, shandler);
            System.out.println("sll " + sll.toString());
            System.out.println("tss " + targetString);
            System.out.println("tll " + tll.toString());
        }
    }
}
