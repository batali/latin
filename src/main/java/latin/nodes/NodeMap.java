
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicLongMap;
import latin.veritas.PropExpression;
import latin.veritas.PropParser;
import latin.veritas.Psetting;
import latin.veritas.StringParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class NodeMap implements Psetting.GetSetting<BooleanSetting> {

    private Map<String,Node<?>> nodeMap;
    private Map<String,List<Drule>> ruleMap;

    public NodeMap() {
        this.nodeMap = Maps.newTreeMap();
        this.ruleMap = Maps.newTreeMap();
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
                List<String> sl = Lists.newArrayList();
                int nc = n.setterCount();
                for (int i = 0; i < nc; i++) {
                    Setter<?> st = n.getIndexSetter(i);
                    if (st.supportable()) {
                        sl.add(n.getIndexSetter(i).toString());
                    }
                }
                System.out.println(n.toString() + sl.toString());
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
        public void retractLoop() {
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

    public BooleanSetting parseSetting (String ss) {
        StringParser sp = new StringParser(ss);
        Psetting ps = PropParser.parseSetting(sp, Psetting.simpleHandler);
        return ps.getSetting(this);
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

    public class SupSetter implements SupportHandler {

        final Set<Supporter> seen;
        final AtomicLongMap<TopSupporter> cm;
        final DQ dq;
        final RQ rq;
        final Set<BooleanSetting> tsettings;

        public SupSetter (Set<BooleanSetting> tsettings){
            this.seen = Sets.newHashSet();
            this.cm = AtomicLongMap.create();
            this.rq = new RQ();
            this.dq = new DQ();
            this.tsettings = tsettings;
        }

        public SupSetter () {
            this(new HashSet<BooleanSetting>());
        }

        public void recordBlockingSupporter(Supported setting) throws ContradictionException {
            Preconditions.checkState(!tsettings.contains(setting));
            Supporter ss = setting.getSupporter();
            if (ss != null) {
                if (ss instanceof TopSupporter) {
                    TopSupporter tss = (TopSupporter) ss;
                    System.out.println("Retracting blocker " + setting.toString());
                    retractTopSupporter(tss);
                    Preconditions.checkState(!setting.haveSupporter());
                }
                else {
                    seen.clear();
                    ss.handleSupport(this);
                }
            }
        }

        public Set<Supported> recordBlockingSet(Set<Supported> blockingSupporters) throws ContradictionException {
            Iterator<Supported> it = blockingSupporters.iterator();
            while (it.hasNext()) {
                Supported sup = it.next();
                recordBlockingSupporter(sup);
                if (!sup.haveSupporter()) {
                    it.remove();
                }
            }
            return blockingSupporters;
        }

        public Set<Supported> retractBlockingSet(Set<Supported> blocking) throws ContradictionException {
            int tc = blocking.size();
            int sc = recordBlockingSet(blocking).size();
            System.out.println("starting " + sc + "/" + tc);
            while (!blocking.isEmpty()) {
                retractNext();
                int nsc = keepSupported(blocking).size();
                if (nsc == sc) {
                    System.out.println("No retract " + nsc);
                    break;
                }
                sc = nsc;
            }
            return blocking;
        }

        public Set<Supported> keepSupported(Set<Supported> settings) {
            Iterator<Supported> it = settings.iterator();
            while (it.hasNext()) {
                if (!it.next().haveSupporter()) {
                    it.remove();
                }
            }
            return settings;
        }



        public boolean handleSupport(Supporter supporter, Supported supported) {
            if (seen.add(supporter)) {
                if (supporter instanceof TopSupporter) {
                    Supported sd = supporter.peekSupported();
                    System.out.println("recording tp " + supporter.toString() + " " + tsettings.contains(sd));
                    if (sd != null && !tsettings.contains(sd)) {
                        cm.incrementAndGet((TopSupporter) supporter);
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }


        public TopSupporter findNext() {
            TopSupporter bts = null;
            long bc = 0;
            Set<TopSupporter> removed = Sets.newHashSet();
            for (Map.Entry<TopSupporter,Long> e : cm.asMap().entrySet()) {
                long ec = e.getValue();
                TopSupporter ets = e.getKey();
                Supported esp = ets.peekSupported();
                if (esp == null) {
                    removed.add(ets);
                    continue;
                }
                Preconditions.checkState(!tsettings.contains(esp));
                if (bts == null || ec > bc) {
                    bts = ets;
                    bc = ec;
                }
            }
            for (TopSupporter rt : removed) {
                cm.remove(rt);
            }
            return bts;
        }

        public void retractTopSupporter(TopSupporter topSupporter) throws ContradictionException {
            if (topSupporter.haveSupported()) {
                topSupporter.retractAll(rq);
                rq.retractLoop();
            }
        }

        public boolean retractNext() throws ContradictionException {
            TopSupporter bts = findNext();
            if (bts != null) {
                long bc = cm.get(bts);
                System.out.println("Retracting bs " + bts.toString() + " " + bc);
                retractTopSupporter(bts);
                cm.remove(bts);
                return true;
            }
            else {
                return false;
            }
        }

        public void retractBlockers () throws ContradictionException {
            Set<Supported> blocking = SupportCollector.collectBlockers(tsettings);
            Preconditions.checkState(retractBlockingSet(blocking).isEmpty());
        }

        public boolean supportSetting(BooleanSetting tsetting) throws ContradictionException {
            if (tsetting.haveSupporter()) {
                return true;
            }
            else if (tsetting.supportable()) {
                TopSupporter tops = new TopSupporter();
                try {
                    if (tsetting.setSupporter(tops)) {
                        dq.addDeduced(tsetting);
                        dq.propagateLoop();
                        return true;
                    }
                }
                catch(ContradictionException ce) {
                    System.out.println("contra");
                    System.out.println("tops " + tops.toString());
                    seen.clear();
                    ce.atRule.handleSupport(this);
                }
            }
            else {
                Set<Supported> blockers = Sets.newHashSet();
                tsetting.supportedBlockers(blockers);
                if (!blockers.isEmpty()) {
                    System.out.println("blocking " + blockers.toString());
                    recordBlockingSet(blockers);
                }
            }
            return false;
        }

        public boolean supportSettings () throws ContradictionException {
            int step = 0;
            int nts = tsettings.size();
            while (true) {
                int nss = 0;
                for (BooleanSetting tsetting : tsettings) {
                    if (supportSetting(tsetting)) {
                        nss += 1;
                    }
                }
                step += 1;
                System.out.println("step " + step + " set " + nss + "/" + nts);
                if (nss == nts) {
                    return true;
                }
                if (!retractNext()) {
                    System.out.println("Nothing to retract");
                    return false;
                }
            }
        }
    }

    public boolean support(Set<BooleanSetting> tsettings) throws ContradictionException {
        SupSetter supSetter = new SupSetter(tsettings);
        supSetter.retractBlockers();
        return supSetter.supportSettings();
    }

    public boolean support (String... ssl) throws ContradictionException {
        Set<BooleanSetting> tsettings = Sets.newHashSet();
        for (String ss : ssl) {
            tsettings.add(parseSetting(ss));
        }
        return support(tsettings);
    }

    public boolean retract(Set<Supported> settings) throws ContradictionException {
        SupSetter supSetter = new SupSetter();
        return supSetter.retractBlockingSet(settings).isEmpty();
    }

    public boolean retract(String... ssl)  throws ContradictionException {
        Set<Supported> settings = Sets.newHashSet();
        for (String ss : ssl) {
            settings.add(parseSetting(ss));
        }
        return retract(settings);
    }

}









