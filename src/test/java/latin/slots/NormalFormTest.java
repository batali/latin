
package latin.slots;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class NormalFormTest {

    public static SlotSpecMap slotSpecMap;
    public static SettingHandler<ISetting> settingHandler;

    @BeforeClass
    public static void setup() {
        slotSpecMap = new SlotSpecMap();
        settingHandler = NormalForm.getSettingHandler(slotSpecMap, SimpleSetting.pathHandler);
        slotSpecMap.putBooleanSpec("p");
        slotSpecMap.putBooleanSpec("q");
        slotSpecMap.putBooleanSpec("r");
        slotSpecMap.putBooleanSpec("s");
        slotSpecMap.putBooleanSpec("t");
        slotSpecMap.putBooleanSpec("u");
        slotSpecMap.putBooleanSpec("v");
        slotSpecMap.putValueSpec("a", "2", "3", "4");
        slotSpecMap.putValueSpec("b", "v", "w", "x");
    }

    public static List<ISetting> parseSettingList(String sls) throws Exception {
        StringParser stringParser = new StringParser(sls);
        List<ISetting> settings = PropParser.parseSettingList(stringParser, settingHandler);
        SettingList.sortSettingList(settings);
        return settings;
    }

    @Test
    public void testAdjoin() throws Exception {
        List<ISetting> sl1 = parseSettingList("p q r");
        List<ISetting> sl2 = parseSettingList("r s t");
        List<ISetting> sl3 = parseSettingList("t u v");
        List<List<ISetting>> sll1 = Lists.newArrayList();
        NormalForm.adjoinSettingList(sl1, sll1);
        NormalForm.adjoinSettingList(sl2, sll1);
        NormalForm.adjoinSettingList(sl3, sll1);
        Assert.assertEquals("[[p, q, r], [r, s, t], [t, u, v]]", sll1.toString());
        List<ISetting> sl4 = parseSettingList("p q r s");
        NormalForm.adjoinSettingList(sl4, sll1);
        Assert.assertEquals("[[p, q, r], [r, s, t], [t, u, v]]", sll1.toString());
        List<ISetting> sl5 = parseSettingList("p q");
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
            Pair.of("!(p | !q)", "[[!p], [q]]")
    );

    @Test
    public void testNormalForms() throws Exception {
        for (Pair<String,String> cnfPair : cnfPairs) {
            String propString = cnfPair.getLeft();
            String targetString = cnfPair.getRight();
            StringParser stringParser = new StringParser(propString);
            PropExpression pe = PropParser.parseProp(stringParser);
            List<List<ISetting>> sll = pe.getCnf(true, settingHandler);
            Assert.assertEquals(propString, targetString, sll.toString());
        }
    }
}
