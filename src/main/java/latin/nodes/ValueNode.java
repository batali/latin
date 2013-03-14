
package latin.nodes;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class ValueNode<T> extends AbstractDrule implements Node<T>, ChoiceSettings {

    private List<ValueProp> valueProps;
    private ValueProp setProp;
    private Set<Supported> supportedSet;
    public final String pathString;

    public String getPathString() {
        return pathString;
    }

    public BSRule getRule() {
        return this;
    }

    public ValueNode(String pathString, List<T> values) {
        super(values.size());
        this.pathString = pathString;
        this.valueProps = Lists.newArrayList();
        this.setProp = null;
        this.supportedSet = Sets.newHashSet();
        for (T v : values) {
            valueProps.add(new ValueProp(v));
        }
    }

    public List<String> allChoiceNames() {
        return Lists.transform(valueProps, new Function<ValueProp, String>() {
            @Override
            public String apply(ValueProp valueProp) {
                return valueProp.valString();
            }
        });
    }

    public ValueNode(List<T> values) {
        this("", values);
    }

    public ValueProp getValueSetter(T v) {
        for (ValueProp vp : valueProps) {
            if (vp.value.equals(v)) {
                return vp;
            }
        }
        return null;
    }

    public Setter<T> getSupportedSetting() {
        return setProp;
    }

    public int setterCount() {
        return valueProps.size();
    }

    public ValueProp getIndexSetter(int i) {
        return valueProps.get(i);
    }

    public boolean retract(RetractQueue retractQueue) {
        boolean rv = false;
        Supported s = null;
        while ((s = peekSupported()) != null) {
            if (retractQueue.removeSupport(s)) {
                rv = true;
            }
        }
        return rv;
    }

    public abstract class ValuePropSetting extends BooleanSetting {
        public ValuePropSetting() {
            super();
        }
        @Override
        public void announceSet(DeduceQueue deduceQueue) throws ContradictionException {
            recordSet(getTrueSetting(), booleanValue(), deduceQueue);
            super.announceSet(deduceQueue);
        }
        @Override
        public void announceUnset(RetractQueue retractQueue, BSRule stopAt) {
            recordUnset(getTrueSetting(), booleanValue(), retractQueue, stopAt);
            if (!Objects.equal(getRule(), stopAt)) {
                super.announceUnset(retractQueue, stopAt);
            }
        }
        public abstract String valString();
        public String opString() {
            return booleanValue() ? "=" : "!=";
        }
        public String toString() {
            return pathString + opString() + valString();
        }
        public boolean supportable() {
            if (setProp == null) {
                return super.supportable();
            }
            else {
                return booleanValue() == Objects.equal(setProp, getTrueSetting());
            }
        }
    }
    class ValueProp extends ValuePropSetting implements Setter<T> {

        public final T value;
        public final ValuePropSetting falseSetting;

        public ValueProp getValueProp() {
            return this;
        }

        public T setterValue() {
            return value;
        }

        public ValuePropSetting getOpposite() {
            return falseSetting;
        }

        public String valString() {
            return value.toString();
        }

        public boolean booleanValue() {
            return true;
        }

        public boolean supportable() {
            if (setProp == null) {
                return super.supportable();
            }
            else {
                return Objects.equal(setProp, this);
            }
        }

        public void supportedBlockers(Set<Supported> sblockers) {
            if (!haveSupporter()) {
                BooleanSetting op = getOpposite();
                if (op.haveSupporter()) {
                    sblockers.add(op);
                }
                if (setProp != null && !Objects.equal(setProp, this) && setProp.haveSupporter()) {
                    sblockers.add(setProp);
                }
            }
        }

        public ValueProp(T value) {
            super();
            this.value = value;
            this.falseSetting = new ValuePropSetting() {
                @Override
                public ValueProp getOpposite() {
                    return getValueProp();
                }
                @Override
                public boolean booleanValue() {
                    return false;
                }
                public String valString() {
                    return getValueProp().valString();
                }
            };
        }

        @Override
        public void setSupport(Supporter newSupporter) {
            super.setSupport(newSupporter);
            if (setProp == null) {
                setProp = this;
            }
        }

        public boolean removeSupport() {
            try {
                return super.removeSupport();
            }
            finally {
                if (Objects.equal(setProp, this)) {
                    setProp = null;
                }
            }
        }
    }

    @Override
    public boolean contradictionCheck() {
        return trueCount > 1 || falseCount == settingCount;
    }

    public void deduce(DeduceQueue deduceQueue) throws ContradictionException {
        if (trueCount == 1)  {
            if (setProp != null && setProp.getSupporter() != getRule() && falseCount + 1 < valueProps.size()) {
                for (ValueProp vp : valueProps) {
                    if (vp.supporter == null) {
                        ValuePropSetting op = vp.getOpposite();
                        if (op.supportable()) {
                            deduceQueue.setSupport(op, this);
                        }
                    }
                }
            }
        }
        else {
            disjunctionDeduce(deduceQueue, valueProps);
        }
    }

    public void afterUnset(boolean sp, RetractQueue retractQueue) {
        if (sp) {
            if (!supportedSet.isEmpty()) {
                retract(retractQueue);
            }
            else if (disjunctionDeduceTest(valueProps)) {
                retractQueue.addRededucer(getRule());
        }
        else {
                if (setProp != null && setProp.getSupporter() != getRule()) {
                    retractQueue.addRededucer(getRule());
                }
                else if (!supportedSet.isEmpty()) {
                    retract(retractQueue);
                }
            }
        }
    }

    @Override
    public boolean addSupported(Supported supported) {
        Preconditions.checkState(Objects.equal(supported.getSupporter(), getRule()));
        return supportedSet.add(supported);
    }

    @Override
    public boolean removeSupported(Supported supported) {
        return supportedSet.remove(supported);
    }

    public Supported peekSupported() {
        if (supportedSet.isEmpty()) {
            return null;
        }
        else  {
            return supportedSet.iterator().next();
        }
    }

    public ValuePropSetting getChoiceSetting(String cn, boolean sv) {
        for (ValueProp vp : valueProps) {
            if (vp.value.toString().equalsIgnoreCase(cn)) {
                return sv ? vp : vp.getOpposite();
            }
        }
        return null;
    }

    public String toString() {
        return pathString;
    }

    public Pair<Integer,Integer> getCounts() {
        Preconditions.checkState((setProp != null) == (trueCount >= 1));
        return getCounts(valueProps);
    }

    public SupportCollector collectSupport(SupportCollector supportCollector) {
       if (setProp != null) {
           if (!supportedSet.contains(setProp)) {
               supportCollector.recordSupporter(setProp);
           }
           else {
               for (ValueProp vp : valueProps) {
                   supportCollector.recordSupporter(vp.getOpposite());
               }
           }
       }
        return supportCollector;
    }

    public void collectContradictionSupport(SupportCollector supportCollector) {
        if (trueCount == 0) {
            for (ValueProp valueProp : valueProps) {
                supportCollector.recordSupporter(valueProp.getOpposite());
            }
        }
        else {
            for (ValueProp valueProp : valueProps) {
                if (valueProp.haveSupporter()) {
                    supportCollector.recordSupporter(valueProp);
                }
            }
        }
    }

    public boolean doesSupport() {
        return !supportedSet.isEmpty();
    }
}