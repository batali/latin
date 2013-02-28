
package latin.slots;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropParser {

    public static final Predicate<Character> slotStartChar = new Predicate<Character>() {
        @Override
        public boolean apply(@Nullable Character character) {
            return Character.isJavaIdentifierStart(character) || character.charValue()=='.';
        }
    };

    public static final Predicate<Character> slotPartCharacter = new Predicate<Character>() {
        @Override
        public boolean apply(@Nullable Character character) {
            return Character.isJavaIdentifierPart(character) || character.charValue()=='.';
        }
    };

    public static final Predicate<Character> valueChar = new Predicate<Character>() {
        @Override
        public boolean apply(@Nullable Character character) {
            return Character.isLetterOrDigit(character);
        }
    };

    public static PropExpression parseBounded(StringParser stringParser) {
        char c = stringParser.ignoreSpaces().charAt();
        if (c == '(') {
            stringParser.useChar(c);
            return parseProp(stringParser, true);
        }
        else if (c == '!') {
            stringParser.useChar(c);
            return new NotExpression(parseBounded(stringParser));
        }
        else if (slotStartChar.apply(c)) {
            return parseAtomic(stringParser);
        }
        else if (c == ')') {
            stringParser.signalError("unexpected close parenthesis");
            return null;
        }
        else {
            stringParser.signalError("bad start of bounded expression");
            return null;
        }
    }

    public static PropExpression parseAtomic(StringParser stringParser) {
        String pathSpec = stringParser.getToken(slotPartCharacter);
        stringParser.ignoreSpaces();
        boolean bv = true;
        if (stringParser.usePrefix("!=")) {
            bv = false;
        }
        else if (stringParser.startsWith("==") || !stringParser.usePrefix("=")) {
            return new BooleanExpression(pathSpec);
        }
        stringParser.ignoreSpaces();
        String choiceName = stringParser.getToken(valueChar);
        PropExpression valueExpression = new ValueExpression(pathSpec, choiceName);
        return bv ? valueExpression : new NotExpression(valueExpression);
    }

    public static SequenceExpression makeAndExpression() {
        return new SequenceExpression(SequenceExpression.andOperator);
    }

    public static SequenceExpression makeOrExpression() {
        return new SequenceExpression(SequenceExpression.orOperator);
    }

    public static PropExpression parseProp(StringParser stringParser, boolean parenp) {
        PropExpression exp1 = parseBounded(stringParser);
        stringParser.ignoreSpaces();
        if (stringParser.atEnd(parenp)) {
            return exp1;
        }
        BinaryOperatorExpression.Operator bop = BinaryOperatorExpression.getOperator(stringParser);
        if (bop != null) {
            PropExpression exp2 = parseBounded(stringParser);
            stringParser.ignoreSpaces();
            stringParser.requireEnd(parenp);
            return new BinaryOperatorExpression(bop, exp1, exp2);
        }
        else {
            SequenceExpression orExp = null;
            SequenceExpression andExp = null;
            do {
                if (stringParser.usePrefix("&")) {
                    if (andExp == null) {
                        andExp = makeAndExpression();
                    }
                    andExp.addSub(exp1);
                }
                else if (stringParser.usePrefix("|")) {
                    if (andExp != null) {
                        exp1 = andExp.addSub(exp1);
                    }
                    if (orExp == null) {
                        orExp = makeOrExpression();
                    }
                    orExp.addSub(exp1);
                    andExp = null;
                }
                else {
                    stringParser.signalError("bad operator");
                }
                exp1 = parseBounded(stringParser);
                stringParser.ignoreSpaces();
            }
            while (!stringParser.atEnd(parenp));
            if (andExp != null) {
                exp1 = andExp.addSub(exp1);
            }
            if (orExp != null) {
                exp1 = orExp.addSub(exp1);
            }
            return exp1;
        }
    }

    public static PropExpression parseProp(StringParser stringParser) {
        return parseProp(stringParser, false);
    }

    public static ISetting parseSetting(StringParser stringParser, SettingHandler<ISetting> settingHandler) throws SettingSpecException {
        if (stringParser.ignoreSpaces().atEnd(false)) {
            return null;
        }
        boolean sv = !stringParser.usePrefix("!");
        if (!stringParser.apply(slotStartChar)) {
            stringParser.signalError("bad start of setting");
        }
        String pathSpec = stringParser.getToken(slotPartCharacter);
//        SlotSettingSpec slotSettingSpec = settingHandler.getSlotSettingSpec(pathSpec);
        if (stringParser.atEnd(false) || stringParser.apply(StringParser.isSpacePredicate)) {
        //    return slotSettingSpec.getBooleanSetting(settingHandler, pathSpec, sv);
            return settingHandler.getBooleanSetting(pathSpec, sv);
        }
        if (stringParser.usePrefix("!=")) {
            sv = !sv;
        }
        else if (!stringParser.usePrefix("=")) {
            stringParser.signalError("bad start to setting");
        }
        String choiceName = stringParser.getToken(valueChar);
        //return slotSettingSpec.getValueSetting(settingHandler, pathSpec, choiceName, sv);
        return settingHandler.getValueSetting(pathSpec, choiceName, sv);
    }

    public static List<ISetting> parseSettingList(StringParser stringParser, SettingHandler<ISetting> settingHandler) throws SettingSpecException {
        List<ISetting> sl = Lists.newArrayList();
        while(true) {
            ISetting setting = parseSetting(stringParser, settingHandler);
            if (setting == null) {
                return sl;
            }
            else {
                sl.add(setting);
            }
        }
    }

    public static final Pattern setterPattern =
            Pattern.compile("([\\w._-]+)(!?=)([\\w._-]+)");

    public static Matcher parseSetter(String s) {
        return setterPattern.matcher(s);
    }
}


