
package latin.nodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;
import org.junit.Test;

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
            List<BooleanSetting> settingList = Lists.newArrayList();
            int s = sizes.size();
            for (int i = 0; i < s; i++) {
                Node<?> node = nodes.get(i);
                settingList.add(node.getIndexSetter(positions.get(i)));
            }
            return settingList;
        }

        public List<BooleanSetting> positionSettingList(Long p) {
            List<BooleanSetting> settingList = Lists.newArrayList();
            int s = sizes.size();
            long ip = p;
            for (int i = 0; i < s; i++) {
                Node<?> node = nodes.get(i);
                int si = sizes.get(i);
                settingList.add(node.getIndexSetter(LongMath.mod(ip, si)));
                ip /= si;
            }
            return settingList;
        }

        public List<String> setVals() {
            List<String> vl = Lists.newArrayList();
            for (Node<?> node : nodes) {
                Setter<?> s = node.getSupportedSetting();
                if (s != null) {
                    vl.add(s.toString());
                }
            }
            return vl;
        }
    }

    @Test
    public void testPermuter()  throws ContradictionException {


        NodeMap nodeMap = new NodeMap();
        nodeMap.makeBooleanNode("an");
        nodeMap.makeBooleanNode("hu");
        nodeMap.makeValueNode("p", "1", "2", "3");
        nodeMap.makeValueNode("g", "m", "f", "n");
        nodeMap.makeValueNode("pro", "I", "me", "we", "us", "you", "he", "him", "she", "her", "it", "they", "them");
        nodeMap.makeValueNode("nm", "si", "pl");
        nodeMap.makeValueNode("cs", "S", "O");

        nodeMap.makeDrules("hu -> an");
        nodeMap.makeDrules("g=n == !an");
        nodeMap.makeDrules("(p=1 | p=2) -> an");
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


        PermuterSetter ps = new PermuterSetter(nodeMap);
        List<BooleanSetting> settingList = ps.settingList();
        System.out.println(settingList.toString());
        System.out.println(ps.totalCount);
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            long vp = LongMath.mod(random.nextLong(), ps.totalCount);
            List<BooleanSetting> pettingList = ps.positionSettingList(vp);
            System.out.println("vp " + vp + " " + pettingList.toString());
            Set<BooleanSetting> pettingSet = Sets.newHashSet(pettingList);
            nodeMap.support(pettingSet);
            nodeMap.printNodes();
            System.out.println("set " + ps.setVals().toString());
        }
    }
}