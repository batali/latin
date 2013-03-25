
package latin.veritas;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SequenceExpression implements PropExpression {

    public static final List<String> operatorNames = Lists.newArrayList("&", "|");

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

    public static Operator getOperator(String opName) {
        if (opName.equals("&")) {
            return andOperator;
        }
        else if (opName.equals("|")) {
            return orOperator;
        }
        else {
            throw new IllegalArgumentException("Unknown sequence operator " + opName);
        }
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

    @Override
    public List<List<Psetting>> getCnf(boolean bv, Psetting.Handler handler) {
        if (operator.isConjunction() == bv) {
            return Psetting.conjoinSequence(subExpressions, bv, handler);
        }
        else {
            return Psetting.disjoinSequence(subExpressions, 0, subExpressions.size(), bv, handler);
        }
    }

    @Override
    public void collectPaths(Set<String> pathStrings) {
        for (PropExpression se : subExpressions) {
            se.collectPaths(pathStrings);
        }
    }

    @Override
    public String prettyPrint(boolean top) {
        StringBuilder builder = new StringBuilder();
        if (!top) {
            builder.append("(");
        }
        int n = subExpressions.size();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                builder.append(" ");
                builder.append(operator.getName());
                builder.append(" ");
            }
            builder.append(subExpressions.get(i).prettyPrint(false));
        }
        if (!top) {
            builder.append(")");
        }
        return builder.toString();
    }
}