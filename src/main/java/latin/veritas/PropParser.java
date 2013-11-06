
package latin.veritas;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

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

    public static PropExpression makeNotExpression(PropExpression sub) {
        return new CompoundExpression(CompoundExpression.notOperator, sub);
    }

    public static PropExpression parseBounded(StringParser stringParser) {
        char c = stringParser.ignoreSpaces().charAt();
        if (c == '(') {
            stringParser.useChar(c);
            return parseProp(stringParser, true);
        }
        else if (c == '!') {
            stringParser.useChar(c);
            return makeNotExpression(parseBounded(stringParser));
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
        return bv ? valueExpression : makeNotExpression(valueExpression);
    }

    public static CompoundExpression makeAndExpression(List<PropExpression> subs) {
        return new CompoundExpression(CompoundExpression.andOperator, subs);
    }

    public static CompoundExpression makeOrExpression(List<PropExpression> subs) {
        return new CompoundExpression(CompoundExpression.orOperator, subs);
    }

    public static CompoundExpression.Operator getBinaryOperator(StringParser stringParser) {
        if (stringParser.usePrefix("->")) {
            return CompoundExpression.ifOperator;
        }
        if (stringParser.usePrefix("==")) {
            return CompoundExpression.iffOperator;
        }
        if (stringParser.usePrefix("^")) {
            return CompoundExpression.XorOperator;
        }
        return null;
    }

    public static PropExpression parseProp(StringParser stringParser, boolean parenp) {
        PropExpression exp1 = parseBounded(stringParser);
        stringParser.ignoreSpaces();
        if (stringParser.atEnd(parenp)) {
            return exp1;
        }
        CompoundExpression.Operator bop = getBinaryOperator(stringParser);
        if (bop != null) {
            PropExpression exp2 = parseBounded(stringParser);
            stringParser.ignoreSpaces();
            stringParser.requireEnd(parenp);
            return new CompoundExpression(bop, exp1, exp2);
        }
        else {
            List<PropExpression> orSubs = null;
            List<PropExpression> andSubs = null;
            do {
                if (stringParser.usePrefix("&")) {
                    if (andSubs == null) {
                        andSubs= Lists.newArrayList();
                    }
                    andSubs.add(exp1);
                }
                else if (stringParser.usePrefix("|")) {
                    if (andSubs != null) {
                        andSubs.add(exp1);
                        exp1 = makeAndExpression(andSubs);
                    }
                    if (orSubs == null) {
                        orSubs = Lists.newArrayList();
                    }
                    orSubs.add(exp1);
                    andSubs = null;
                }
                else {
                    stringParser.signalError("bad operator");
                }
                exp1 = parseBounded(stringParser);
                stringParser.ignoreSpaces();
            }
            while (!stringParser.atEnd(parenp));
            if (andSubs != null) {
                andSubs.add(exp1);
                exp1 = makeAndExpression(andSubs);
            }
            if (orSubs != null) {
                orSubs.add(exp1);
                exp1 = makeOrExpression(orSubs);
            }
            return exp1;
        }
    }

    public static PropExpression parseProp(StringParser stringParser) {
        return parseProp(stringParser, false);
    }

    public static PropExpression parseProp(String s) {
        return parseProp(new StringParser(s));
    }

    public static Psetting parseSetting(StringParser stringParser, Psetting.Handler handler) {
        if (stringParser.ignoreSpaces().atEnd(false)) {
            return null;
        }
        boolean sv = !stringParser.usePrefix("!");
        if (!stringParser.apply(slotStartChar)) {
            stringParser.signalError("bad start of setting");
        }
        String pathSpec = stringParser.getToken(slotPartCharacter);
        if (stringParser.atEnd(false) || stringParser.apply(StringParser.isSpacePredicate)) {
            return handler.getBooleanSetting(pathSpec, sv);
        }
        if (stringParser.usePrefix("!=")) {
            sv = !sv;
        }
        else if (!stringParser.usePrefix("=")) {
            stringParser.signalError("bad start to setting");
        }
        String choiceName = stringParser.getToken(valueChar);
        return handler.getValueSetting(pathSpec, choiceName, sv);
    }

    public static SettingSpec parseSetting(StringParser stringParser) {
        if (stringParser.ignoreSpaces().atEnd(false)) {
            return null;
        }
        boolean sv = !stringParser.usePrefix("!");
        if (!stringParser.apply(slotStartChar)) {
            stringParser.signalError("bad start of setting");
        }
        String pathSpec = stringParser.getToken(slotPartCharacter);
        if (stringParser.atEnd(false) || stringParser.apply(StringParser.isSpacePredicate)) {
            return new BooleanExpression(pathSpec).getSetting(sv);
        }
        if (stringParser.usePrefix("!=")) {
            sv = !sv;
        }
        else if (!stringParser.usePrefix("=")) {
            stringParser.signalError("bad start to setting");
        }
        String choiceName = stringParser.getToken(valueChar);
        return new ValueExpression(pathSpec, choiceName).getSetting(sv);
    }


    public static List<Psetting> parseSettingList(StringParser stringParser, Psetting.Handler handler) {
        List<Psetting> sl = Lists.newArrayList();
        while(true) {
            Psetting setting = parseSetting(stringParser, handler);
            if (setting == null) {
                return sl;
            }
            else {
                sl.add(setting);
            }
        }
    }

    public static List<SettingSpec> parseSettingList(StringParser stringParser) {
        List<SettingSpec> sl = Lists.newArrayList();
        while(true) {
            SettingSpec setting = parseSetting(stringParser);
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


