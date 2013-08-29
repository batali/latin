
package latin.choices;

import latin.forms.Formf;
import latin.forms.Forms;
import latin.forms.IForm;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.StoredForms;
import latin.forms.Suffix;
import latin.forms.Token;
import latin.forms.TokenRule;
import latin.forms.Tokens;
import latin.util.PathId;

import java.util.EnumMap;
import java.util.List;

public class Noun {

    public static StoredForms<CaseNumber> emptyForms = new StoredForms<CaseNumber> () {
        @Override
        public Formf getStored(CaseNumber key) {
            return null;
        }
    };

    public interface Rules {
        public Rulef getRule(CaseNumber caseNumber);
    }

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

    public static <ET> IForm getForm(CaseNumber key,
                                     StoredForms<CaseNumber> storedForms,
                                     Stemf<ET> stemf,
                                     ET stemEntry,
                                     Rules rules,
                                     Alts.Chooser chooser) {
        if (storedForms != null) {
            Formf storedFormf = storedForms.getStored(key);
            if (storedFormf != null) {
                return storedFormf.apply(chooser);
            }
        }
        Rulef ruleFormf = rules.getRule(key);
        if (ruleFormf != null) {
            return Forms.applyRule(ruleFormf, Forms.applyStemf(stemf, stemEntry, chooser), chooser);
        }
        CaseNumber rkey = renamed.get(key);
        if (rkey != null) {
            return getForm(rkey, storedForms, stemf, stemEntry, rules, chooser);
        }
        else {
            return null;
        }
    }

    public static <ET> Token getNounToken (Tokens.Stored<CaseNumber> stored,
                                           ValueFunction<ET,Token> stemf,
                                           ET e,
                                           Tokens.Rules<CaseNumber> rules,
                                           CaseNumber key,
                                           Alts.Chooser chooser) {
        Token t = stored.getStoredToken(key, chooser);
        if (t != null) {
            return t;
        }
        TokenRule r = rules.getTokenRule(key, chooser);
        if (r != null) {
            return r.apply(stemf.testApply(e, chooser));
        }
        CaseNumber rkey = renamed.get(key);
        return (rkey != null)
               ? getNounToken(stored, stemf, e, rules, rkey, chooser)
               : null;
    }

    public static final ValueFunction<NE,Token> neStem = new ValueFunction<NE,Token>() {
        @Override
        public Token apply(NE e, Alts.Chooser chooser) {
            return e.stem.get(chooser);
        }
        @Override
        public boolean test(NE e) {
            return true;
        }
        @Override
        public Token testApply(NE e, Alts.Chooser chooser) {
            return apply(e, chooser);
        }
    };



    public static class NE {
        PathId.Element path;
        Tokens.Stored<CaseNumber> stored;
        Value<Token> stem;
        Tokens.Rules<CaseNumber> rules;
        public NE(PathId.Element path,
                  Tokens.Stored<CaseNumber> stored,
                  Value<Token> stem,
                  Tokens.Rules<CaseNumber> rules) {
            this.path = path;
            this.stored = stored;
            this.stem = stem;
            this.rules = rules;
        }
        public NE(Tokens.StoredTokensBuilder<CaseNumber> stb,
                  List<Token> gstems,
                  Tokens.Rules<CaseNumber> rules) {
            this(stb.getPath(),
                 stb.build(),
                 stb.makeValuesList("gstem", gstems),
                 rules);
        }
        public Token getForm(CaseNumber key, Alts.Chooser chooser) {
            return getNounToken(stored, neStem, this, rules, key, chooser);
        }
        public String getId() {
            return path.toString();
        }
        public String getSpec() {
            return path.toString() + ":" + rules.toString();
        }
    }

    public static Tokens.StoredTokensBuilder<CaseNumber> storedTokensBuilder(Object nst) {
        PathId.Element path = new PathId.Root(Suffix.unaccentString(nst.toString()));
        return Tokens.storedTokensBuilder(path, CaseNumber.class);
    }

    public static NE makeEntry(String gs, String rn) {
        List<Token> gstl = Tokens.parseTokens(gs);
        NounForms.Rules rl = NounForms.getRules(rn);
        TokenRule r = rl.getTokenRule(CaseNumber.NomSi, Alts.firstAlt);
        Token nsit = r.apply(gstl.get(0));
        Tokens.StoredTokensBuilder<CaseNumber> stb = storedTokensBuilder(nsit);
        return new NE(stb, gstl, rl);
    }

    public static NE makeEntry(String nsi, String gs, String rn) {
        List<Token> gstl = Tokens.parseTokens(gs);
        NounForms.Rules rl = NounForms.getRules(rn);
        Tokens.StoredTokensBuilder<CaseNumber> stb = storedTokensBuilder(nsi);
        stb.stored("NomSi", nsi);
        return new NE(stb, gstl, rl);
    }

}