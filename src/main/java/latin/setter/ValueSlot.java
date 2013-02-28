
package latin.setter;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import latin.slots.SettingSpecException;
import latin.slots.SettingTraits;
import latin.slots.SlotSettingSpec;

import java.util.List;
import java.util.Set;

public class ValueSlot extends AbstractSlot {

    private Set<Setter> supported;

    private ValueSlot getValueSlot() {
        return this;
    }

    public int supportMode() {
        if (supported.isEmpty()) {
            return 0;
        }
        else {
            Setter s1 = supported.iterator().next();
            if (s1.getValue()) {
                Preconditions.checkState(supported.size() == 1);
                return 1;
            }
            else {
                return -1;
            }
        }
    }

    public abstract class ValueSlotSetter extends Setter {

        public abstract ValueSlotSetter getTrueSetter();

        @Override
        public ISlot getSlot() {
            return getValueSlot();
        }

        @Override
        public void announceSupported(Propagator propagator) throws ContradictionException {
            supportRule.recordSupported(getTrueSetter(), getValue(), propagator);
            super.announceSupported(propagator);
        }

        @Override
        public void announceRetracted(Propagator propagator, SupportRule stopAfter) {
            supportRule.recordRetracted(getTrueSetter(), getValue(), propagator);
            if (!Objects.equal(supportRule, stopAfter)) {
                super.announceRetracted(propagator, stopAfter);
            }
        }
    }

    public class FalseValueSlotSetter extends ValueSlotSetter {

        private ValueSlotSetter trueSetter;

        public FalseValueSlotSetter(ValueSlotSetter trueSetter) {
            super();
            this.trueSetter = trueSetter;
        }

        @Override
        public boolean getValue() {
            return false;
        }
        @Override
        public String getChoiceName() {
            return trueSetter.getChoiceName();
        }

        @Override
        public int getIndex() {
            return trueSetter.getIndex();
        }

        @Override
        public ValueSlotSetter getTrueSetter() {
            return trueSetter;
        }

        @Override
        public ValueSlotSetter getOpposite() {
            return trueSetter;
        }

    }

    public class TrueValueSlotSetter extends ValueSlotSetter {
        private ValueSlotSetter falseSetter;
        public final String choiceName;
        public final int index;
        public TrueValueSlotSetter(String choiceName, int index) {
            super();
            this.choiceName = choiceName;
            this.index = index;
            this.falseSetter = new FalseValueSlotSetter(this);
        }
        @Override
        public String getChoiceName() {
            return choiceName;
        }
        @Override
        public boolean getValue() {
            return true;
        }
        @Override
        public int getIndex() {
            return index;
        }
        @Override
        public ValueSlotSetter getTrueSetter() {
            return this;
        }
        @Override
        public ValueSlotSetter getOpposite() {
            return falseSetter;
        }
        public ValueSlotSetter getValueSetter(boolean sv) {
            return sv ? this : falseSetter;
        }
    }

    private List<TrueValueSlotSetter> setters;

    public ValueSlot(String pathString, SlotSettingSpec slotSettingSpec) {
        super(pathString, slotSettingSpec);
        this.supported = Sets.newHashSet();
        this.setters = Lists.newArrayList();
        List<String> choiceNames = slotSettingSpec.getChoiceNames();
        int s = choiceNames.size();
        for (int i = 0; i < s; i++) {
            setters.add(new TrueValueSlotSetter(choiceNames.get(i), i));
        }
    }

    public ValueSlotSetter getSetter(int index, boolean sv) {
        return setters.get(index).getValueSetter(sv);
    }

    public ValueSlotSetter getSetter(String choiceName, boolean sv) throws SettingSpecException {
        int i = SettingTraits.findChoiceIndex(choiceName, slotSettingSpec.getChoiceNames(), true);
        return setters.get(i).getValueSetter(sv);
    }

    public ValueSlotSetter getSetter(String choiceName, int index, boolean sv) throws SettingSpecException {
        Preconditions.checkElementIndex(index, setters.size());
        TrueValueSlotSetter trueValueSlotSetter = setters.get(index);
        if (!trueValueSlotSetter.getChoiceName().equals(choiceName)) {
            throw new SettingSpecException("wrong name");
        }
        return trueValueSlotSetter.getValueSetter(sv);
    }

    private AbstractDisjunctionRule supportRule = new AbstractDisjunctionRule() {

        @Override
        public List<TrueValueSlotSetter> getSetters() {
            return setters;
        }

        @Override
        public void addSupported(Setter setter) {
            supported.add(setter);
        }

        @Override
        public void removeSupported(Setter setter) {
            supported.remove(setter);
        }

        @Override
        public void deduce(Propagator propagator) throws ContradictionException {
            if (trueCount == 0) {
                disjunctionDeduce(propagator);
            }
            else if (trueCount + falseCount < setters.size()) {
                for (Setter setter : setters) {
                    if (setter.getStatus() == 0) {
                        propagator.recordSupported(setter.getOpposite(), this);
                    }
                }
            }
        }

        @Override
        public void retract(Propagator propagator) {
            int sm = supportMode();
            if (sm > 0 && falseCount + 1 < setters.size()) {
                BaseSupporter.retractSet(supported, this, propagator);
            }
            else if (sm == 0 && disjunctionReDeduceTest()) {
                propagator.addRededucer(this);
            }
            else if (sm < 0) {
                if (trueCount == 0) {
                    BaseSupporter.retractSet(supported, this, propagator);
                }
                else {
                    propagator.addRededucer(this);
                }
            }
        }

        @Override
        public void recordSupported(Setter setter, boolean tv, Propagator propagator) throws ContradictionException {
            if (tv) {
                if (oValueSetter.isPresent()) {
                    System.out.println("contra thre");
                    propagator.recordContradiction(this, oValueSetter.get(), setter);
                }
                else {
                    oValueSetter = Optional.of(setter);
                }
            }
            super.recordSupported(setter, tv, propagator);
        }

        @Override
        public void recordRetracted(Setter setter, boolean tv, Propagator propagator) {
            if (tv && Objects.equal(oValueSetter.orNull(), setter)) {
                oValueSetter = Optional.absent();
            }
            super.recordRetracted(setter, tv, propagator);
        }

        public String toString () {
            return "ValueSlot." + getPathString();
        }

        public boolean hasSupported() {
            return !supported.isEmpty();
        }

    };

    public AbstractDisjunctionRule getSupportRule() {
        return supportRule;
    }

}
