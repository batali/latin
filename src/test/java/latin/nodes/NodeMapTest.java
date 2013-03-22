
package latin.nodes;

import com.google.common.base.Objects;
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
        TryPropagator tp = new TryPropagator();
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        BooleanSetting rs = nodeMap.makeBooleanNode("r").trueSetting;
        nodeMap.makeDrules("r1", "p->q");
        nodeMap.makeDrules("r2", "q->r");
        nodeMap.trysup(tp, "p");
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
        BooleanSetting cfa = nodeMap.parseSetting("f=a");
        Assert.assertTrue(Objects.equal(fa, cfa));
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        Assert.assertTrue(nodeMap.support("f=a"));
        System.out.println("f=a " + fa.haveSupporter());
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
    public void testTrySmallContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        nodeMap.makeDrules("r1", "p -> q");
        nodeMap.makeDrules("r2", "p -> !q");
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        System.out.println("trysup p");
        Assert.assertFalse(nodeMap.support("p"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        System.out.println("trysup !p");
        Assert.assertTrue(nodeMap.support("!p"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testTryBiggerContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        nodeMap.makeBooleanNode("r");
        nodeMap.makeBooleanNode("s");
        nodeMap.makeBooleanNode("a");
        nodeMap.makeBooleanNode("b");
        nodeMap.makeBooleanNode("w");
        nodeMap.makeBooleanNode("x");
        nodeMap.makeDrules("r1", "p | q | r | s");
        nodeMap.makeDrules("r2", "a -> (!p & !q)");
        nodeMap.makeDrules("r3", "b -> (!r & !s)");
        nodeMap.makeDrules("r4", "w -> a");
        nodeMap.makeDrules("r5", "x -> b");
        TryPropagator tp = new TryPropagator();
        System.out.println("trysup w");
        Assert.assertTrue(nodeMap.trysup(tp, "w"));
        tp.clear();
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        try {
            nodeMap.trysup(tp, "x");
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println(ce.getMessage());
            tp.retractContradiction();
        }
        Assert.assertTrue(nodeMap.checkCounts());
        nodeMap.printNodes();
    }

    @Test
    public void testTryValueContradiction() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        nodeMap.makeBooleanNode("r");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> f=b");
        nodeMap.makeDrules("r3", "r -> (p & q)");
        TryPropagator tp = new TryPropagator();
        try {
            nodeMap.trysup(tp, "r");
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println(ce.getMessage());
            tp.retractContradiction();
        }
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
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
        TryPropagator tp = new TryPropagator();
        String[] ssa = { "d=i", "b=v", "a=3" };
        Assert.assertTrue(nodeMap.trysup(tp, "d=i"));
        tp.clear();
        Assert.assertTrue(nodeMap.trysup(tp, "b=v"));
        tp.clear();
        try {
            nodeMap.trysup(tp, "a=3");
            Assert.fail();
        }
        catch(ContradictionException ce) {
            System.out.println(" contra " + ce.getMessage());
            tp.retractContradiction();
        }
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(nodeMap.trysup(tp, "a!=3"));
        tp.clear();
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testRetractToSet() throws Exception {
        System.out.println("retract to set");
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        nodeMap.makeBooleanNode("r");
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
    public void testTryRet() throws Exception {
        System.out.println("tryret");
        NodeMap nodeMap = new NodeMap();
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        BooleanSetting rs = nodeMap.makeBooleanNode("r").trueSetting;
        BooleanSetting ws = nodeMap.makeBooleanNode("w").trueSetting;
        BooleanSetting xs = nodeMap.makeBooleanNode("x").trueSetting;
        nodeMap.makeDrules("(w & x) -> p");
        nodeMap.makeDrules("(w & q) -> r");
        TryPropagator tp = new TryPropagator();
        Assert.assertTrue(nodeMap.trysup(tp, "w"));
        tp.clear();
        Assert.assertTrue(nodeMap.trysup(tp, "x"));
        tp.clear();
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(nodeMap.trysup(tp, "q"));
        tp.clear();
        nodeMap.printNodes();
        checkStatus(ps, 1);
        checkStatus(rs, 1);
        Retractor r = nodeMap.makeRetractor("w");
        Assert.assertTrue(nodeMap.tryret(tp, r, "p"));
        nodeMap.printNodes();
        Assert.assertTrue(nodeMap.checkCounts());
        checkStatus(ps, 0);
        checkStatus(rs, 1);
        checkStatus(ws, 1);
        Assert.assertTrue(nodeMap.tryret(tp, r, "r"));
        checkStatus(ps, 0);
        checkStatus(rs, 0);
        checkStatus(ws, 1);
        Assert.assertTrue(nodeMap.checkCounts());
    }


    @Test
    public void testRetractSettings() throws Exception {
        System.out.println("try retract settings");
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        nodeMap.makeBooleanNode("r");
        nodeMap.makeBooleanNode("s");
        nodeMap.makeBooleanNode("t");
        nodeMap.makeBooleanNode("u");
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



}