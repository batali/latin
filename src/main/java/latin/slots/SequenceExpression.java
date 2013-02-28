
package latin.slots;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class SequenceExpression implements PropExpression {

    public static abstract class Operator {
        public abstract boolean isConjunction();
        public String getName() {
            return isConjunction() ? "&" : "|";
        }
        public boolean mevalSequence(List<PropExpression> propExpressions, MevalEnvironment mevalEnvironment) {
            boolean cv = isConjunction();
            for (PropExpression pe : propExpressions) {
                if (pe.meval(mevalEnvironment) != cv) {
                    return !cv;
                }
            }
            return cv;
        }
    }

    public static final Operator andOperator = new Operator() {
        @Override
        public boolean isConjunction() {
            return true;
        }
    };

    public static final Operator orOperator = new Operator() {
        @Override
        public boolean isConjunction() {
            return false;
        }
    };

    private Operator operator;
    private List<PropExpression> subExpressions;

    public SequenceExpression(Operator operator) {
        this.operator = operator;
        this.subExpressions = Lists.newArrayList();
    }

    public SequenceExpression addSub(PropExpression propExpression) {
        subExpressions.add(propExpression);
        return this;
    }

    @Override
    public boolean meval(MevalEnvironment mevalEnvironment) {
        return operator.mevalSequence(subExpressions, mevalEnvironment);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int n = subExpressions.size();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                builder.append(" ");
                builder.append(operator.getName());
                builder.append(" ");
            }
            builder.append(subExpressions.get(i).toString());
        }
        return builder.toString();
    }

    @Override
    public String asList() {
        int s = subExpressions.size();
        String [] sl = new String[s + 1];
        sl[0] = operator.getName();
        for (int p = 0; p < s; p++) {
            sl[p+1] = subExpressions.get(p).asList();
        }
        return Arrays.toString(sl);
    }

    public List<List<ISetting>> disjoinSequence(List<PropExpression> subExpressions, int sp, int ep,
                                                boolean bv, SettingHandler<ISetting> settingHandler)
            throws SettingSpecException {
        if (sp + 1 == ep) {
            return subExpressions.get(sp).getCnf(bv, settingHandler);
        }
        else {
            int mp = (sp + ep)/2;
            return NormalForm.mergeNormalForms(disjoinSequence(subExpressions, sp, mp, bv, settingHandler),
                                               disjoinSequence(subExpressions, mp, ep, bv, settingHandler));
        }
    }

    @Override
    public List<List<ISetting>> getCnf(boolean bv, SettingHandler<ISetting> settingHandler) throws SettingSpecException {
        if (operator.isConjunction() == bv) {
            List<List<ISetting>> to_sll = Lists.newArrayList();
            for (PropExpression subExpression : subExpressions) {
                NormalForm.adjoinNormalForm(subExpression.getCnf(bv, settingHandler), to_sll);
            }
            return NormalForm.sortNormalForm(to_sll);
        }
        else {
            return disjoinSequence(subExpressions, 0, subExpressions.size(), bv, settingHandler);
        }
    }

}