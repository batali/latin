
package latin.setter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import latin.slots.GetSettingSpecHandler;
import latin.slots.ISetting;
import latin.slots.NormalForm;
import latin.slots.PathHandler;
import latin.slots.PropExpression;
import latin.slots.PropParser;
import latin.slots.SettingHandler;
import latin.slots.SettingSpecException;
import latin.slots.SettingTraits;
import latin.slots.SimpleSetting;
import latin.slots.SlotSettingSpec;
import latin.slots.StringParser;
import latin.slots.UseSettingSpecHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotMap implements UseSettingSpecHandler<ISlot>, GetSlotHandler, PathHandler<Setter> {

    public Map<String,ISlot> slotMap;
    private Map<String,DisjunctionRule> ruleMap;
    private Map<Setter,BaseSupporter> baseSupporterMap;
    private Propagator propagator;

    private GetSettingSpecHandler getSettingSpecHandler = new GetSettingSpecHandler() {
        @Override
        public SlotSettingSpec getSlotSettingSpec(String pathString) throws SettingSpecException {
            return getISlotSettingSpec(pathString);
        }
    };

    private SettingHandler<ISetting> settingHandler =
            NormalForm.getSettingHandler(getSettingSpecHandler, SimpleSetting.pathHandler);

    public SlotMap () {
        this.slotMap = new HashMap<String, ISlot>();
        this.ruleMap = new HashMap<String, DisjunctionRule>();
        this.baseSupporterMap = new HashMap<Setter, BaseSupporter>();
        this.propagator = new Propagator();
    }

    public Map<String,DisjunctionRule> getRuleMap() {
        return ruleMap;
    }

    public Map<String,ISlot> getSlotMap() {
        return slotMap;
    }

    public Propagator getPropagator () {
        return propagator;
    }

    public ISlot putSlot(ISlot slot) {
        slotMap.put(slot.getPathString(), slot);
        return slot;
    }

    public ISlot getSlot(String spec, boolean errorp) throws SettingSpecException {
        ISlot slot = slotMap.get(spec);
        if (slot == null && errorp) {
            throw new SettingSpecException("Unknown slot " + spec);
        }
        return slot;
    }

    public SlotSettingSpec getISlotSettingSpec(String pathString) throws SettingSpecException {
        ISlot slot = getSlot(pathString, true);
        return slot.getSlotSettingSpec();
    }

    public ISlot useBooleanSettingSpec(String pathString, SlotSettingSpec slotSettingSpec) {
        return putSlot(new BooleanSlot(pathString, slotSettingSpec));
    }

    public ISlot useBinarySettingSpec(String pathString, SlotSettingSpec slotSettingSpec) {
        return putSlot(new BinarySlot(pathString, slotSettingSpec));
    }

    public ISlot useValueSettingSpec(String pathString, SlotSettingSpec slotSettingSpec) {
        return putSlot(new ValueSlot(pathString, slotSettingSpec));
    }

    public <T extends ISlot> T castSlot(ISlot slot, Class<T> cls) throws SettingSpecException {
        if (!cls.isInstance(slot)) {
            throw new SettingSpecException("wrong class");
        }
        return cls.cast(slot);
    }

    public <T extends ISlot> T getCastSlot(String pathString, Class<T> cls) throws SettingSpecException {
        ISlot iSlot = getSlot(pathString, true);
        return castSlot(iSlot, cls);
    }

    public BooleanSlot getBooleanSlot(String pathSpec) throws SettingSpecException {
        return getCastSlot(pathSpec, BooleanSlot.class);
    }

    public BinarySlot getBinarySlot(String pathSpec) throws SettingSpecException {
        return getCastSlot(pathSpec, BinarySlot.class);
    }

    public ValueSlot getValueSlot(String pathSpec) throws SettingSpecException {
        return getCastSlot(pathSpec, ValueSlot.class);
    }

    @Override
    public Setter onBoolean(String pathString, boolean sv) throws SettingSpecException {
        return getBooleanSlot(pathString).getBooleanSetter(sv);
    }

    @Override
    public Setter onBinary(String pathString, String choiceName, boolean sv) throws SettingSpecException {
        return getBinarySlot(pathString).getBinarySetter(choiceName, sv);
    }

    @Override
    public Setter onValue(String pathString, String choiceName, int index, boolean sv) throws SettingSpecException {
        return getValueSlot(pathString).getSetter(choiceName, index, sv);
    }

    public Setter getSetter(ISetting iSetting) throws SettingSpecException {
        return iSetting.getTraits().applyOn(this, iSetting);
    }

    public List<Setter> getSetterList(List<ISetting> settings) throws SettingSpecException {
        List<Setter> setters = Lists.newArrayList();
        for (ISetting setting : settings) {
            setters.add(getSetter(setting));
        }
        return setters;
    }

    public void addRule(String rname, DisjunctionRule drule) {
        ruleMap.put(rname, drule);
    }

    public void addRule(String rname, List<ISetting> settings) throws SettingSpecException {
        addRule(rname, new DisjunctionRule(rname, getSetterList(settings)));
    }

    public void addRules(String baseName, List<List<ISetting>> sll) throws SettingSpecException {
        int s = sll.size();
        for (int i = 0; i < s; i++) {
            addRule(baseName + "." + i, sll.get(i));
        }
    }

    public void addRules(String baseName, PropExpression pe) throws SettingSpecException {
        List<List<ISetting>> sll = pe.getCnf(true, settingHandler);
        addRules(baseName, sll);
    }

    public void addRules(String baseName, String pss) throws SettingSpecException {
        PropExpression pe = PropParser.parseProp(new StringParser(pss));
        addRules(baseName, pe);
    }

    public void addRules(String pss) throws SettingSpecException {
        addRules("[" + pss + "]", pss);
    }

    public Setter getSetter(String ss) throws SettingSpecException {
        ISetting iSetting = PropParser.parseSetting(new StringParser(ss), settingHandler);
        return getSetter(iSetting);
    }

    public List<String> getAvailableChoices(ISlot iSlot) throws SettingSpecException {
        SlotSettingSpec slotSettingSpec = iSlot.getSlotSettingSpec();
        List<String> al = Lists.newArrayList();
        for (String cn : slotSettingSpec.getChoiceNames()) {
            Setter setter = iSlot.getSetter(cn, true);
            if (setter.getStatus() >= 0) {
                al.add(cn);
            }
        }
        return al;
    }

    public String getAvailable(ISlot iSlot) throws SettingSpecException {
        String ss = iSlot.getPathString();
        if (iSlot.haveValueSetter()) {
            return ss + " = "+ iSlot.getValueSetter().toString();
        }
        else {
            return ss + " < " +  getAvailableChoices(iSlot).toString();
        }
    }

    public void showSlots() throws SettingSpecException {
        for (Map.Entry<String,ISlot> e : slotMap.entrySet()) {
            String pathString = e.getKey();
            ISlot slot = e.getValue();
            System.out.println(getAvailable(slot));
        }
    }

    public boolean checkCounts(boolean verbose) throws SettingSpecException {
        boolean tokp = true;
        for (Map.Entry<String,ISlot> e : slotMap.entrySet()) {
            String pathString = e.getKey();
            ISlot slot = e.getValue();
            if (slot instanceof ValueSlot) {
                ValueSlot valueSlot = castSlot(slot, ValueSlot.class);
                boolean okp = valueSlot.getSupportRule().checkCounts();
                if (!okp || verbose) {
                    if (!okp) {
                        tokp = false;
                    }
                    String oks = okp ? "ok" : "bad";
                    System.out.println(valueSlot.getPathString() + " " + oks + " " + valueSlot.getSupportRule().getCountsString());
                }
            }
        }
        for (Map.Entry<String,DisjunctionRule> e : ruleMap.entrySet()) {
            String ruleName = e.getKey();
            DisjunctionRule disjunctionRule = e.getValue();
            boolean okp = disjunctionRule.checkCounts();
            if (!okp || verbose) {
                if (!okp) {
                    tokp = false;
                }
                String oks = okp ? "ok" : "bad";
                System.out.println(ruleName + " " + oks + " " + disjunctionRule.getCountsString());
            }
        }
        return tokp;
    }

    public void showRules() {
        for (Map.Entry<String,DisjunctionRule> e : ruleMap.entrySet()) {
            String ruleName = e.getKey();
            DisjunctionRule drule = e.getValue();
            System.out.println(drule.toString());
        }
    }

    public DisjunctionRule getRule(String rname) {
        return ruleMap.get(rname);
    }

    public void getTotalSupportedCount(int[] ca) throws SettingSpecException {
        int tk = 0;
        int ts = 0;
        for (ISlot iSlot : slotMap.values()) {
            SlotSettingSpec slotSettingSpec = iSlot.getSlotSettingSpec();
            List<String> choiceNames = slotSettingSpec.getChoiceNames();
            int s = choiceNames.size();
            if (s == 2) {
                ts += 1;
                Setter st = iSlot.getSetter(choiceNames.get(1), true);
                if (st.getStatus() != 0) {
                    tk += 1;
                }
            }
            else {
                for (int i = 0; i < s; i++) {
                    Setter st = iSlot.getSetter(choiceNames.get(i), true);
                    ts += 1;
                    if (st.getStatus() != 0) {
                        tk += 1;
                    }
                }
            }
        }
        ca[0] = tk;
        ca[1] = ts;
    }

    public void useSlots(UseSlotHandler<?> useSlotHandler) throws SettingSpecException {
        for (ISlot iSlot : slotMap.values()) {
            SettingTraits traits = iSlot.getSlotSettingSpec().getTraits();
            traits.useSlot(useSlotHandler, iSlot);
        }
    }

    public Setter addBaseSupport(Setter setter) throws ContradictionException {
        Preconditions.checkState(setter.getStatus() == 0);
        BaseSupporter baseSupporter = new BaseSupporter();
        baseSupporterMap.put(setter, baseSupporter);
        propagator.recordSupported(setter, baseSupporter);
        propagator.propagateLoop();
        return setter;
    }

    public void removeBaseSupport(Setter setter) throws ContradictionException  {
        BaseSupporter baseSupporter = baseSupporterMap.remove(setter);
        if (baseSupporter != null) {
            baseSupporter.retract(propagator);
            propagator.retractLoop();
        }
    }

    public void addSupport(String ss) throws SettingSpecException, ContradictionException {
        addBaseSupport(getSetter(ss));
    }

    public void removeSupport(String ss) throws SettingSpecException, ContradictionException {
        removeBaseSupport(getSetter(ss));
    }

    public void removeAllBaseSupport() throws ContradictionException {
        for (BaseSupporter baseSupporter : baseSupporterMap.values()) {
            if (baseSupporter.hasSupported()) {
                baseSupporter.retract(propagator);
                propagator.retractLoop();
            }
        }
        baseSupporterMap.clear();
    }
}