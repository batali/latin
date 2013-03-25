
package latin.veritas;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import latin.util.Shuffler;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RandomExpression {

    private RandomExpression() {
    }

    public static final List<String> opNames = Lists.newArrayList("!", "!", "&", "&", "|", "|", "->", "==", "^");

    public static AtomicExpression randomAtomicExpression(SlotSpec slotSpec) {
        if (slotSpec.isBoolean()) {
            return new BooleanExpression(slotSpec.getPathString());
        }
        else {
            return new ValueExpression(
                    slotSpec.getPathString(),
                    slotSpec.getChoiceName(Shuffler.nextInt(slotSpec.getChoiceCount())));
        }
    }

    public static AtomicExpression randomAtomicExpression(List<? extends SlotSpec> slots) {
        return randomAtomicExpression(Shuffler.randomElement(slots));
    }

    public static PropExpression make(List<? extends SlotSpec> slots, int depth) {
        if (depth == 0) {
            return randomAtomicExpression(slots);
        }
        else {
            String op = Shuffler.randomElement(opNames);
            if (op.equals("!")) {
                return new CompoundExpression(CompoundExpression.notOperator, make(slots, Shuffler.nextInt(depth)));
//                return new NotExpression(make(slots, Shuffler.nextInt(depth)));
            }
            else {
                int d1 = Shuffler.nextInt(depth);
                int d2 = Shuffler.nextInt(depth - d1);
                PropExpression p1 = make(slots, d1);
                PropExpression p2 = make(slots, d2);
                return new CompoundExpression(CompoundExpression.getOperator(op), p1, p2);
                /*
                if (SequenceExpression.operatorNames.contains(op)) {
                    SequenceExpression se = new SequenceExpression(SequenceExpression.getOperator(op));
                    se.addSub(p1);
                    se.addSub(p2);
                    return se;
                }
                else {
                    return new BinaryOperatorExpression(BinaryOperatorExpression.getOperator(op), p1, p2);
                }
                */
            }
        }
    }

    public static PropExpression wake(List<? extends SlotSpec> slots, int tweight) {
        if (tweight <= 1) {
            return randomAtomicExpression(slots);
        }
        else {
            CompoundExpression.Operator cop = CompoundExpression.getOperator(Shuffler.randomElement(opNames));
            List<PropExpression> subs = Lists.newArrayList();
            int tsw = 0;
            while(subs.size() < cop.maxSubCount() && (subs.size() < cop.minSubCount() || tsw < tweight)) {
                int sw = 1;
                int dw = tweight - tsw;
                if (dw > 1) {
                    if (subs.size() + 1 == cop.maxSubCount()) {
                        sw = dw;
                    }
                    else {
                        sw = 1 + Shuffler.nextInt(dw-1);
                    }
                }
                PropExpression nsub = wake(slots, sw);
                subs.add(nsub);
                tsw += nsub.weight();
            }
            Shuffler.shuffle(subs);
            return new CompoundExpression(cop, subs);
        }
    }

    public static Psetting randomSetting(List<? extends SlotSpec> slotSpecs) {
        AtomicExpression ae = randomAtomicExpression(slotSpecs);
        return ae.getSetting(Shuffler.nextBoolean(), Psetting.simpleHandler);
    }

    public static Psetting randomSetting(SlotSpec slotSpec) {
        AtomicExpression ae = randomAtomicExpression(slotSpec);
        return ae.getSetting(Shuffler.nextBoolean(), Psetting.simpleHandler);
    }

    public static boolean addable(SlotSpec slotSpec, Set<Psetting> psettings) {
        String nps = slotSpec.getPathString();
        Set<String> ucs = Sets.newHashSet();
        int cc = slotSpec.getChoiceCount();
        boolean uv = true;
        for (Psetting ops : psettings) {
            if (ops.pathString.equals(nps)) {
                if (ucs.size() + 2 >= cc) {
                    return false;
                }
                if (ucs.isEmpty()) {
                    uv = ops.value;
                }
                else {
                    Preconditions.checkState(uv == ops.value);
                }
                ucs.add(nps);
            }
        }
        boolean sv = ucs.isEmpty() ? Shuffler.nextBoolean() : uv;
        if (slotSpec.isBoolean()) {
            Preconditions.checkState(ucs.isEmpty());
            return psettings.add(new Psetting.BooleanPsetting(nps, sv));
        }
        else {
            Preconditions.checkState(ucs.size() + 1 < cc);
            int rp = Shuffler.nextInt(cc);
            for (int i = 0; i < cc; i++) {
                String rcn = slotSpec.getChoiceName((rp+i)%cc);
                if (!ucs.contains(rcn) && psettings.add(new Psetting.ValuePsetting(nps, rcn, sv))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Set<Psetting> randomSettings(List<? extends SlotSpec> slotSpecs, int tsize) {
        Set<Psetting> psettingSet = new TreeSet<Psetting>(Psetting.PsettingOrdering);
        while(psettingSet.size() < tsize) {
            SlotSpec slotSpec = Shuffler.randomElement(slotSpecs);
            addable(slotSpec, psettingSet);
        }
        return psettingSet;
    }


}