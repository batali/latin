
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import latin.veritas.PropExpression;
import latin.veritas.PropParser;
import latin.veritas.Psetting;
import latin.veritas.StringParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class NodeMap implements Psetting.GetSetting<BooleanSetting> {

    private Map<String,Node<?>> nodeMap;
    private Map<String,List<Drule>> ruleMap;
    private Map<BooleanSetting,TopSupporter> topSupporterMap;

    public NodeMap() {
        this.nodeMap = Maps.newTreeMap();
        this.ruleMap = Maps.newTreeMap();
        this.topSupporterMap = Maps.newHashMap();
    }

    public BooleanNode makeBooleanNode(String path) {
        BooleanNode booleanNode = new BooleanNode(path);
        nodeMap.put(path, booleanNode);
        return booleanNode;
    }

    public <T> Node<T> makeValueNode(String path, List<T> values) {
        int n = values.size();
        Preconditions.checkState(n > 1);
        Node<T> node = (n == 2) ? new BinaryChoiceNode<T>(path, values) : new ValueNode<T>(path, values);
        nodeMap.put(path, node);
        return node;
    }

    public <T> Node<T> makeValueNode(String path, T... values) {
        return makeValueNode(path, Arrays.asList(values));
    }

    public <K extends Enum<K>>  Node<K> makeValueNode(String path, Class<K> kclass) {
        return makeValueNode(path, kclass.getEnumConstants());
    }

    public Node<?> getNode(String pathString) {
        Node<?> n = nodeMap.get(pathString);
        if (n == null) {
            throw new IllegalArgumentException("Unknown node " + pathString);
        }
        return n;
    }

    @Override
    public BooleanSetting getBooleanSetting(String pathString, boolean sv) {
        Node<?> n = getNode(pathString);
        Preconditions.checkState(n instanceof BooleanSettings);
        return ((BooleanSettings)n).getBooleanSetting(sv);
    }

    @Override
    public BooleanSetting getValueSetting(String pathString, String choiceName, boolean sv) {
        Node<?> n = getNode(pathString);
        Preconditions.checkState(n instanceof ChoiceSettings);
        BooleanSetting bs = ((ChoiceSettings) n).getChoiceSetting(choiceName, sv);
        Preconditions.checkNotNull(bs);
        return bs;
    }

    public void makeDrules(String name, List<List<Psetting>> cnf) throws ContradictionException {
        int s = cnf.size();
        List<Drule> nrules = Lists.newArrayList();
        ruleMap.put(name, nrules);
        for (int i = 0; i < s; i++) {
            String rn = name + "." + i;
            Drule drule = new Drule(rn, Psetting.transformPsettings(cnf.get(i), this));
            nrules.add(drule);
        }
        DQ dq = new DQ();
        for (Drule r : nrules) {
            r.setCounts(dq);
        }
        dq.propagateLoop();
    }

    public void makeDrules(String name, String ps) throws ContradictionException {
        StringParser sp = new StringParser(ps);
        PropExpression pe = PropParser.parseProp(sp);
        makeDrules(name, pe.getCnf(true, Psetting.simpleHandler));
    }

    public void printNodes() {
        for(Node<?> n : nodeMap.values()) {
            Setter<?> s = n.getSupportedSetting();
            if (s != null) {
                System.out.println(s.toString());
            }
            else {
                if (n instanceof ChoiceSettings) {
                    ChoiceSettings cs = (ChoiceSettings) n;
                    System.out.println(n.toString() + cs.allChoiceNames().toString());
                }
                else {
                    System.out.println(n.toString() + ":?");
                }
            }
        }
    }

    public static class CRQ implements RetractQueue {
        @Override
        public void addRetracted(Supported supported) {
        }
        @Override
        public void addRededucer(Deducer deducer) {
        }
    }

    public static class DQ implements DeduceQueue {
        private Queue<Supported> queue;
        public DQ () {
            queue = new LinkedBlockingQueue<Supported>();
        }
        public void addDeduced(Supported supported) {
            queue.add(supported);
        }
        public boolean isEmpty() {
            return queue.isEmpty();
        }
        public Supported peek() {
            return queue.peek();
        }
        public void propagateLoop() throws ContradictionException {
            Supported ns = null;
            while ((ns = queue.poll()) != null) {
                try {
                    ns.announceSet(this);
                }
                catch(ContradictionException ce) {
                    System.out.println("ce " + ce.getMessage());
                    CRQ crq = new CRQ();
                    Supporter atRule = ce.atRule;
                    if (atRule != null) {
                        System.out.println("At rule " + atRule.toString() + " " + ns.toString());
                        SupportCollector supportCollector = new SupportCollector();
                        atRule.collectSupport(supportCollector);
                        System.out.println(supportCollector.getTopSupporters().toString());
                    }
                    Preconditions.checkState(ns.unsetSupporter());
                    ns.announceUnset(crq, atRule);
                    while ((ns = queue.poll()) != null) {
                        Preconditions.checkState(ns.unsetSupporter());
                    }
                    throw ce;
                }
            }
        }
    }

    public static class RQ implements RetractQueue {
        private Queue<Supported> rqueue;
        private Set<Deducer> rdset;
        public RQ() {
            this.rqueue = new LinkedBlockingQueue<Supported>();
            this.rdset = Sets.newHashSet();
        }
        @Override
        public void addRetracted(Supported supported) {
            rqueue.add(supported);
        }

        @Override
        public void addRededucer(Deducer deducer) {
            rdset.add(deducer);
        }

        public void retractLoop() throws ContradictionException {
            Supported rs = null;
            while ((rs = rqueue.poll()) != null) {
                rs.announceUnset(this, null);
            }
            if (!rdset.isEmpty()) {
                DQ dq = new DQ();
                for (Deducer d : rdset) {
                    d.deduce(dq);
                    if (!dq.isEmpty()) {
                        dq.propagateLoop();
                    }
                }
            }
        }
    }

    public void supportSetting(BooleanSetting bs) throws ContradictionException {
        Preconditions.checkState(bs.supportable());
        if (bs.getSupporter() == null) {
            TopSupporter ts = new TopSupporter();
            if (bs.setSupporter(ts)) {
                topSupporterMap.put(bs, ts);
                DQ dq = new DQ();
                dq.addDeduced(bs);
                dq.propagateLoop();
            }
        }
    }

    public BooleanSetting parseSetting (String ss) {
        StringParser sp = new StringParser(ss);
        Psetting ps = PropParser.parseSetting(sp, Psetting.simpleHandler);
        return ps.getSetting(this);
    }

    public void retractSetting(BooleanSetting bs) throws ContradictionException {
        TopSupporter ts = topSupporterMap.get(bs);
        Preconditions.checkNotNull(ts);
        RQ rq = new RQ();
        ts.retractAll(rq);
        rq.retractLoop();
        topSupporterMap.remove(bs);
    }

    public void supportSetting(String ss) throws ContradictionException {
        supportSetting(parseSetting(ss));
    }

    public void retractSetting(String ss) throws ContradictionException {
        retractSetting(parseSetting(ss));
    }

    public boolean checkCounts() {
        boolean rv = true;
        for (List<Drule> drules : ruleMap.values()) {
            for (Drule dr : drules) {
                boolean bv = dr.checkCounts();
                if (!bv) {
                    System.out.println("Bad at " + dr.name);
                    rv = false;
                }
            }
        }
        for (Node<?> node : nodeMap.values()) {
            if (node instanceof ValueNode) {
                boolean bv = ((ValueNode<?>)node).checkCounts();
                if (!bv) {
                    System.out.println("Bad at " + node.toString());
                    rv = false;
                }
            }
        }
        return rv;
    }

}