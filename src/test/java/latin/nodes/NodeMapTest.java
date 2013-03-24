
package latin.nodes;

import junit.framework.Assert;
import org.junit.Test;

public class NodeMapTest {

    @Test
    public void testSettings() throws Exception {
        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("p");
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeValueNode("g", "0", "1");

        BooleanSetting pt = nodeMap.getBooleanSetting("p", true);
        BooleanSetting pf = nodeMap.getBooleanSetting("p", false);
        Assert.assertSame(pt.getOpposite(), pf);
        Assert.assertSame(pt, nodeMap.parseSetting("p"));
        Assert.assertSame(pf, nodeMap.parseSetting("!p"));

        BooleanSetting fat = nodeMap.getValueSetting("f", "a", true);
        Assert.assertSame(fat, nodeMap.parseSetting("f=a"));
        Assert.assertSame(fat.getOpposite(), nodeMap.getValueSetting("f", "a", false));
        Assert.assertSame(fat.getOpposite(), nodeMap.parseSetting("f!=a"));
        Assert.assertSame(fat.getOpposite(), nodeMap.parseSetting("!f=a"));
        Assert.assertEquals("f=a", fat.toString());
        Assert.assertEquals("f!=a", fat.getOpposite().toString());

        BooleanSetting g1 = nodeMap.getBooleanSetting("g", true);
        Assert.assertEquals("g=1", g1.toString());
        Assert.assertSame(g1, nodeMap.getValueSetting("g", "1", true));
        Assert.assertSame(g1, nodeMap.getValueSetting("g", "0", false));
        Assert.assertSame(g1, nodeMap.parseSetting("g"));
        Assert.assertSame(g1, nodeMap.parseSetting("g=1"));
        Assert.assertSame(g1, nodeMap.parseSetting("g!=0"));
        Assert.assertSame(g1, nodeMap.parseSetting("!g=0"));
        BooleanSetting g0 = g1.getOpposite();
        Assert.assertEquals("g=0", g0.toString());
        Assert.assertSame(g0, nodeMap.getBooleanSetting("g", false));
        Assert.assertSame(g0, nodeMap.getValueSetting("g", "0", true));
        Assert.assertSame(g0, nodeMap.getValueSetting("g", "1", false));
        Assert.assertSame(g0, nodeMap.parseSetting("!g"));
        Assert.assertSame(g0, nodeMap.parseSetting("g=0"));
        Assert.assertSame(g0, nodeMap.parseSetting("g!=1"));
        Assert.assertSame(g0, nodeMap.parseSetting("!g=1"));
    }

    public void checkStatus(BooleanSetting bs, int ts) {
        Assert.assertEquals(bs.toString(), ts, bs.getStatus());
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