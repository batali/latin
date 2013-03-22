
package latin.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Set;

public class PropagateTest {

    NodeMap nodeMap;
    LinkedList<Supported> dqueue;
    LinkedList<Supported> rqueue;
    LinkedList<Deducer> rdqueue;
    DeduceQueue deduceQueue;
    RetractQueue retractQueue;

    @Before
    public void setUp() {
        nodeMap = new NodeMap();
        dqueue = Lists.newLinkedList();
        rqueue = Lists.newLinkedList();
        rdqueue = Lists.newLinkedList();
        deduceQueue = new AbstractDeduceQueue(dqueue);
        retractQueue = new AbstractRetractQueue(rqueue, rdqueue);
    }

    public void checkStatus(BooleanSetting bs, int ts) {
        Assert.assertEquals(bs.toString(), ts, bs.getStatus());
    }

    public boolean psup(BooleanSetting bs, TopSupporter ts) throws ContradictionException {
        return deduceQueue.setSupport(bs, ts) && deduceQueue.propagateLoop();
    }

    public boolean rsup(TopSupporter ts) {
        return ts.retract(retractQueue) && retractQueue.retractLoop();
    }

    @Test
    public void testPropagateBoolean() throws Exception {
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        BooleanSetting rs = nodeMap.makeBooleanNode("r").trueSetting;
        nodeMap.makeDrules("r1", "p->q");
        nodeMap.makeDrules("r2", "q->r");
        TopSupporter tops = new TopSupporter();
        Assert.assertTrue(psup(ps, tops));
        checkStatus(ps, 1);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(tops) && !retractQueue.haveRededucer());
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter topt = new TopSupporter();
        Assert.assertTrue(psup(rs.getOpposite(), topt));
        checkStatus(ps, -1);
        checkStatus(qs, -1);
        checkStatus(rs, -1);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(topt) && !retractQueue.haveRededucer());
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter topq = new TopSupporter();
        Assert.assertTrue(psup(qs, topq));
        checkStatus(ps, 0);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testRededuceBoolean() throws Exception {
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        BooleanSetting rs = nodeMap.makeBooleanNode("r").trueSetting;
        BooleanSetting ss = nodeMap.makeBooleanNode("s").trueSetting;
        nodeMap.makeDrules("r1", "p->q");
        nodeMap.makeDrules("r2", "q->r");
        nodeMap.makeDrules("r3", "s->r");
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup(ps, top1));
        checkStatus(ps, 1);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        checkStatus(ss, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup(ss, top2));
        checkStatus(ps, 1);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        checkStatus(ss, 1);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(top1));
        Assert.assertTrue(retractQueue.haveRededucer());
        retractQueue.rededuceLoop(deduceQueue);
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 1);
        checkStatus(ss, 1);
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testPropagateValues() throws Exception {
        nodeMap.makeValueNode("f", "a", "b", "c");
        BooleanSetting fa = nodeMap.parseSetting("f=a");
        BooleanSetting fb = nodeMap.parseSetting("f=b");
        BooleanSetting fc = nodeMap.parseSetting("f=c");
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup(fa, top1));
        checkStatus(fa, 1);
        checkStatus(fb, -1);
        checkStatus(fc, -1);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(top1) && !retractQueue.haveRededucer());
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup(fb.getOpposite(), top2));
        checkStatus(fa, 0);
        checkStatus(fb, -1);
        checkStatus(fc, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter top3 = new TopSupporter();
        Assert.assertTrue(psup(fc.getOpposite(), top3));
        checkStatus(fa, 1);
        checkStatus(fb, -1);
        checkStatus(fc, -1);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(top2) && !retractQueue.haveRededucer());
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, -1);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter top4 = new TopSupporter();
        Assert.assertTrue(psup(fa.getOpposite(), top4));
        checkStatus(fa, -1);
        checkStatus(fb, 1);
        checkStatus(fc, -1);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(top3) && !retractQueue.haveRededucer());
        checkStatus(fa, -1);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testPropagateBinary() throws Exception {
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        nodeMap.makeValueNode("g", "i", "o");
        BooleanSetting gi = nodeMap.parseSetting("g=i");
        BooleanSetting go = nodeMap.parseSetting("g=o");
        nodeMap.makeDrules("p -> g=i");
        nodeMap.makeDrules("g=o -> q");
        checkStatus(gi, 0);
        checkStatus(go, 0);
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup(gi, top1));
        checkStatus(gi, 1);
        checkStatus(go, -1);
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(top1) && !retractQueue.haveRededucer());
        checkStatus(gi, 0);
        checkStatus(go, 0);
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup(ps, top2));
        checkStatus(gi, 1);
        checkStatus(go, -1);
        checkStatus(ps, 1);
        checkStatus(qs, 0);
        Assert.assertTrue(nodeMap.checkCounts());
        Assert.assertTrue(rsup(top2) && !retractQueue.haveRededucer());
        TopSupporter top3 = new TopSupporter();
        Assert.assertTrue(psup(qs.getOpposite(), top3));
        checkStatus(gi, 1);
        checkStatus(go, -1);
        checkStatus(ps, 0);
        checkStatus(qs, -1);
        Assert.assertTrue(nodeMap.checkCounts());
    }

    @Test
    public void testGetTopSupporters() throws Exception {
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        BooleanSetting rs = nodeMap.makeBooleanNode("r").trueSetting;
        BooleanSetting ss = nodeMap.makeBooleanNode("s").trueSetting;
        BooleanSetting ts = nodeMap.makeBooleanNode("t").trueSetting;
        nodeMap.makeDrules("(p & q) -> r");
        nodeMap.makeDrules("(r & s) -> t");
        Set<TopSupporter> tset1 = Sets.newHashSet();
        Set<TopSupporter> tseta = Sets.newHashSet();
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup(ps, top1));
        tset1.add(top1);
        tseta.add(top1);
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup(qs, top2));
        tset1.add(top2);
        tseta.add(top2);
        TopSupporter top3 = new TopSupporter();
        Assert.assertTrue(psup(ss, top3));
        tset1.add(top3);
        checkStatus(ts, 1);
        SupportCollector supportCollector = new SupportCollector();
        supportCollector.recordSupporter(ts);
        Set<TopSupporter> tset2 = Sets.newHashSet(supportCollector.topSupporters());
        Assert.assertEquals(tset1, tset2);
        supportCollector.clear();
        supportCollector.recordSupporter(rs);
        Set<TopSupporter> tset3 = Sets.newHashSet(supportCollector.topSupporters());
        Assert.assertEquals(tseta, tset3);
    }

}