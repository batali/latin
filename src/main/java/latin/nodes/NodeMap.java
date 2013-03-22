
package latin.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import latin.veritas.PropExpression;
import latin.veritas.PropParser;
import latin.veritas.Psetting;
import latin.veritas.StringParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NodeMap implements Psetting.GetSetting<BooleanSetting> {

    private Map<String,Node<?>> nodeMap;
    private Map<String,List<DisjunctionRule>> ruleMap;

    public NodeMap() {
        this.nodeMap = Maps.newTreeMap();
        this.ruleMap = Maps.newTreeMap();
    }

    public Collection<Node<?>> getNodes() {
        return nodeMap.values();
    }

    public List<Node<?>> unsetNodes() {
        List<Node<?>> nodeList = Lists.newArrayList();
        for (Node<?> node : nodeMap.values()) {
            if (node.getSupportedSetting() == null) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    public List<Node<?>> setNodes() {
        List<Node<?>> nodeList = Lists.newArrayList();
        for (Node<?> node : nodeMap.values()) {
            if (node.getSupportedSetting() != null) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    public List<BooleanSetting> setSettings() {
        List<BooleanSetting> settingList = Lists.newArrayList();
        for (Node<?> node : nodeMap.values()) {
            BooleanSetting st = node.getSupportedSetting();
            if (st != null) {
                settingList.add(st);
            }
        }
        return settingList;
    }

    public BooleanNode makeBooleanNode(String path) {
        BooleanNode booleanNode = new BooleanNode(path);
        nodeMap.put(path, booleanNode);
        return booleanNode;
    }

    public <T> Node<T> makeValueNode(String path, List<T> values) {
        int n = values.size();
        Preconditions.checkState(n > 1);
        Node<T> node = (n == 2) ? new BinaryChoiceNode<T>(path, values) : new Vnode<T>(path, values);
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

    public void makeDrules(String name, List<List<Psetting>> cnf) {
        int s = cnf.size();
        List<DisjunctionRule> nrules = Lists.newArrayList();
        ruleMap.put(name, nrules);
        for (int i = 0; i < s; i++) {
            String rn = name + "." + i;
            nrules.add(new DisjunctionRule(rn, cnf.get(i), this));
        }
    }

    public void makeDrules(String name, String ps) {
        StringParser sp = new StringParser(ps);
        PropExpression pe = PropParser.parseProp(sp);
        makeDrules(name, pe.getCnf(true, Psetting.simpleHandler));
    }

    public void makeDrules(String ps) {
        makeDrules("[" + ps + "]", ps);
    }

    public void printNodes() {
        for(Node<?> n : nodeMap.values()) {
            BooleanSetting s = n.getSupportedSetting();
            if (s != null) {
                System.out.println(s.toString());
            }
            else {
                List<String> sl = Lists.newArrayList();
                int nc = n.setterCount();
                for (int i = 0; i < nc; i++) {
                    BooleanSetting st = n.getIndexSetting(i);
                    if (st.supportable()) {
                        sl.add(n.getIndexSetting(i).toString());
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
        for (List<DisjunctionRule> drules : ruleMap.values()) {
            for (DisjunctionRule dr : drules) {
                boolean bv = dr.checkCounts();
                if (!bv) {
                    System.out.println("Bad at " + dr.name);
                    rv = false;
                }
            }
        }
        for (Node<?> node : nodeMap.values()) {
            if (node instanceof Vnode) {
                boolean bv = ((Vnode<?>)node).checkCounts();
                if (!bv) {
                    System.out.println("Bad at " + node.toString());
                    rv = false;
                }
            }
        }
        return rv;
    }

    public boolean support(List<BooleanSetting> tsettings) throws ContradictionException {
        TryPropagator tp = new TryPropagator();
        return tp.trySupport(tsettings);
    }

    public boolean support (String... ssl) throws ContradictionException {
        return support(parseSettings(ssl));
    }

    public boolean retract(List<BooleanSetting> settings) throws ContradictionException {
        TryPropagator tp = new TryPropagator();
        Retractor rt = new Retractor();
        return tp.retractSupports(settings, rt);
    }

    public boolean retract(String... ssl)  throws ContradictionException {
        return retract(parseSettings(ssl));
    }

    public Retractor makeRetractor(String... ssl) {
        return new Retractor(parseSettings(ssl));
    }

    public boolean tryret(TryPropagator tp, Retractor rt, String ss) {
        return tp.retractTopSupporter(rt.selectTopSupporter(parseSetting(ss)));
    }

    public boolean trysup(TryPropagator tp, BooleanSetting setting) throws ContradictionException {
        return tp.trySupport(setting);
    }

    public boolean trysup(TryPropagator tp, String ss) throws ContradictionException {
        return trysup(tp, parseSetting(ss));
    }

}









