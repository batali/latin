
package latin.setter;

import latin.slots.ISetting;
import latin.slots.NormalForm;
import latin.slots.SettingHandler;
import latin.slots.SimpleSetting;
import latin.slots.SlotSpecMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SlotMapTest {

    public static SlotSpecMap slotSpecMap;
    public static SettingHandler<ISetting> settingHandler;
    public static SlotMap slotMap;

    @BeforeClass
    public static void setupClass() throws Exception {
        slotSpecMap = new SlotSpecMap();
        settingHandler = NormalForm.getSettingHandler(slotSpecMap, SimpleSetting.pathHandler);
        slotSpecMap.putBooleanSpec("p");
        slotSpecMap.putBooleanSpec("q");
        slotSpecMap.putBooleanSpec("r");
        slotSpecMap.putBooleanSpec("s");
        slotSpecMap.putBooleanSpec("t");
        slotSpecMap.putBooleanSpec("u");
        slotSpecMap.putBooleanSpec("v");
        slotSpecMap.putValueSpec("a", "1", "2", "3", "4");
        slotSpecMap.putValueSpec("b", "v", "w", "x");
        slotSpecMap.putValueSpec("c", "0", "1");
        slotMap = new SlotMap();
        slotSpecMap.useSlotSpecs(slotMap);
    }

    @Test
    public void testHaveSlots() throws Exception {
        slotMap.getValueSlot("a");
        slotMap.getBooleanSlot("p");
        slotMap.getBinarySlot("c");
    }

    @Test
    public void testGetSetters() throws Exception {
        Setter pts = slotMap.getSetter("p");
        Setter pfs = slotMap.getSetter("!p");
        Assert.assertTrue(pts.getOpposite().equals(pfs));
        Setter ae1 = slotMap.getSetter("a=1");
        Setter an1 = slotMap.getSetter("a!=1");
        Assert.assertTrue(an1.getOpposite().equals(ae1));
        Setter ce1 = slotMap.getSetter("c=1");
        Setter ce0 = slotMap.getSetter("c=0");
        Setter cn1 = slotMap.getSetter("c!=1");
        Setter cf0 = slotMap.getSetter("!c");
        Assert.assertTrue(ce1.getOpposite().equals(ce0));
        Assert.assertSame(ce0, cn1);
        Assert.assertSame(ce0, cf0);

    }

}