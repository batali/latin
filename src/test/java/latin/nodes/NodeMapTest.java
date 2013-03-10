
package latin.nodes;

import junit.framework.Assert;
import latin.choices.Aspect;
import latin.choices.Case;
import latin.choices.CaseNumber;
import latin.choices.Number;
import latin.choices.Time;
import latin.choices.VerbChoices;
import latin.choices.Voice;
import org.junit.Test;

public class NodeMapTest {

    @Test
    public void testSettings() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        nodeMap.makeBooleanNode("r");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeValueNode("g", "0", "1");
        nodeMap.getBooleanSetting("p", false);
        nodeMap.getValueSetting("f", "a", true);
        BooleanSetting bs1 = nodeMap.getBooleanSetting("g", true);
        BooleanSetting bs2 = nodeMap.getValueSetting("g", "1", true);
        Assert.assertEquals(bs1, bs2);
        nodeMap.printNodes();
    }

    @Test
    public void testEnumNodes() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeValueNode("Case", Case.class);
        nodeMap.makeValueNode("Number", Number.class);
        Node<?> cnNode = nodeMap.makeValueNode("CaseNumber", CaseNumber.class);
        for (CaseNumber cn : CaseNumber.values()) {
            Case ck = cn.getCase();
            Number nk = cn.getNumber();
            nodeMap.makeDrules(cn.toString(), String.format("(Case=%s & Number=%s) == CaseNumber=%s",
                    ck.toString(), nk.toString(), cn.toString()));
        }
        nodeMap.support("Case=Abl");
        nodeMap.support("Number=Si");
//        System.out.prntln(cnNode.getSupportedSetting().toString());
        nodeMap.support("Number=Pl");
//        System.out.println(cnNode.getSupportedSetting().toString());
        nodeMap.support("Case=Dat");
//        System.out.println(cnNode.getSupportedSetting().toString());
        nodeMap.support("CaseNumber=AccSi");
        nodeMap.printNodes();
    }

    @Test
    public void testVerbChoices() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeValueNode("Time", Time.class);
        nodeMap.makeValueNode("Aspect", Aspect.class);
        nodeMap.makeValueNode("Fvoice", Voice.class);
        nodeMap.makeValueNode("Evoice", Voice.class);
        nodeMap.makeValueNode("Rule", "Vform", "Part");
        Node<?> vcn = nodeMap.makeValueNode("VerbChoices", VerbChoices.class);
        for (VerbChoices vc :  VerbChoices.values()) {
            Time tk = vc.time;
            Aspect ak = vc.aspect;
            Voice vk = vc.voice;
            nodeMap.makeDrules(vc.toString(), String.format("(Time=%s & Aspect=%s & Fvoice=%s) == VerbChoices=%s",
                    tk.toString(), ak.toString(), vk.toString(), vc.toString()));
        }
        nodeMap.makeBooleanNode("dep");
        nodeMap.makeBooleanNode("trans");
        nodeMap.makeBooleanNode("astem");
        nodeMap.makeBooleanNode("pstem");
        nodeMap.makeBooleanNode("jstem");
        nodeMap.makeDrules("v1", "Evoice=Pas -> Fvoice=Pas");
        nodeMap.makeDrules("v2", "Fvoice=Act -> Evoice=Act");
        nodeMap.makeDrules("dp1", "dep -> (Fvoice=Pas & Evoice=Act)");
        nodeMap.makeDrules("dp2", "!dep -> (Fvoice == Evoice)");
        nodeMap.makeDrules("pr1", "(Aspect=Cm & Fvoice=Pas) == Rule=Part");
        nodeMap.makeDrules("tr1", "Evoice=Pas -> trans");
        nodeMap.makeDrules("as1", "Aspect=In -> astem");
        nodeMap.makeDrules("ps1", "(Aspect=Cm & Rule=Vform) -> pstem");
        nodeMap.makeDrules("js1", "Rule=Part -> jstem");
        System.out.println("empty");
        nodeMap.printNodes();
        nodeMap.support("Time!=Pre");
        System.out.println("Pre");
        nodeMap.printNodes();
        nodeMap.support("!pstem");
        System.out.println("Cm");
        nodeMap.support("Aspect=Cm");
        nodeMap.printNodes();
        nodeMap.support("!trans");
        nodeMap.support("dep");
        System.out.println("dep");
        nodeMap.printNodes();
        System.out.println("aa");
    }

    public void checkStatus(BooleanSetting bs, int ts) {
        Assert.assertEquals(bs.toString(), ts, bs.getStatus());
    }

    @Test
    public void testRules() throws Exception {
        System.out.println("testRules");
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        nodeMap.makeDrules("r1", "p->q");
        nodeMap.makeDrules("r2", "q->r");
        nodeMap.support("p");
        checkStatus(ps, 1);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        nodeMap.retract("q");
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
        nodeMap.support("!r");
        Assert.assertTrue(nodeMap.checkCounts());
        checkStatus(ps, -1);
        checkStatus(qs, -1);
        checkStatus(rs, -1);
        nodeMap.retract("!r");
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
    }

    @Test
    public void testValues() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeValueNode("f", "a", "b", "c");
        BooleanSetting fa = nodeMap.parseSetting("f=a");
        BooleanSetting fb = nodeMap.parseSetting("f=b");
        BooleanSetting fc = nodeMap.parseSetting("f=c");
        Assert.assertEquals(0, fa.getStatus());
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        nodeMap.support("f=a");
        checkStatus(fa, 1);
        checkStatus(fb, -1);
        checkStatus(fc, -1);
        nodeMap.retract("f=a");
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.support("f!=b");
        checkStatus(fa, 0);
        checkStatus(fb, -1);
        checkStatus(fc, 0);
        nodeMap.retract("f!=b");
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
    }

    @Test
    public void testSmallContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        nodeMap.makeDrules("r1", "p -> q");
        nodeMap.makeDrules("r2", "p -> !q");
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        Assert.assertFalse(nodeMap.support("p"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(nodeMap.retract("p"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.support("!p");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testBiggerContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        BooleanSetting ss = nodeMap.makeBooleanNode("s");
        BooleanSetting as = nodeMap.makeBooleanNode("a");
        BooleanSetting bs = nodeMap.makeBooleanNode("b");
        BooleanSetting ws = nodeMap.makeBooleanNode("w");
        BooleanSetting xs = nodeMap.makeBooleanNode("x");
        nodeMap.makeDrules("r1", "p | q | r | s");
        nodeMap.makeDrules("r2", "a -> (!p & !q)");
        nodeMap.makeDrules("r3", "b -> (!r & !s)");
        nodeMap.makeDrules("r4", "w -> a");
        nodeMap.makeDrules("r5", "x -> b");
        nodeMap.support("w");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertFalse(nodeMap.support("w", "x"));
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retract("w");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retract("x");
        Assert.assertTrue(nodeMap.support("w"));
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.printNodes();
    }

    @Test
    public void testValueContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> f=b");
        nodeMap.makeDrules("r3", "r -> (p & q)");
        Assert.assertFalse(nodeMap.support("r"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(nodeMap.retract("r"));
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testWeirdValueContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        nodeMap.makeBooleanNode("r");
        nodeMap.makeBooleanNode("s");
        nodeMap.makeBooleanNode("t");
        nodeMap.makeBooleanNode("u");
        nodeMap.makeBooleanNode("v");
        nodeMap.makeValueNode("a", "1", "2", "3", "4");
        nodeMap.makeValueNode("b", "v", "w", "x");
        nodeMap.makeValueNode("c", "0", "1");
        nodeMap.makeValueNode("d", "i", "o", "u");
        nodeMap.makeDrules("r1", "d=i -> b=v");
        nodeMap.makeDrules("r2", "a!=1 == !c");
        nodeMap.makeDrules("r3", "a=3 ^ t");
        nodeMap.makeDrules("r4", "d=o -> (r & s)");
        nodeMap.makeDrules("r5", "a!=1 -> p");
        nodeMap.makeDrules("r6", "a!=2 -> !p");

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
        Assert.assertFalse(nodeMap.support("a=3"));
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(nodeMap.retract("a=3"));
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testRetractToSet() throws Exception {
        System.out.println("retract to set");
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> r");
        nodeMap.makeDrules("r3", "f=c -> q");
        nodeMap.support("p");
        nodeMap.printNodes();
        nodeMap.support("f=b");
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.printNodes();
        nodeMap.support("f=c");
        nodeMap.printNodes();
        nodeMap.support("!r");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.support("f=a", "q", "r"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(nodeMap.support("!q", "f!=c"));

    }

    @Test
    public void testTryContradiction() throws Exception {
        System.out.println("try contra");
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        BooleanSetting ss = nodeMap.makeBooleanNode("s");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> f=b");
        nodeMap.makeDrules("r3", "(r & s) -> (p & q)");
        nodeMap.support("r");
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.printNodes();
        System.out.println("hh");
        nodeMap.support("s", "p");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testRetractSettings() throws Exception {
        System.out.println("try retract settings");
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        BooleanSetting ss = nodeMap.makeBooleanNode("s");
        BooleanSetting ts = nodeMap.makeBooleanNode("t");
        BooleanSetting us = nodeMap.makeBooleanNode("u");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeValueNode("g", "1", "2", "3");
        nodeMap.makeValueNode("h", "e", "o", "u");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> g=1");
        nodeMap.makeDrules("r3", "r -> h=u");
        nodeMap.makeDrules("r4", "s -> (p | q)");
        nodeMap.makeDrules("r5", "t ^ s");
        nodeMap.makeDrules("r6", "u == f!=c");
        nodeMap.support("f!=b");
        nodeMap.support("h=o");
        nodeMap.support("s");
        nodeMap.printNodes();
        nodeMap.retract("h=o",   "!t");
        nodeMap.printNodes();

    }

    @Test
    public void testSupSettings() throws Exception {
        System.out.println("try sup settings");
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p");
        BooleanSetting qs = nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r");
        BooleanSetting ss = nodeMap.makeBooleanNode("s");
        BooleanSetting ts = nodeMap.makeBooleanNode("t");
        BooleanSetting us = nodeMap.makeBooleanNode("u");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeValueNode("g", "1", "2", "3");
        nodeMap.makeValueNode("h", "e", "o", "u");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> g=1");
        nodeMap.makeDrules("r3", "r -> h=u");
        nodeMap.makeDrules("r4", "s -> (p | q)");
        nodeMap.makeDrules("r5", "t ^ s");
        nodeMap.makeDrules("r6", "u == f!=c");
        nodeMap.support("f=a", "h=u", "s");
        nodeMap.printNodes();
        nodeMap.support("g=1", "h!=u", "t");
        nodeMap.printNodes();
        nodeMap.support("r", "s", "!u");
        nodeMap.printNodes();
    }


}