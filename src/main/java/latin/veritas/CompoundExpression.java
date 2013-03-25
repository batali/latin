
package latin.veritas;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CompoundExpression implements PropExpression {

    public static final List<Operator> operators = Lists.newArrayList();

    public abstract static class Operator {

        public final String name;

        public Operator(String name) {
            this.name = name;
            operators.add(this);
        }

        public int minSubCount() {
            return 2;
        }

        public int maxSubCount() {
            return Integer.MAX_VALUE;
        }

        public boolean checkSubs(List<PropExpression> subs) {
            int s = subs.size();
            return s >= minSubCount() && s <= maxSubCount();
        }

        public List<PropExpression> makeSubs(List<PropExpression> subs) {
            if (!checkSubs(subs)) {
                throw new IllegalArgumentException("Bad subs for " + name + " " + subs.toString());
            }
            return subs;
        }

        public abstract boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv);
        public abstract List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler);

        public String prettyPrint(List<PropExpression> subs, boolean top) {
            StringBuilder builder = new StringBuilder();
            if (!top) {
                builder.append("(");
            }
            int n = subs.size();
            for (int i = 0; i < n; i++) {
                if (i > 0) {
                    builder.append(" ");
                    builder.append(name);
                    builder.append(" ");
            }
                builder.append(subs.get(i).prettyPrint(false));
            }
            if (!top) {
                builder.append(")");
            }
            return builder.toString();
        }
    }

    public final Operator operator;
    private List<PropExpression> subExpressions;

    public CompoundExpression (Operator operator, List<PropExpression> subExpressions) {
        this.operator = operator;
        this.subExpressions = operator.makeSubs(subExpressions);
    }

    public CompoundExpression (Operator operator, PropExpression... subs) {
        this(operator, Lists.newArrayList(subs));
    }

    @Override
    public boolean meval(MevalEnvironment menv) {
        return operator.mevalSequence(subExpressions, menv);
    }

    @Override
    public void collectPaths(Set<String> pathStrings) {
        for (PropExpression propExpression : subExpressions) {
            propExpression.collectPaths(pathStrings);
        }
    }

    @Override
    public String asList() {
        int s = subExpressions.size();
        String [] sl = new String[s + 1];
        sl[0] = operator.name;
        for (int p = 0; p < s; p++) {
            sl[p+1] = subExpressions.get(p).asList();
        }
        return Arrays.toString(sl);
    }

    @Override
    public List<List<Psetting>> getCnf(boolean bv, Psetting.Handler handler) {
        return operator.getCnf(subExpressions, bv, handler);
    }

    @Override
    public String prettyPrint(boolean top) {
        return operator.prettyPrint(subExpressions, top);
    }

    @Override
    public int weight() {
        int tw = 0;
        for (PropExpression sub : subExpressions) {
            tw += sub.weight();
        }
        return tw;
    }

    public static final Operator notOperator = new Operator("!") {
        @Override
        public int minSubCount() {
            return 1;
        }
        @Override
        public int maxSubCount() {
            return 1;
        }
        @Override
        public boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv) {
            return !subs.get(0).meval(menv);
        }
        @Override
        public List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler) {
            return subs.get(0).getCnf(!bv, handler);
        }
        @Override
        public String prettyPrint(List<PropExpression> subs, boolean top) {
            return "!" + subs.get(0).prettyPrint(false);
        }
    };

    public static boolean mevalSubs(List<PropExpression> subs, boolean isConjunction, MevalEnvironment mevalEnvironment) {
        for (PropExpression pe : subs) {
            if (pe.meval(mevalEnvironment) != isConjunction) {
                return !isConjunction;
            }
        }
        return isConjunction;
    }

    public static List<List<Psetting>> subsCnf(List<PropExpression> subs,
                                               boolean isConjunction, boolean bv, Psetting.Handler handler) {
        if (isConjunction == bv) {
            return Psetting.conjoinSequence(subs, bv, handler);
        }
        else {
            return Psetting.disjoinSequence(subs, 0, subs.size(), bv, handler);
        }
    }

    public static final Operator andOperator = new Operator("&") {
        @Override
        public boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv) {
            return mevalSubs(subs, true, menv);
        }
        @Override
        public List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler) {
            return subsCnf(subs, true, bv, handler);
        }
    };

    public static final Operator orOperator = new Operator("|") {
        @Override
        public boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv) {
            return mevalSubs(subs, false, menv);
        }
        @Override
        public List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler) {
            return subsCnf(subs, false, bv, handler);
        }
    };

    public static List<List<Psetting>> getIfCnf(PropExpression lhs, PropExpression rhs, boolean bv, Psetting.Handler handler) {
        if (bv) {
            return Psetting.mergeNormalForms(lhs.getCnf(false, handler), rhs.getCnf(true, handler));
        }
        else {
            return Psetting.combineNormalForms(lhs.getCnf(true, handler), rhs.getCnf(false, handler));
        }
    }

    public static List<List<Psetting>> getIffCnf(PropExpression lhs, PropExpression rhs, boolean bv, Psetting.Handler handler) {
        if (bv) {
            return Psetting.combineNormalForms(
                    getIfCnf(lhs, rhs, true, handler),
                    getIfCnf(rhs, lhs, true, handler));
        }
        else {
            return Psetting.mergeNormalForms(
                    getIfCnf(lhs, rhs, false, handler),
                    getIfCnf(rhs, lhs, false, handler));
        }
    }

    public static final Operator ifOperator = new Operator("->") {
        @Override
        public int maxSubCount() {
            return 2;
        }
        @Override
        public boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv) {
            return !subs.get(0).meval(menv) || subs.get(1).meval(menv);
        }
        @Override
        public List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler) {
            return getIfCnf(subs.get(0), subs.get(1), bv, handler);
        }
    };

    public static final Operator iffOperator = new Operator("==") {
        @Override
        public int maxSubCount() {
            return 2;
        }
        @Override
        public boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv) {
            return subs.get(0).meval(menv) == subs.get(1).meval(menv);
        }
        @Override
        public List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler) {
            return getIffCnf(subs.get(0), subs.get(1), bv, handler);
        }
    };

    public static final Operator XorOperator = new Operator("^") {
        @Override
        public int maxSubCount() {
            return 2;
        }
        @Override
        public boolean mevalSequence(List<PropExpression> subs, MevalEnvironment menv) {
            return subs.get(0).meval(menv) != subs.get(1).meval(menv);
        }
        @Override
        public List<List<Psetting>> getCnf(List<PropExpression> subs, boolean bv, Psetting.Handler handler) {
            return getIffCnf(subs.get(0), subs.get(1), !bv, handler);
        }
    };

    public static Operator getOperator (String opName) {
        for (Operator op : operators) {
            if (op.name.equals(opName)) {
                return op;
            }
        }
        return null;
    }

}