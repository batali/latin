package latin.veritas;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SettingSpecTest {

    public List<SettingSpec> parseSettingList(String sls) throws Exception {
        StringParser stringParser = new StringParser(sls);
        List<SettingSpec> settings = PropParser.parseSettingList(stringParser);
        return NormalForm.sortSettingSpecs(settings);
    }

    public SettingSpec parseSetting(String ss) throws Exception {
        StringParser stringParser = new StringParser(ss);
        return PropParser.parseSetting(stringParser);
    }

    @Test
    public void testParseSettingList() throws Exception {
        List<SettingSpec> settings = parseSettingList("p q !p a!=4 a=4 a=3 !b=v !b!=v");
        Assert.assertEquals("[a=3, a=4, a!=4, b=v, b!=v, p, !p, q]", settings.toString());
    }

    @Test
    public void testMergeSettings() throws Exception {
        List<SettingSpec> sl1 = parseSettingList("p q");
        List<SettingSpec> sl2 = parseSettingList("q r");
        List<SettingSpec> sl3 = parseSettingList("!q r");
        List<SettingSpec> sl4 = parseSettingList("!q s t");
        List<SettingSpec> sl12 = NormalForm.mergeSettingLists(sl1, sl2);
        Assert.assertEquals("[p, q, r]", sl12.toString());
        List<SettingSpec> sl13 = NormalForm.mergeSettingLists(sl1, sl3);
        Assert.assertNull(sl13);
        List<SettingSpec> sl34 = NormalForm.mergeSettingLists(sl3, sl4);
        Assert.assertEquals("[!q, r, s, t]", sl34.toString());
    }

    @Test
    public void testFindProp() throws Exception {
        List<SettingSpec> sl = parseSettingList("p !q r s !t");
        int sls = sl.size();
        SettingSpec pp = parseSetting("p");
        SettingSpec pn = parseSetting("!p");
        Assert.assertEquals(0, NormalForm.findProp(pp, sl, 0, sls));
        Assert.assertEquals(0, NormalForm.findProp(pn, sl, 0, sls));
        SettingSpec rp = parseSetting("r");
        Assert.assertEquals(2, NormalForm.findProp(rp, sl, 0, sls));
        Assert.assertEquals(2, NormalForm.findProp(rp, sl, 2, sls));
        Assert.assertEquals(-1, NormalForm.findProp(rp, sl, 2, 2));
    }

    @Test
    public void testSubset() throws Exception {
        List<SettingSpec> sl1 = parseSettingList("p r t");
        Assert.assertTrue(NormalForm.isSubset(parseSettingList("p r t"), sl1));
        Assert.assertTrue(NormalForm.isSubset(parseSettingList("p r"), sl1));
        Assert.assertTrue(NormalForm.isSubset(parseSettingList("p t"), sl1));
        Assert.assertTrue(NormalForm.isSubset(parseSettingList("r t"), sl1));
        Assert.assertTrue(NormalForm.isSubset(parseSettingList("p"), sl1));
        Assert.assertFalse(NormalForm.isSubset(parseSettingList("p q"), sl1));
        Assert.assertFalse(NormalForm.isSubset(parseSettingList("!p r t"), sl1));
        Assert.assertFalse(NormalForm.isSubset(parseSettingList("t z"), sl1));
    }

    @Test
    public void testAdjoin() throws Exception {
        List<SettingSpec> sl1 = parseSettingList("p q r");
        List<SettingSpec> sl2 = parseSettingList("r s t");
        List<SettingSpec> sl3 = parseSettingList("t u v");
        List<List<SettingSpec>> sll1 = Lists.newArrayList();
        NormalForm.adjoinSettingList(sl1, sll1);
        NormalForm.adjoinSettingList(sl2, sll1);
        NormalForm.adjoinSettingList(sl3, sll1);
        Assert.assertEquals("[[p, q, r], [r, s, t], [t, u, v]]", sll1.toString());
        List<SettingSpec> sl4 = parseSettingList("p q r s");
        NormalForm.adjoinSettingList(sl4, sll1);
        Assert.assertEquals("[[p, q, r], [r, s, t], [t, u, v]]", sll1.toString());
        List<SettingSpec> sl5 = parseSettingList("p q");
        NormalForm.adjoinSettingList(sl5, sll1);
        NormalForm.sortNormalForm(sll1);
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
            List<List<SettingSpec>> sll = pe.getCnf(true);
            Assert.assertEquals(propString, targetString, sll.toString());
        }
    }

}