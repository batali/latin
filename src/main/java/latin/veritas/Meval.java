
package latin.veritas;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import latin.util.Shuffler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Meval {

    public static final List<String> booleanValues = Lists.newArrayList("F", "T");

    public static class Slot implements SlotSpec {
        public final String name;
        public final List<String> values;
        public Slot(String name, List<String> values) {
            this.name = name;
            this.values = values;
        }
        public Slot(String name, String... values) {
            this(name, Lists.newArrayList(values));
        }

        @Override
        public String getPathString() {
            return name;
        }

        @Override
        public int getChoiceCount() {
            return values.size();
        }

        @Override
        public boolean isBoolean() {
            return values.equals(booleanValues);
        }

        @Override
        public String getChoiceName(int p) {
            return values.get(p);
        }

        @Override
        public int getChoiceIndex(String choiceName) {
            int s = values.size();
            for (int i = 0; i < s; i++) {
                if (values.get(i).equalsIgnoreCase(choiceName)) {
                    return i;
                }
            }
            return -1;
        }
    }


    private Map<String, Slot> slotMap;

    public Meval() {
        this.slotMap = Maps.newHashMap();
    }

    public Slot addSlot(Slot slot) {
        Preconditions.checkState(!slotMap.containsKey(slot.name));
        slotMap.put(slot.name, slot);
        return slot;
    }

    public Slot addSlot(String name, List<String> values) {
        return addSlot(new Slot(name, values));
    }

    public Slot valueSlot(String name, String... values) {
        return addSlot(new Slot(name, values));
    }

    public Slot booleanSlot(String name) {
        return addSlot(name, booleanValues);
    }

    public List<Slot> getSlotList() {
        return Lists.newArrayList(slotMap.values());
    }

    public class Stepper {

        private MevalEnvironment menv;
        private List<Slot> slots;
        private int totalCount;

        public Stepper(Collection<String> slotNames) {
            this.menv = new MevalEnvironment();
            this.slots = Lists.newArrayList();
            this.totalCount = 1;
            for (String slotName : slotNames) {
                Slot slot = slotMap.get(slotName);
                slots.add(slot);
                this.totalCount *= slot.getChoiceCount();
            }
        }

        public Stepper scramble() {
            Shuffler.shuffle(slots);
            return this;
        }

        public Stepper setPositionValues(int position) {
            Preconditions.checkElementIndex(position, totalCount);
            for (Slot slot : slots) {
                int vc = slot.getChoiceCount();
                menv.put(slot.name, slot.getChoiceName(position % vc));
                position /= vc;
            }
            return this;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public boolean evalPropExpression(PropExpression propExpression) {
            return propExpression.meval(menv);
        }

        public boolean evalNormalForm(List<List<Psetting>> psettingsList, boolean amCnf) {
            return Psetting.mevalNormalForm(psettingsList, menv, amCnf);
        }

        public boolean evalSequence(List<Psetting> psettings, boolean amConjunction) {
            return Psetting.mevalSequence(psettings, menv, amConjunction);
        }

        public boolean evalCnf(List<List<Psetting>> psettingsList) {
            return evalNormalForm(psettingsList, true);
        }

        public List<String> getValues() {
            List<String> valueStrings = Lists.newArrayList();
            for (Slot slot : slots) {
                valueStrings.add(String.format("%s:%s", slot.name, menv.get(slot.name)));
            }
            Collections.sort(valueStrings);
            return valueStrings;
        }
    }

    public Stepper makeStepper(Collection<String> slotNames) {
        return new Stepper(slotNames);
    }

    public Stepper makeStepper(PropExpression pe) {
        Set<String> slotNames = Sets.newHashSet();
        pe.collectPaths(slotNames);
        return makeStepper(slotNames);
    }

}