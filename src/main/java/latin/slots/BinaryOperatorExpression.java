
package latin.slots;

import java.util.Arrays;
import java.util.List;

public class BinaryOperatorExpression implements PropExpression {

    public static abstract class Operator {
        public abstract String getName();
        public abstract boolean mevalExps(PropExpression lhs,
                                          PropExpression rhs,
                                          MevalEnvironment mevalEnvironment);
        public abstract List<List<ISetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, SettingHandler<ISetting> settingHandler)
            throws SettingSpecException;
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
        public List<List<ISetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, SettingHandler<ISetting> settingHandler)
                throws SettingSpecException {
            return getIfCnf(lhs, rhs, bv, settingHandler);
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
        public List<List<ISetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, SettingHandler<ISetting> settingHandler)
                throws SettingSpecException {
            return getIffCnf(lhs, rhs, bv, settingHandler);
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
        public List<List<ISetting>> getCnf(PropExpression lhs, PropExpression rhs, boolean bv, SettingHandler<ISetting> settingHandler)
                throws SettingSpecException {
            return getIffCnf(lhs, rhs, !bv, settingHandler);
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

    @Override
    public boolean meval(MevalEnvironment mevalEnvironment) {
        return operator.mevalExps(lhs, rhs, mevalEnvironment);
    }

    @Override
    public List<List<ISetting>> getCnf(boolean bv, SettingHandler<ISetting> settingHandler) throws SettingSpecException {
        return operator.getCnf(lhs, rhs, bv, settingHandler);
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

    public static List<List<ISetting>> getIfCnf(PropExpression lhs, PropExpression rhs, boolean bv, SettingHandler<ISetting> settingHandler)
            throws SettingSpecException {
        if (bv) {
            return NormalForm.mergeNormalForms(lhs.getCnf(false, settingHandler), rhs.getCnf(true, settingHandler));
        }
        else {
            return NormalForm.combineNormalForms(lhs.getCnf(true, settingHandler), rhs.getCnf(false, settingHandler));
        }
    }

    public static List<List<ISetting>> getIffCnf(PropExpression lhs, PropExpression rhs, boolean bv, SettingHandler<ISetting> settingHandler)
            throws SettingSpecException {
        if (bv) {
            return NormalForm.combineNormalForms(getIfCnf(lhs, rhs, true, settingHandler),
                                                 getIfCnf(rhs, lhs, true, settingHandler));
        }
        else {
            return NormalForm.mergeNormalForms(getIfCnf(lhs, rhs, false, settingHandler),
                                               getIfCnf(rhs, lhs, false, settingHandler));
        }
    }

}
