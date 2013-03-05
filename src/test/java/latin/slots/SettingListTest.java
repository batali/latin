
package latin.slots;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SettingListTest {

    public SlotSpecMap slotSpecMap;
    public SettingHandler<ISetting> settingHandler;

    @Before
    public void setUp() {
        slotSpecMap = new SlotSpecMap();
        settingHandler = slotSpecMap.getSettingHandler(ISetting.class, SimpleSetting.pathHandler);
        slotSpecMap.putValueSpec("h", "ho", "ha");
        slotSpecMap.putValueSpec("a", "1", "2", "3", "4");
        slotSpecMap.putValueSpec("b", "t", "u", "v");
        slotSpecMap.putBooleanSpec("p");
        slotSpecMap.putBooleanSpec("q");
        slotSpecMap.putBooleanSpec("r");
        slotSpecMap.putBooleanSpec("s");
        slotSpecMap.putBooleanSpec("t");
        slotSpecMap.putBooleanSpec("z");
    }

    public List<ISetting> parseSettingList(String sls) throws Exception {
        StringParser stringParser = new StringParser(sls);
        List<ISetting> settings = PropParser.parseSettingList(stringParser, settingHandler);
        return SettingList.sortSettingList(settings);
    }

    public ISetting parseSetting(String ss) throws Exception {
        StringParser stringParser = new StringParser(ss);
        return PropParser.parseSetting(stringParser, settingHandler);
    }

    @Test
    public void testParseSettingList() throws Exception {
        List<ISetting> settings = parseSettingList("p q !p a!=4 a=4 a=3 !b=v !b!=v");
        Assert.assertEquals("[a=3, a=4, a!=4, b=v, b!=v, p, !p, q]", settings.toString());
    }

    @Test
    public void testParseBinary() throws Exception {
        StringParser stringParser = new StringParser("h=ha h=ho h!=ha h!=ho h !h");
        List<ISetting> settings = PropParser.parseSettingList(stringParser, settingHandler);
        Assert.assertEquals("[h=ha, h=ho, h=ho, h=ha, h=ha, h=ho]", settings.toString());
    }

    @Test
    public void testMergeSettings() throws Exception {
        List<ISetting> sl1 = parseSettingList("p q");
        List<ISetting> sl2 = parseSettingList("q r");
        List<ISetting> sl3 = parseSettingList("!q r");
        List<ISetting> sl4 = parseSettingList("!q s t");
        List<ISetting> sl12 = SettingList.mergeSettingLists(sl1, sl2);
        Assert.assertEquals("[p, q, r]", sl12.toString());
        List<ISetting> sl13 = SettingList.mergeSettingLists(sl1, sl3);
        Assert.assertNull(sl13);
        List<ISetting> sl34 = SettingList.mergeSettingLists(sl3, sl4);
        Assert.assertEquals("[!q, r, s, t]", sl34.toString());
    }

    @Test
    public void testFindProp() throws Exception {
        List<ISetting> sl = parseSettingList("p !q r s !t");
        int sls = sl.size();
        ISetting pp = parseSetting("p");
        ISetting pn = parseSetting("!p");
        Assert.assertEquals(0, SettingList.findProp(pp, sl, 0, sls));
        Assert.assertEquals(0, SettingList.findProp(pn, sl, 0, sls));
        ISetting rp = parseSetting("r");
        Assert.assertEquals(2, SettingList.findProp(rp, sl, 0, sls));
        Assert.assertEquals(2, SettingList.findProp(rp, sl, 2, sls));
        Assert.assertEquals(-1, SettingList.findProp(rp, sl, 2, 2));
    }

    @Test
    public void testSubset() throws Exception {
        List<ISetting> sl1 = parseSettingList("p r t");
        Assert.assertTrue(SettingList.isSubset(parseSettingList("p r t"), sl1));
        Assert.assertTrue(SettingList.isSubset(parseSettingList("p r"), sl1));
        Assert.assertTrue(SettingList.isSubset(parseSettingList("p t"), sl1));
        Assert.assertTrue(SettingList.isSubset(parseSettingList("r t"), sl1));
        Assert.assertTrue(SettingList.isSubset(parseSettingList("p"), sl1));
        Assert.assertFalse(SettingList.isSubset(parseSettingList("p q"), sl1));
        Assert.assertFalse(SettingList.isSubset(parseSettingList("!p r t"), sl1));
        Assert.assertFalse(SettingList.isSubset(parseSettingList("t z"), sl1));
    }


}