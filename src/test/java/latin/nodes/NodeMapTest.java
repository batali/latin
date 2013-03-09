
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
        nodeMap.supportSetting("Case=Abl");
        nodeMap.supportSetting("Number=Si");
        System.out.println(cnNode.getSupportedSetting().toString());
        nodeMap.retractSetting("Number=Si");
        nodeMap.supportSetting("Number=Pl");
        System.out.println(cnNode.getSupportedSetting().toString());
        nodeMap.retractSetting("Case=Abl");
        nodeMap.supportSetting("Case=Dat");
        System.out.println(cnNode.getSupportedSetting().toString());
        nodeMap.retractSetting("Case=Dat");
        nodeMap.retractSetting("Number=Pl");
        nodeMap.supportSetting("CaseNumber=AccSi");
        nodeMap.printNodes();
    }

    @Test
    public void testVerbChoices() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeValueNode("Time", Time.class);
        nodeMap.makeValueNode("Aspect", Aspect.class);
        nodeMap.makeValueNode("Voice", Voice.class);
        Node<?> vcn = nodeMap.makeValueNode("VerbChoices", VerbChoices.class);
        for (VerbChoices vc :  VerbChoices.values()) {
            Time tk = vc.time;
            Aspect ak = vc.aspect;
            Voice vk = vc.voice;
            nodeMap.makeDrules(vc.toString(), String.format("(Time=%s & Aspect=%s & Voice=%s) == VerbChoices=%s",
                    tk.toString(), ak.toString(), vk.toString(), vc.toString()));
        }
        nodeMap.supportSetting("Time=Pre");
        nodeMap.supportSetting("Aspect=In");
        nodeMap.supportSetting("Voice=Act");
        System.out.println(vcn.getSupportedSetting().toString());
        nodeMap.retractSetting("Aspect=In");
        nodeMap.supportSetting("Aspect=Cm");
        System.out.println(vcn.getSupportedSetting().toString());
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
        nodeMap.supportSetting("p");
        checkStatus(ps, 1);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        nodeMap.retractSetting("p");
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
        nodeMap.supportSetting("!r");
        Assert.assertTrue(nodeMap.checkCounts());
        checkStatus(ps, -1);
        checkStatus(qs, -1);
        checkStatus(rs, -1);
        nodeMap.retractSetting("!r");
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
        nodeMap.supportSetting("f=a");
        checkStatus(fa, 1);
        checkStatus(fb, -1);
        checkStatus(fc, -1);
        nodeMap.retractSetting(fa);
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.supportSetting("f!=a");
        nodeMap.supportSetting("f!=b");
        checkStatus(fa, -1);
        checkStatus(fb, -1);
        checkStatus(fc, 1);
        nodeMap.retractSetting("f!=a");
        checkStatus(fa, 0);
        checkStatus(fb, -1);
        checkStatus(fc, 0);
        nodeMap.retractSetting("f!=b");
        checkStatus(fb, 0);
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
        try {
            nodeMap.supportSetting("p");
        }
        catch(ContradictionException ce) {
            System.out.println("ce");
        }
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retractSetting("p");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.supportSetting("!p");
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
        nodeMap.supportSetting("w");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        try {
            nodeMap.supportSetting("x");
        }
        catch(ContradictionException ce) {
        }
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retractSetting("w");
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retractSetting("x");
        nodeMap.supportSetting("w");
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
        try {
            nodeMap.supportSetting("r");
        }
        catch(ContradictionException ce) {
            System.out.println("vce " + ce.getMessage());
        }
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retractSetting("r");
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
        try {
            nodeMap.supportSetting("a=3");
        }
        catch(ContradictionException ce) {
            System.out.println("testValueContradiction " + ce.getMessage());
        }
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.retractSetting("a=3");
        Assert.assertTrue(nodeMap.checkCounts());
    }
}