
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicLongMap;
import latin.util.Shuffler;
import latin.veritas.PropExpression;
import latin.veritas.PropParser;
import latin.veritas.Psetting;
import latin.veritas.StringParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodeMap implements Psetting.GetSetting<BooleanSetting> {

    private Map<String,Node<?>> nodeMap;
    private Map<String,List<Drule>> ruleMap;

    public NodeMap() {
        this.nodeMap = Maps.newTreeMap();
        this.ruleMap = Maps.newTreeMap();
    }

    public Collection<Node<?>> getNodes() {
        return nodeMap.values();
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
        Propagator propagator = new Propagator();
        for (Drule r : nrules) {
            r.setCounts(propagator);
        }
        propagator.deduceLoop();
    }

    public void makeDrules(String name, String ps) throws ContradictionException {
        StringParser sp = new StringParser(ps);
        PropExpression pe = PropParser.parseProp(sp);
        makeDrules(name, pe.getCnf(true, Psetting.simpleHandler));
    }

    public void makeDrules(String ps) throws ContradictionException {
        makeDrules("[" + ps + "]", ps);
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
                    BooleanSetting st = n.getIndexSetter(i);
                    if (st.supportable()) {
                        sl.add(n.getIndexSetter(i).toString());
                    }
                }
                System.out.println(n.toString() + sl.toString());
            }
        }
    }


    public BooleanSetting parseSetting (String ss) {
        StringParser sp = new StringParser(ss);
        Psetting ps = PropParser.parseSetting(sp, Psetting.simpleHandler);
        return ps.getSetting(this);
    }

    public List<BooleanSetting> parseSettings(String[] ssa) {
        List<BooleanSetting> settings = Lists.newArrayList();
        for (String ss : ssa) {
            settings.add(parseSetting(ss));
        }
        return settings;
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

    public class NupSetter {
        final Propagator dq;
        final List<BooleanSetting> tsettings;

        public NupSetter(List<BooleanSetting> tsettings) {
            this.dq = new Propagator();
            this.tsettings = tsettings;
        }

        public List<TopSupporter> retractable(SupportCollector supportCollector,
                                              List<TopSupporter> rlist) {
            for (TopSupporter ts : supportCollector.topSupporters()) {
                if (!(rlist.contains(ts) || ts.containsAny(tsettings))) {
                    rlist.add(ts);
                }
            }
            return rlist;
        }

        public List<TopSupporter> retractable(SupportCollector supportCollector) {
            return retractable(supportCollector, new ArrayList<TopSupporter>());
        }

        public boolean retractTopSupport(SupportCollector supportCollector) {
            List<TopSupporter> rtops = retractable(supportCollector);
            System.out.println("rtops " + rtops.toString());
            TopSupporter rtop = Shuffler.randomElement(rtops);
            if (rtop == null) {
                return false;
            }
            try {
                rtop.retract(dq);
                dq.retractLoop();
                return true;
            }
            catch(ContradictionException ce) {
                SupportCollector csc = new SupportCollector();
                ce.atRule.collectContradictionSupport(csc);
                return retractTopSupport(csc);
            }
        }

        public boolean retractTopSupport(BooleanSetting op) {
            SupportCollector supportCollector = new SupportCollector();
            supportCollector.recordSupporter(op);
            return retractTopSupport(supportCollector);
        }

        public boolean retractSupport(BooleanSetting op) {
            while(op.haveSupporter()) {
                if (!retractTopSupport(op)) {
                    return false;
                }
            }
            return true;
        }

        public boolean retractSupports(List<BooleanSetting> settings) {
            for (BooleanSetting setting : settings) {
                if (!retractSupport(setting)) {
                    return false;
                }
            }
            return true;
        }

        public boolean supportSetting(BooleanSetting tsetting) throws ContradictionException {
            BooleanSetting op = tsetting.getOpposite();
            while(true) {
                if (op.haveSupporter()) {
                    if (!retractTopSupport(op)) {
                        return false;
                    }
                }
                else if (tsetting.haveSupporter()) {
                    return true;
                }
                else {
                    TopSupporter ntop = new TopSupporter(tsetting);
                    try {
                        ntop.deduce(dq);
                        dq.deduceLoop();
                        return true;
                    }
                    catch(ContradictionException ce) {
                        SupportCollector csc = new SupportCollector();
                        ce.atRule.collectContradictionSupport(csc);
                        if (retractTopSupport(csc)) {
                            return true;
                        }
                        else {
                            ntop.retract(dq);
                            dq.retractLoop();
                            return false;
                        }
                    }
                }
            }
        }

        public void supportSettings() throws ContradictionException {
            for (BooleanSetting tsetting : tsettings) {
                if (!supportSetting(tsetting)) {
                    System.out.println("failed to support " + tsetting.toString());
                    break;
                }
            }
        }
    }

    public class BupSetter extends SupportCollector {

        final AtomicLongMap<TopSupporter> cm;
        final Propagator dq;
        final Set<BooleanSetting> tsettings;

        public BupSetter (Set<BooleanSetting> tsettings){
            super();
            this.cm = AtomicLongMap.create();
            this.dq = new Propagator();
            this.tsettings = tsettings;
        }

        public BupSetter () {
            this(new HashSet<BooleanSetting>());
        }

        public void recordBlockingSupporter(Supported setting) throws ContradictionException {
            Preconditions.checkState(!tsettings.contains(setting));
            Supporter ss = setting.getSupporter();
            if (ss != null) {
                if (ss instanceof TopSupporter) {
                    TopSupporter tss = (TopSupporter) ss;
                    System.out.println("Retracting blocker " + tss.toString());
                    System.out.println("havd before " + tss.haveSupported());
                    System.out.println("havr before " + setting.haveSupporter());
                    retractTopSupporter(tss);
                    System.out.println("havd after " + tss.haveSupported());
                    System.out.println("havr after " + setting.haveSupporter());
                    if (setting.haveSupporter()) {
                        System.out.println("new sup " + setting.getSupporter().toString());
                    }
                  //  Preconditions.checkState(!setting.haveSupporter());
                }
                else {
                    seen.clear();
                    ss.collectSupport(this);
                }
            }
        }

        public Set<Supported> recordBlockingSet(Set<Supported> blockingSupporters) throws ContradictionException {
            Iterator<Supported> it = blockingSupporters.iterator();
            while (it.hasNext()) {
                Supported sup = it.next();
                if (sup.haveSupporter()) {
                    recordBlockingSupporter(sup);
                }
                if (!sup.haveSupporter()) {
                    it.remove();
                }
            }
            return blockingSupporters;
        }

        public Set<Supported> retractBlockingSet(Set<Supported> blocking) throws ContradictionException {
            int tc = blocking.size();
            int sc = recordBlockingSet(blocking).size();
            //System.out.println("starting " + sc + "/" + tc);
            while (!blocking.isEmpty()) {
                boolean rn = retractNext();
                int nsc = keepSupported(blocking).size();
                if (!rn && nsc == sc) {
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

        public boolean collectTopSupporter(Supporter supporter) {
            if (supporter instanceof TopSupporter) {
                TopSupporter tops = (TopSupporter) supporter;
                Supported sd = tops.peekSupported();
                System.out.println("recording tp " + supporter.toString() + " " + tsettings.contains(sd));
                if (sd != null && !tsettings.contains(sd)) {
                    cm.incrementAndGet(tops);
                }
            }
            return true;
        }

        @Override
        public boolean collectSupporter(Supporter supporter) {
            return super.collectSupporter(supporter) && collectTopSupporter(supporter);
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
            if (topSupporter.doesSupport()) {
                topSupporter.retract(dq);
                dq.retractLoop();
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
            System.out.println("retracting " + blocking.toString() + " " + blocking.size());
            if (!blocking.isEmpty()) {
                retractBlockingSet(blocking);
            }
        }

        public boolean supportSetting (BooleanSetting tsetting) throws ContradictionException {
            if (tsetting.haveSupporter()) {
                return true;
            }
            else if (tsetting.supportable()) {
                TopSupporter tops = new TopSupporter(tsetting);
                try {
                    tops.deduce(dq);
                    dq.deduceLoop();
                    return true;
                }
                catch(ContradictionException ce) {
                    System.out.println("contra");
                    System.out.println("tops " + tops.toString());
                    seen.clear();
                    ce.atRule.collectSupport(this);
                }
            }
            else {
                Set<Supported> blockers = Sets.newHashSet();
                tsetting.supportedBlockers(blockers);
                if (!blockers.isEmpty()) {
//                    System.out.println("blocking " + blockers.toString());
                    recordBlockingSet(blockers);
                }
            }
            return false;
        }

        public boolean supportSettings () throws ContradictionException {
            return supportSettings(tsettings);
        }

        public boolean supportSettings (Collection<BooleanSetting> settingList)  throws ContradictionException {
            int step = 0;
            int nts = settingList.size();
            while (true) {
                int nss = 0;
                for (BooleanSetting tsetting : settingList) {
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
        BupSetter supSetter = new BupSetter(tsettings);
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

    public void supports(List<BooleanSetting> tsettings) throws ContradictionException {
        NupSetter nupSetter = new NupSetter(tsettings);
        nupSetter.supportSettings();
    }

    public void supports (String... ssl) throws ContradictionException {
        List<BooleanSetting> tsettings = Lists.newArrayList();
        for (String ss : ssl) {
            tsettings.add(parseSetting(ss));
        }
        supports(tsettings);
    }

    public boolean retract(Set<Supported> settings) throws ContradictionException {
        BupSetter supSetter = new BupSetter();
        return supSetter.retractBlockingSet(settings).isEmpty();
    }

    public boolean retract(String... ssl)  throws ContradictionException {
        Set<Supported> settings = Sets.newHashSet();
        for (String ss : ssl) {
            settings.add(parseSetting(ss));
        }
        return retract(settings);
    }

    public void retracts(List<BooleanSetting> settings) throws ContradictionException {
        NupSetter nupSetter = new NupSetter(new ArrayList<BooleanSetting>());
        nupSetter.retractSupports(settings);
    }

    public void retracts (String... ssl) throws ContradictionException {
        List<BooleanSetting> tsettings = Lists.newArrayList();
        for (String ss : ssl) {
            tsettings.add(parseSetting(ss));
        }
        retracts(tsettings);
    }

    public Retractor makeRetractor(String... ssl) {
        return new Retractor(parseSettings(ssl));
    }

    public boolean tryret(Retractor rt, String ss) {
        return rt.retractTopSupporter(parseSetting(ss));
    }

    public boolean trysup(TryPropagator tp, BooleanSetting setting) throws ContradictionException {
        return tp.trySupport(setting);
    }

    public boolean trysup(TryPropagator tp, String ss) throws ContradictionException {
        return trysup(tp, parseSetting(ss));
    }

}









