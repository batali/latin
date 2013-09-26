
package latin.choices;

import latin.forms.StringToken;
import latin.forms.Token;
import latin.forms.TokenRule;
import latin.forms.ValueMapBuilder;
import latin.util.PathId;

import java.util.EnumMap;

public class NounForms {

    public static EnumMap<CaseNumber, CaseNumber> renamed =
        new EnumMap<CaseNumber, CaseNumber>(CaseNumber.class);

    static {
        renamed.put(CaseNumber.AccSi, CaseNumber.NomSi);
        renamed.put(CaseNumber.VocSi, CaseNumber.NomSi);
        renamed.put(CaseNumber.VocPl, CaseNumber.NomPl);
        renamed.put(CaseNumber.LocSi, CaseNumber.AblSi);
        renamed.put(CaseNumber.LocPl, CaseNumber.AblPl);
        renamed.put(CaseNumber.AblPl, CaseNumber.DatPl);
        renamed.put(CaseNumber.AccPl, CaseNumber.NomPl);
    }

    static Token applyRule(TokenRule rule, Token stem) {
        if (rule == null || stem == null) {
            return null;
        }
        else {
            return rule.apply(stem);
        }
    }

    public static <ET> Token getForm(CaseNumber key,
                                     KeyValues<CaseNumber,Token> stored,
                                     StemFunction<ET> stemFunction,
                                     ET entry,
                                     KeyValues<CaseNumber,TokenRule> rules,
                                     Alts.Chooser chooser) {
        Value<Token> sv = stored.getValue(key);
        if (sv != null) {
            return sv.choose(chooser);
        }
        Value<TokenRule> rv = rules.getValue(key);
        if (rv != null) {
            return applyRule (rv.choose(chooser), stemFunction.apply(entry, chooser));
        }
        CaseNumber rkey = renamed.get(key);
        return rkey != null ? getForm(rkey, stored, stemFunction, entry, rules, chooser) : null;
    }

    public static class StoredBuilder extends ValueMapBuilder<CaseNumber,Token> {
        public StoredBuilder(PathId.Element path) {
            super(path,
                  new EnumMap<CaseNumber, Values<Token>>(CaseNumber.class),
                  CaseNumber.toKey,
                  StringToken.toStringToken);
        }
        public KeyValues<CaseNumber,Token> build() {
            return makeKeyValues(valuesMap);
        }
    }

    public static final KeyValues<CaseNumber,Token> emptyStored = new KeyValues<CaseNumber,Token>() {
        @Override
        public Value<Token> getValue(CaseNumber key) {
            return null;
        }
        public boolean containsKey(CaseNumber key) {
            return false;
        }
    };

}



