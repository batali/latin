
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;
import junit.framework.Assert;
import latin.util.Shuffler;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PermuteSetTest {

    public static class PermuterSetter {

        List<Integer> sizes;
        List<Integer> positions;
        List<Node<?>> nodes;
        long totalCount;

        public PermuterSetter(NodeMap nodeMap) {
            sizes = Lists.newArrayList();
            positions = Lists.newArrayList();
            nodes = Lists.newArrayList();
            long tc = 1;
            for (Node<?> node : nodeMap.getNodes()) {
                int s = node.setterCount();
                tc *= s;
                sizes.add(s);
                positions.add(0);
                nodes.add(node);
            }
            this.totalCount = tc;
        }

        public List<BooleanSetting> settingList() {
            List<BooleanSetting> settings = Lists.newArrayList();
            int s = sizes.size();
            for (int i = 0; i < s; i++) {
                Node<?> node = nodes.get(i);
                settings.add(node.getIndexSetting(positions.get(i)));
            }
            return settings;
        }

        public List<BooleanSetting> positionSettingList(Long p) {
            List<BooleanSetting> settingList = Lists.newArrayList();
            int s = sizes.size();
            long ip = p;
            for (int i = 0; i < s; i++) {
                Node<?> node = nodes.get(i);
                int si = sizes.get(i);
                settingList.add(node.getIndexSetting(LongMath.mod(ip, si)));
                ip /= si;
            }
            return settingList;
        }

        public List<String> setVals() {
            List<String> vl = Lists.newArrayList();
            for (Node<?> node : nodes) {
                BooleanSetting s = node.getSupportedSetting();
                if (s != null) {
                    vl.add(s.toString());
                }
            }
            return vl;
        }
    }

    public static BooleanSetting findFromNode (List<BooleanSetting> settings, Node<?> n) {
        int nc = n.setterCount();
        for (BooleanSetting s : settings) {
            for (int i = 0; i < nc; i++) {
                if (Objects.equal(s, n.getIndexSetting(i))) {
                    return s;
                }
            }
        }
        return null;
    }

    public static BooleanSetting setNode(Node<?> n, TryPropagator tp) {
        if (n.getSupportedSetting() != null) {
            return null;
        }
        int ns = n.setterCount();
        Preconditions.checkState(ns > 0);
        int rp = Shuffler.nextInt(ns);
        for (int i = 0; i < ns; i++) {
            BooleanSetting setting = n.getIndexSetting((rp+i)%ns);
            if (setting.supportable()) {
                try {
                    if (tp.trySupport(setting)) {
                        return setting;
                    }
                }
                catch (ContradictionException ce) {
                    tp.retractContradiction();
                }
                finally {
                    tp.clear();
                }
            }
        }
        return null;
    }

    NodeMap nodeMap;
    Node<?> pn;

    @Before
    public void makeNodeMap() throws Exception {
        nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("an");
        nodeMap.makeBooleanNode("hu");
        nodeMap.makeValueNode("p", "1", "2", "3");
        nodeMap.makeValueNode("g", "m", "f", "n");
        pn = nodeMap.makeValueNode("pro", "I", "me", "we", "us", "you", "he", "him", "she", "her", "it", "they", "them");
        nodeMap.makeValueNode("nm", "si", "pl");
        nodeMap.makeValueNode("cs", "S", "O");
        nodeMap.makeDrules("hu -> an");
        nodeMap.makeDrules("g=n == !an");
        nodeMap.makeDrules("(p=1 | p=2) -> an");
        nodeMap.makeDrules("p=1 -> hu");
        nodeMap.makeDrules("pro=I -> (p=1 & nm=si & cs=S)");
        nodeMap.makeDrules("pro=me -> (p=1 & nm=si & cs=O)");
        nodeMap.makeDrules("pro=we -> (p=1 & nm=pl & cs=S)");
        nodeMap.makeDrules("pro=us -> (p=1 & nm=pl & cs=O)");
        nodeMap.makeDrules("pro=you == p=2");
        nodeMap.makeDrules("pro=he -> (p=3 & g=m & nm=si & cs=S)");
        nodeMap.makeDrules("pro=him -> (p=3 & g=m & nm=si & cs=O)");
        nodeMap.makeDrules("pro=she -> (p=3 & g=f & nm=si & cs=S)");
        nodeMap.makeDrules("pro=her -> (p=3 & g=f & nm=si & cs=O)");
        nodeMap.makeDrules("pro=it == (!an & nm=si)");
        nodeMap.makeDrules("pro=they == (p=3 & nm=pl & cs=S)");
        nodeMap.makeDrules("pro=them == (p=3 & nm=pl & cs=O)");
    }

    public static List<BooleanSetting> getConsistentSettings(NodeMap nodeMap, TryPropagator tp) {
        List<BooleanSetting> settingList = Lists.newArrayList();
        List<Node<?>> allNodes = Lists.newArrayList(nodeMap.getNodes());
        Collections.shuffle(allNodes);
        for (Node<?> n : allNodes) {
            BooleanSetting bs = n.getSupportedSetting();
            if (bs == null) {
                bs = setNode(n, tp);
            }
            Preconditions.checkNotNull(bs);
            settingList.add(bs);
        }
        return settingList;
    }

    @Test
    public void testSetNodes() throws ContradictionException {
        TryPropagator tp = new TryPropagator();
        List<Node<?>> allNodes = Lists.newArrayList(nodeMap.getNodes());
        int nc = allNodes.size();
        List<BooleanSetting> tsettings = Lists.newArrayList();
        List<BooleanSetting> dsettings = Lists.newArrayList();
        List<Integer> pl = Shuffler.choose(nc, nc);
        for (int p : pl) {
            Node<?> nn = allNodes.get(p);
            BooleanSetting bs = nn.getSupportedSetting();
            if (bs != null) {
                dsettings.add(bs);
            }
            else {
                bs = setNode(nn, tp);
                if (bs != null) {
                    System.out.println("st " + bs);
                    tsettings.add(bs);
                }
                else {
                    System.out.println("failed " + nn);
                    Assert.fail();
                }
            }
        }
        System.out.println(tsettings.toString());
        System.out.println(dsettings.toString());
        List<BooleanSetting> setSettings = nodeMap.setSettings();
        System.out.println(setSettings.toString());
        Assert.assertEquals(nc, setSettings.size());
        tp.retractAll(tsettings);
        setSettings = nodeMap.setSettings();
        System.out.println("after ret " + setSettings.toString());
        Assert.assertTrue(setSettings.isEmpty());
    }

    @Test
    public void testResetNodes() throws ContradictionException {
        TryPropagator tp = new TryPropagator();
        List<BooleanSetting> sl1 = getConsistentSettings(nodeMap, tp);
        System.out.println("sl1 " + sl1.toString());
        tp.retractAll(sl1);
        List<BooleanSetting> sl2 = getConsistentSettings(nodeMap, tp);
        System.out.println("sl2 " + sl2.toString());
        Retractor rt = new Retractor(sl1);
        for (BooleanSetting bs : sl1) {
            if (!tp.trySupport(bs,rt)) {
                System.out.println("failed " + bs);
            }
        }
        Set<BooleanSetting> sSettings = Sets.newHashSet(nodeMap.setSettings());
        Set<BooleanSetting> tSettings = Sets.newHashSet(sl1);
        System.out.println("set " + sSettings.toString());
        System.out.println("tar " + tSettings.toString());
        Assert.assertTrue(sSettings.containsAll(tSettings));
        Assert.assertTrue(tSettings.containsAll(sSettings));
    }

    @Test
    public void testPermuter()  throws ContradictionException {
        PermuterSetter ps = new PermuterSetter(nodeMap);
        System.out.println(ps.totalCount);
        TryPropagator tp = new TryPropagator();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            long vp = LongMath.mod(random.nextLong(), ps.totalCount);
            List<BooleanSetting> pettingList = ps.positionSettingList(vp);
            Collections.shuffle(pettingList);
            Retractor rt = new Retractor();
            System.out.println("");
            System.out.println("i " + i);
            /*
            BooleanSetting nt = findFromNode(pettingList, pn);
            if (nt != null) {
                System.out.println("found from node " + nt);
                pettingList.remove(nt);
            }
            */
            System.out.println("vp " + vp + " " + pettingList.toString());
            List<BooleanSetting> psets = Lists.newArrayList();
            List<BooleanSetting> fsets = Lists.newArrayList();
            boolean isfirst = true;
            for (BooleanSetting bs : pettingList) {
                if (tp.trySupport(bs, rt)) {
                    psets.add(bs);
                    rt.addTsetting(bs);
                }
                else {
                    fsets.add(bs);
                }
                if (isfirst) {
                    Preconditions.checkState(bs.haveSupporter());
                }
                Preconditions.checkState(nodeMap.checkCounts());
                isfirst = false;
            }
            List<Node<?>> nl = nodeMap.unsetNodes();
            if (!nl.isEmpty()) {
                Collections.shuffle(nl);
                for (Node<?> n : nl) {
                    BooleanSetting ns = setNode(n, tp);
                    if (ns != null) {
                        System.out.println("set node " + ns);
                        psets.add(ns);
                    }
                }
            }
            System.out.println("pset " + psets.toString());
            System.out.println("fset " + fsets.toString());
            System.out.println("vset " + ps.setVals().toString());
            if (pn.getSupportedSetting() != null) {
                System.out.print("pn " + pn.getSupportedSetting());
            }
        }
    }
}


