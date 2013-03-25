
package latin.veritas;

import com.google.common.collect.Lists;
import latin.util.Shuffler;

import java.util.List;

public class RandomExpression {

    private RandomExpression() {
    }

    public static final List<String> opNames = Lists.newArrayList("!", "!", "&", "&", "|", "|", "->", "==", "^");

    public static PropExpression make(List<? extends SlotSpec> slots, int depth) {
        if (depth == 0) {
            SlotSpec slot = Shuffler.randomElement(slots);
            if (slot.isBoolean()) {
                return new BooleanExpression(slot.getPathString());
            }
            else {
                return new ValueExpression(
                        slot.getPathString(),
                        slot.getChoiceName(Shuffler.nextInt(slot.getChoiceCount())));
            }
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

}