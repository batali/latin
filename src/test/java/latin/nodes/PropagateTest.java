
package latin.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import latin.choices.Case;
import latin.choices.Number;
import latin.choices.CaseNumber;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Set;

public class PropagateTest {

    private static Logger logger = LoggerFactory.getLogger(PropagateTest.class);

    NodeMap nodeMap;
    LinkedList<Supported> dqueue;
    LinkedList<Supported> rqueue;
    LinkedList<Deducer> rdqueue;
    AbstractDeduceQueue deduceQueue;
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

    public void checkStatus(String ss, int ts) {
        checkStatus(nodeMap.parseSetting(ss), ts);
    }

    public void checkCounts() {
        Assert.assertTrue(nodeMap.checkCounts());
    }

    public boolean psup(BooleanSetting bs, TopSupporter ts) throws ContradictionException {
        return deduceQueue.setSupport(bs, ts) && deduceQueue.propagateLoop();
    }

    public boolean psup(String ss, TopSupporter ts) throws ContradictionException {
        return psup(nodeMap.parseSetting(ss), ts);
    }

    public boolean rsup(TopSupporter ts) {
        return ts.retract(retractQueue) && retractQueue.retractLoop();
    }

    @Test
    public void testPropagateBoolean() throws Exception {
        logger.info("testPropagateBoolean");
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
        checkCounts();
        Assert.assertTrue(rsup(tops) && !retractQueue.haveRededucer());
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
        checkCounts();
        TopSupporter topt = new TopSupporter();
        Assert.assertTrue(psup(rs.getOpposite(), topt));
        checkStatus(ps, -1);
        checkStatus(qs, -1);
        checkStatus(rs, -1);
        checkCounts();
        Assert.assertTrue(rsup(topt) && !retractQueue.haveRededucer());
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 0);
        checkCounts();
        TopSupporter topq = new TopSupporter();
        Assert.assertTrue(psup(qs, topq));
        checkStatus(ps, 0);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        checkCounts();
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
        checkCounts();
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup(ss, top2));
        checkStatus(ps, 1);
        checkStatus(qs, 1);
        checkStatus(rs, 1);
        checkStatus(ss, 1);
        checkCounts();
        Assert.assertTrue(rsup(top1));
        Assert.assertTrue(retractQueue.haveRededucer());
        retractQueue.rededuceLoop(deduceQueue);
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        checkStatus(rs, 1);
        checkStatus(ss, 1);
        checkCounts();
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
        checkCounts();
        Assert.assertTrue(rsup(top1) && !retractQueue.haveRededucer());
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        checkCounts();
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup(fb.getOpposite(), top2));
        checkStatus(fa, 0);
        checkStatus(fb, -1);
        checkStatus(fc, 0);
        checkCounts();
        TopSupporter top3 = new TopSupporter();
        Assert.assertTrue(psup(fc.getOpposite(), top3));
        checkStatus(fa, 1);
        checkStatus(fb, -1);
        checkStatus(fc, -1);
        checkCounts();
        Assert.assertTrue(rsup(top2) && !retractQueue.haveRededucer());
        checkStatus(fa, 0);
        checkStatus(fb, 0);
        checkStatus(fc, -1);
        checkCounts();
        TopSupporter top4 = new TopSupporter();
        Assert.assertTrue(psup(fa.getOpposite(), top4));
        checkStatus(fa, -1);
        checkStatus(fb, 1);
        checkStatus(fc, -1);
        checkCounts();
        Assert.assertTrue(rsup(top3) && !retractQueue.haveRededucer());
        checkStatus(fa, -1);
        checkStatus(fb, 0);
        checkStatus(fc, 0);
        checkCounts();
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

    @Test
    public void testEnumNodes() throws Exception {
        nodeMap.makeValueNode("Case", Case.class);
        nodeMap.makeValueNode("Number", Number.class);
        nodeMap.makeValueNode("CaseNumber", CaseNumber.class);
        for (CaseNumber cn : CaseNumber.values()) {
            Case ck = cn.getCase();
            Number nk = cn.getNumber();
            nodeMap.makeDrules(cn.toString(), String.format("(Case=%s & Number=%s) == CaseNumber=%s",
                    ck.toString(), nk.toString(), cn.toString()));
        }
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup("Case=Abl", top1));
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup("Number=Si", top2));
        checkStatus("CaseNumber=AblSi", 1);
        checkStatus("CaseNumber=DatSi", -1);
        checkStatus("CaseNumber=AblPl", -1);
        checkStatus("CaseNumber=DatPl", -1);
        Assert.assertTrue(rsup(top2));
        retractQueue.rededuceLoop(deduceQueue);
        checkCounts();
        TopSupporter top3 = new TopSupporter();
        Assert.assertTrue(psup("Number=Pl", top3));
        checkStatus("CaseNumber=AblSi", -1);
        checkStatus("CaseNumber=DatSi", -1);
        checkStatus("CaseNumber=AblPl", 1);
        checkStatus("CaseNumber=DatPl", -1);
        Assert.assertTrue(rsup(top1));
        retractQueue.rededuceLoop(deduceQueue);
        checkCounts();
        TopSupporter top4 = new TopSupporter();
        Assert.assertTrue(psup("Case=Dat", top4));
        checkStatus("CaseNumber=AblSi", -1);
        checkStatus("CaseNumber=DatSi", -1);
        checkStatus("CaseNumber=AblPl", -1);
        checkStatus("CaseNumber=DatPl", 1);
        checkCounts();
    }

    @Test
    public void testSmallContradiction() throws Exception {
        BooleanSetting ps = nodeMap.makeBooleanNode("p").trueSetting;
        BooleanSetting qs = nodeMap.makeBooleanNode("q").trueSetting;
        nodeMap.makeDrules("r1", "p -> q");
        nodeMap.makeDrules("r2", "p -> !q");
        checkStatus(ps, 0);
        checkStatus(qs, 0);
        TopSupporter top1 = new TopSupporter();
        try {
            psup("p", top1);
            Assert.fail();
        }
        catch(ContradictionException ce) {
            logger.info("testSmallContradiction: {}", ce.getMessage());
            deduceQueue.retractContradiction(ce.atRule);
        }
        rsup(top1);
        checkCounts();
        TopSupporter top2 = new TopSupporter();
        Assert.assertTrue(psup("!p", top2));
        checkCounts();
    }

    @Test
    public void testValueContradiction() throws Exception {
        nodeMap.makeBooleanNode("p");
        nodeMap.makeBooleanNode("q");
        BooleanSetting rs = nodeMap.makeBooleanNode("r").trueSetting;
        nodeMap.makeValueNode("f", "a", "b", "c");
        nodeMap.makeDrules("r1", "p -> f=a");
        nodeMap.makeDrules("r2", "q -> f=b");
        nodeMap.makeDrules("r3", "r -> (p & q)");
        Assert.assertTrue(rs.supportable());
        TopSupporter top1 = new TopSupporter();
        try {
            psup(rs, top1);
            Assert.fail();
        }
        catch(ContradictionException ce) {
            logger.info("testValueContradiction: {}", ce.getMessage());
            deduceQueue.retractContradiction(ce.atRule);
        }
        rsup(top1);
        checkCounts();
    }

    @Test
    public void testBiggerContradiction() throws Exception {
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
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup("w", top1));
        checkCounts();
        TopSupporter top2 = new TopSupporter();
        try {
            psup("x", top2);
            Assert.fail();
        }
        catch(ContradictionException ce) {
            logger.info("testValueContradiction: {}", ce.getMessage());
            deduceQueue.retractContradiction(ce.atRule);
        }
        rsup(top2);
        checkCounts();
        checkStatus("w", 1);
        checkStatus("x", 0);
        TopSupporter top3 = new TopSupporter();
        Assert.assertTrue(psup("!x", top3));
        checkCounts();
    }

    @Test
    public void testWeirdValueContradiction() throws Exception {
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
        TopSupporter top1 = new TopSupporter();
        Assert.assertTrue(psup("d=i", top1));
        BooleanSetting bv = nodeMap.parseSetting("b=v");
        Assert.assertTrue(bv.haveSupporter());
        BooleanSetting a3 = nodeMap.parseSetting("a=3");
        Assert.assertTrue(a3.supportable());
        TopSupporter top3 = new TopSupporter();
        try {
            psup(a3, top3);
            Assert.fail();
        }
        catch(ContradictionException ce) {
            logger.info("testWeirdValueContradiction: {}", ce.getMessage());
            deduceQueue.retractContradiction(ce.atRule);
        }
        rsup(top3);
        checkCounts();
    }
}
