
package latin.setter;

import latin.slots.SlotSpecMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class PropagateTest {

    public SlotMap slotMap;

    @Before
    public void makeSlotMap() throws Exception {
        SlotSpecMap slotSpecMap = new SlotSpecMap();
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
        slotSpecMap.putValueSpec("d", "i", "o", "u");
        slotMap = new SlotMap();
        slotSpecMap.useSlotSpecs(slotMap);
    }

    @Test
    public void testHaveRules() throws Exception {
        slotMap.addRules("p->q");
        slotMap.addRules("q->r");
        DisjunctionRule dr1 = slotMap.getRule("[p->q].0");
        Assert.assertNotNull(dr1);
        System.out.println(dr1.toString());
        DisjunctionRule dr2 = slotMap.getRule("[q->r].0");
        Assert.assertNotNull(dr2);
        System.out.println(dr2.toString());
    }

    @Test
    public void testPropagate() throws Exception {
        slotMap.addRules("p->q");
        slotMap.addRules("q->r");
        Setter pt = slotMap.getSetter("p");
        Setter qt = slotMap.getSetter("q");
        Setter rt = slotMap.getSetter("r");
        Setter rf = slotMap.getSetter("!r");
        slotMap.addBaseSupport(pt);
        Assert.assertEquals(1, rt.getStatus());
        Assert.assertEquals(1, qt.getStatus());
        slotMap.removeBaseSupport(pt);
        Assert.assertEquals(0, qt.getStatus());
        Assert.assertEquals(0, rt.getStatus());
        slotMap.addBaseSupport(rf);
        Assert.assertEquals(-1, qt.getStatus());
        Assert.assertEquals(-1, pt.getStatus());
        Assert.assertTrue(slotMap.checkCounts(false));
        slotMap.removeBaseSupport(rf);
        Assert.assertEquals(0, pt.getStatus());
        Assert.assertEquals(0, qt.getStatus());
        Assert.assertEquals(0, rt.getStatus());
        Assert.assertTrue(slotMap.checkCounts(false));
    }

    @Test
    public void testValues() throws Exception {
        Setter bv = slotMap.getSetter("b=v");
        Setter bw = slotMap.getSetter("b=w");
        Setter bx = slotMap.getSetter("b=x");
        slotMap.addBaseSupport(bv);
        Assert.assertEquals(-1, bw.getStatus());
        Assert.assertEquals(-1, bx.getStatus());
        slotMap.removeBaseSupport(bv);
        Assert.assertEquals(0, bw.getStatus());
        Assert.assertEquals(0, bx.getStatus());
        slotMap.addBaseSupport(bw.getOpposite());
        slotMap.addBaseSupport(bx.getOpposite());
        Assert.assertEquals(1, bv.getStatus());
        slotMap.removeBaseSupport(bw.getOpposite());
        Assert.assertEquals(0, bv.getStatus());
        slotMap.removeBaseSupport(bx.getOpposite());
        Assert.assertEquals(0, bv.getStatus());
        Assert.assertTrue(slotMap.checkCounts(false));
    }

    @Test
    public void testSimpleContradiction() throws Exception {
        slotMap.addRules("t | u");
        slotMap.addRules("p -> !t");
        slotMap.addRules("p -> !u");
        Setter ts = slotMap.getSetter("t");
        Setter us = slotMap.getSetter("u");
        Setter ps = slotMap.getSetter("p");
        Assert.assertEquals(0, ts.getStatus());
        Assert.assertEquals(0, us.getStatus());
        Assert.assertEquals(0, ps.getStatus());
        try {
            slotMap.addBaseSupport(ps);
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println("Simple Contra " + ce.getMessage());
        }
        slotMap.getPropagator().retractFromContradiction();
        Assert.assertTrue(slotMap.checkCounts(false));
    }

    @Test
    public void testSetValueContradiction() throws Exception {
        slotMap.addRules("p -> d=i");
        slotMap.addRules("p -> d=o");
        Setter ps = slotMap.getSetter("p");
        Assert.assertEquals(0, ps.getStatus());
        try {
            slotMap.addBaseSupport(ps);
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println("SetValueContra " + ce.getMessage());
        }
        slotMap.getPropagator().retractFromContradiction();
        Assert.assertTrue(slotMap.checkCounts(false));
    }

    @Test
    public void testContradiction() throws Exception {
        slotMap.addRules("t | u | v");
        slotMap.addRules("u | !v");
        slotMap.addRules("v -> d=i");
        slotMap.addRules("!v -> d!=o");
        Setter tt = slotMap.getSetter("t");
        Setter ut = slotMap.getSetter("u");
        Setter vt = slotMap.getSetter("v");
        slotMap.addBaseSupport(tt.getOpposite());
        try {
            slotMap.addBaseSupport(ut.getOpposite());
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println("contra " + ce.getMessage());
        }
        slotMap.checkCounts(true);
        Propagator propagator = slotMap.getPropagator();
        Assert.assertTrue(propagator.wasContradiction());
        Setter fd = propagator.firstDeducer();
        Assert.assertNotNull(fd);
        System.out.println("fd " + fd.toString());
        Set<Setter> baseSetters = propagator.contradictionSupportCollector.getBaseSetters();
        System.out.println("base setters " + baseSetters.toString());
        propagator.retractFromContradiction();
        Assert.assertTrue(slotMap.checkCounts(false));
        slotMap.removeBaseSupport(ut.getOpposite());
        Assert.assertTrue(slotMap.checkCounts(false));
        slotMap.showSlots();
        slotMap.addBaseSupport(ut);
        Assert.assertTrue(slotMap.checkCounts(false));
        System.out.println("ut");
        slotMap.showSlots();
        int[] ca = new int[2];
        slotMap.getTotalSupportedCount(ca);
        System.out.println("mtk " + ca[0] + " ts " + ca[1]);
        slotMap.removeBaseSupport(ut);
        slotMap.removeBaseSupport(tt.getOpposite());
        Assert.assertTrue(slotMap.checkCounts(false));
        slotMap.getTotalSupportedCount(ca);
        System.out.println("atk " + ca[0] + " ts " + ca[1]);
    }

    @Test
    public void testValueContradiction() throws Exception {
        slotMap.addRules("d=i -> b=v");
        slotMap.addRules("a!=1 == !c");
        slotMap.addRules("a=3 ^ t");
        slotMap.addRules("d=o -> (r & s)");
        slotMap.addRules("a!=1 -> p");
        slotMap.addRules("a!=2 -> !p");

        /*
        slotMap.addSupport("!r");
        slotMap.addSupport("d=i");
        slotMap.showSlots();
        int[] ca = new int[2];
        slotMap.getTotalSupportedCount(ca);
        System.out.println("vc mtk " + ca[0] + " ts " + ca[1]);
        slotMap.removeSupport("!r");
        slotMap.showSlots();
        slotMap.removeSupport("d=i");
        slotMap.showSlots();
        slotMap.getTotalSupportedCount(ca);
        System.out.println("va mtk " + ca[0] + " ts " + ca[1]);
        */
        try {
            slotMap.addSupport("a=3");
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println("testValueContradiction " + ce.getMessage());
        }
        Propagator propagator = slotMap.getPropagator();
        Set<Setter> baseSetters = propagator.contradictionSupportCollector.getBaseSetters();
        System.out.println("bs " + baseSetters);
        Set<Supporter> ss = propagator.contradictionSupportCollector.getSeen();
        System.out.println("ss " + ss.toString());
        propagator.retractFromContradiction();
        Assert.assertTrue(slotMap.checkCounts(false));
    }



}