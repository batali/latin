
package latin.veritas;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BinaryOperatorExpression implements PropExpression {

    public static final List<String> operatorNames = Lists.newArrayList("->", "==", "^");

    public static abstract class Operator {
        public abstract String getName();
        public abstract boolean mevalExps(PropExpression lhs,
                                          PropExpression rhs,
                                          MevalEnvironment mevalEnvironment);
        public abstract List<List<Psetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, Psetting.Handler handler);
    }

    public static Operator ifOperator = new Operator() {
        @Override
        public String getName() {
            return "->";
        }
        @Override
        public boolean mevalExps(PropExpression lhs, PropExpression rhs, MevalEnvironment mevalEnvironment) {
            return !lhs.meval(mevalEnvironment) || rhs.meval(mevalEnvironment);
        }
        @Override
        public List<List<Psetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, Psetting.Handler handler) {
            return getIfCnf(lhs, rhs, bv, handler);
        }
    };

    public static Operator IffOperator = new Operator() {
        @Override
        public String getName() {
            return "==";
        }
        @Override
        public boolean mevalExps(PropExpression lhs, PropExpression rhs, MevalEnvironment mevalEnvironment) {
            return lhs.meval(mevalEnvironment) == rhs.meval(mevalEnvironment);
        }
        @Override
        public List<List<Psetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, Psetting.Handler handler) {
            return getIffCnf(lhs, rhs, bv, handler);
        }
    };

    public static Operator XorOperator = new Operator() {
        @Override
        public String getName() {
            return "^";
        }
        @Override
        public boolean mevalExps(PropExpression lhs, PropExpression rhs, MevalEnvironment mevalEnvironment) {
            return lhs.meval(mevalEnvironment) != rhs.meval(mevalEnvironment);
        }
        @Override
        public List<List<Psetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, Psetting.Handler handler) {
            return getIffCnf(lhs, rhs, !bv, handler);
        }
    };

    private Operator operator;
    private PropExpression lhs;
    private PropExpression rhs;

    public BinaryOperatorExpression(Operator operator, PropExpression lhs, PropExpression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String toString() {
        return lhs.toString() + " " + operator.getName() + " " + rhs.toString();
    }

    public String asList() {
        String [] sl =  { operator.getName(), lhs.asList(), rhs.asList() };
        return Arrays.toString(sl);
    }

    public String prettyPrint(boolean top) {
        StringBuilder sb = new StringBuilder();
        if (!top) {
            sb.append("(");
        }
        sb.append(lhs.prettyPrint(false));
        sb.append(' ');
        sb.append(operator.getName());
        sb.append(' ');
        sb.append(rhs.prettyPrint(false));
        if (!top) {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public void collectPaths(Set<String> pathStrings) {
        lhs.collectPaths(pathStrings);
        rhs.collectPaths(pathStrings);
    }

    @Override
    public boolean meval(MevalEnvironment mevalEnvironment) {
        return operator.mevalExps(lhs, rhs, mevalEnvironment);
    }

    @Override
    public List<List<Psetting>> getCnf(boolean bv, Psetting.Handler handler) {
        return operator.getCnf(lhs, rhs, bv, handler);
    }

    public static Operator getOperator (String op) {
        if (op.equals("->")) {
            return ifOperator;
        }
        else if (op.equals("==")) {
            return IffOperator;
        }
        else if (op.equals("^")) {
            return XorOperator;
        }
        else {
            throw new IllegalArgumentException("Unknown BinaryOperator " + op);
        }
    }

    public static Operator getOperator(StringParser stringParser) {
        if (stringParser.usePrefix("->")) {
            return ifOperator;
        }
        else if (stringParser.usePrefix("==")){
            return IffOperator;
        }
        else if (stringParser.usePrefix("^")) {
            return XorOperator;
        }
        else {
            return null;
        }
    }

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

}
