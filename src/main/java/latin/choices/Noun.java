
package latin.choices;

import latin.forms.English;
import latin.forms.Suffix;
import latin.forms.Token;
import latin.forms.TokenRule;
import latin.forms.Tokens;
import latin.util.PathId;

import java.util.List;

import javax.annotation.Nullable;

public class Noun {

    public interface LatinEntry {
        public Token getGstem(Alts.Chooser chooser);
        public Token getForm(CaseNumber key, Alts.Chooser chooser);
    }

    public static StemFunction<LatinEntry> gstemFunction = new StemFunction<LatinEntry>() {
        @Override
        public Token apply(LatinEntry e, Alts.Chooser chooser) {
            return e.getGstem(chooser);
        }
    };

    public interface EnglishEntry {
        public Token getSingular(Alts.Chooser chooser);
        public Token getPlural(Alts.Chooser chooser);
    }

    public static abstract class AbstractLatinEntry implements LatinEntry {
        public abstract KeyValues<CaseNumber,Token> getStored();
        public abstract KeyValues<CaseNumber,TokenRule> getRules();
        public Token getForm(CaseNumber key, Alts.Chooser chooser) {
            return NounForms.getForm(key,
                                     getStored(),
                                     gstemFunction,
                                     this,
                                     getRules(),
                                     chooser);
        }
        public boolean hasStored(Object k) {
            return getStored().getValue(CaseNumber.getKey(k)) != null;
        }
    }

    public static class LatinEntryImpl extends AbstractLatinEntry {
        public String id;
        public Values<Token> gstem;
        public KeyValues<CaseNumber,Token> stored;
        public KeyValues<CaseNumber,TokenRule> rules;
        public LatinEntryImpl(String id,
                              Values<Token> gstem,
                              KeyValues<CaseNumber,Token> stored,
                              KeyValues<CaseNumber,TokenRule> rules) {
            this.id = id;
            this.gstem = gstem;
            this.stored = stored;
            this.rules = rules;
        }

        @Override
        public KeyValues<CaseNumber, Token> getStored() {
            return stored;
        }

        @Override
        public KeyValues<CaseNumber, TokenRule> getRules() {
            return rules;
        }
        @Override
        public Token getGstem(Alts.Chooser chooser) {
            return gstem.choose(chooser);
        }
    }

    public static LatinEntryImpl makeEntry(String nsi, String gst, String rn) {
        List<Token> nsiTokens = Tokens.parseTokens(nsi);
        String id = Suffix.unaccentString(nsiTokens.get(0));
        PathId.Element path = PathId.makeRoot(id);
        NounRules rules = NounRules.getRules(rn);
        Values<Token> gstem = new IdValuesList<Token>(path.makeChild("gstem"), Tokens.parseTokens(gst));
        if (rules.hasNomSi()) {
            return new LatinEntryImpl(id, gstem, NounForms.emptyStored, rules);
        }
        else {
            NounForms.StoredBuilder sb = new NounForms.StoredBuilder(path);
            sb.putValues("NomSi", nsiTokens);
            return new LatinEntryImpl(id, gstem, sb.build(), rules);
        }
    }

    // id
    // latin: rules, declension, features
    // english: rules, features
    // NomSi
    // EngSi
    // EngPl
    // gstem
    // GenSi

    public class RegularEnglishEntry implements EnglishEntry {
        final Values<Token> singular;
        public RegularEnglishEntry(Values<Token> singular) {
            this.singular = singular;
        }
        @Override
        public Token getSingular(Alts.Chooser chooser) {
            return singular.choose(chooser);
        }
        @Override
        public Token getPlural(Alts.Chooser chooser) {
            return English.pluralTokenRule.apply(getSingular(chooser));
        }
    }

    public class IrregularEnglishEntry implements EnglishEntry {
        final Values<Token> singular;
        final Values<Token> plural;
        public IrregularEnglishEntry(Values<Token> singular,
                                     Values<Token> plural) {
            this.singular = singular;
            this.plural = plural;
        }
        @Override
        public Token getSingular(Alts.Chooser chooser) {
            return singular.choose(chooser);
        }
        @Override
        public Token getPlural(Alts.Chooser chooser) {
            return plural.choose(chooser);
        }
    }

    public EnglishEntry makeEnglishEntry(String si, @Nullable String pl) {
        List<Token> sitl = Tokens.parseTokens(si);
        String id = sitl.get(0).toString();
        PathId.Element path = PathId.makeRoot(id);
        if (pl == null) {
            return new RegularEnglishEntry(new IdValuesList<Token>(path.makeChild("EngSi"), sitl));
        }
        else {
            List<Token> pltl = Tokens.parseTokens(pl);
            return new IrregularEnglishEntry(new IdValuesList<Token>(path.makeChild("EngSi"), sitl),
                                             new IdValuesList<Token>(path.makeChild("EngPl"), pltl));
        }
    }

}