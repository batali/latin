
package latin.choices;

import com.google.common.collect.Maps;

import latin.forms.TokenRule;
import latin.forms.TokenRules;
import latin.forms.ValueMapBuilder;
import latin.util.PathId;

import java.util.EnumMap;
import java.util.Map;

public class NounRules implements KeyValues<CaseNumber,TokenRule> {

    public final Declension declension;
    public final String name;
    final EnumMap<CaseNumber,Values<TokenRule>> ruleMap;

    public NounRules(Declension declension, String name, Map<CaseNumber,Values<TokenRule>> rm) {
            this.declension = declension;
            this.name = name;
            this.ruleMap = new EnumMap<CaseNumber, Values<TokenRule>>(rm);
    }

    @Override
    public Value<TokenRule> getValue(CaseNumber key) {
        return getRules(key);
    }

    public Values<TokenRule> getRules(CaseNumber key) {
        return ruleMap.get(key);
    }

    @Override
    public boolean containsKey(CaseNumber key) {
        return ruleMap.containsKey(key);
    }

    public String toString() {
        return name;
    }

    public boolean hasNomSi() {
        return ruleMap.containsKey(CaseNumber.NomSi);
    }

    static Map<String, NounRules> rulesMap = Maps.newHashMap();

    public static NounRules getRules(Object x) {
        if (x instanceof NounRules) {
            return (NounRules)x;
        }
        else {
            NounRules r = rulesMap.get(x.toString());
            if (r == null) {
                throw new IllegalArgumentException("Unknown Rules " + x.toString());
            }
            return r;
        }
    }

    public static class RulesBuilder extends ValueMapBuilder<CaseNumber,TokenRule> {
        final Declension declension;
        RulesBuilder(Declension declension, PathId.Element path) {
            super(path,
                  new EnumMap<CaseNumber,Values<TokenRule>>(CaseNumber.class),
                  CaseNumber.toKey,
                  TokenRules.toTokenRule);
            this.declension = declension;
        }
        public RulesBuilder rule(Object k, String rs) {
            super.putValues(k, rs);
            return this;
        }
        public RulesBuilder putAll(NounRules r) {
            putAll(r.ruleMap);
            return this;
        }
        public NounRules build() {
            return new NounRules(declension, path.toString(), valuesMap);
        }
        public NounRules record() {
            NounRules rules = build();
            rulesMap.put(rules.name, rules);
            return rules;
        }
    }

    static RulesBuilder builder (Declension d) {
        return new RulesBuilder(d, d.path);
    }

    static RulesBuilder builder (Declension d, String subname) {
        return new RulesBuilder(d, d.getPath().makeChild(subname));
    }

    static RulesBuilder builder (NounRules rules, String subname) {
        RulesBuilder rb = builder(rules.declension, subname);
        return rb.putAll(rules);
    }

    static RulesBuilder builder (String rulesName, String subname) {
        return builder(getRules(rulesName), subname);
    }

    static {

        builder(Declension.First, "mf")
            .rule("AccSi", "am")
            .rule("GenSi", "ae")
            .rule("DatSi", "ae")
            .rule("AblSi", "ā")
            .rule("LocSi", "ae")
            .rule("NomPl", "ae")
            .rule("AccPl", "ās")
            .rule("GenPl", "ārum")
            .rule("DatPl", "īs")
            .record();
        builder("First.mf", "a")
            .rule("NomSi", "a")
            .record();

        NounRules secondShared = builder(Declension.Second)
            .rule("GenSi", "ī")
            .rule("DatSi", "ō")
            .rule("AblSi", "ō")
            .rule("LocSi", "ī")
            .rule("GenPl", "ōrum")
            .rule("DatPl", "īs")
            .build();
        builder(secondShared, "mf")
            .rule("AccSi", "um")
            .rule("NomPl", "ī")
            .rule("AccPl", "ōs")
            .record();
        builder(secondShared, "n")
            .rule("NomPl", "a")
            .record();

        NounRules secondIU = builder(Declension.Second, "iu")
            .rule("GenSi", "ī,<")
            .build();

        builder("Second.mf", "us")
            .rule("NomSi", "us")
            .rule("VocSi", "e")
            .record();

        builder("Second.mf", "ius")
            .putAll(secondIU)
            .rule("VocSi", "<")
            .record();

        builder("Second.mf", "mf.:")
            .rule("NomSi", ":")
            .record();

        builder("Second.mf", "mf.r:er")
            .rule("NomSi", "r:er")
            .record();

        builder("Second.n", "um")
            .rule("NomSi", "um")
            .record();

        builder("Second.um", "ium")
            .putAll(secondIU)
            .record();

        NounRules thirdShared = builder(Declension.Third)
            .rule("GenSi", "is")
            .rule("DatSi", "ī")
            .rule("AblSi", "e")
            .rule("GenPl", "um")
            .rule("DatPl", "ibus")
            .build();

        builder(thirdShared, "c.mf")
            .rule("AccSi", "em")
            .rule("NomPl", "ēs")
            .rule("AccPl", "ēs")
            .record();

        builder(thirdShared, "c.n")
            .rule("NomPl", "a")
            .record();

        NounRules thirdMixedI = builder(Declension.Third, "mi")
            .rule("GenPl", "ium")
            .build();

        builder("Third.c.mf", "mi.mf")
            .putAll(thirdMixedI)
            .record();

        builder("Third.c.n", "mi.n")
            .putAll(thirdMixedI)
            .rule("NomPl", "ia")
            .record();

        NounRules thirdPureI = builder(Declension.Third, "pi")
            .rule("AblSi", "ī")
            .build();

        builder("Third.mi.mf", "pi.mf")
            .putAll(thirdPureI)
            .rule("AccSi", "im")
            .rule("AccPl", "ēs,īs")
            .record();

        builder("Third.mi.n", "pi.n")
            .putAll(thirdPureI)
            .record();

        NounRules fourthShared = builder(Declension.Fourth)
            .rule("GenSi", "ūs")
            .rule("DatSi", "ū")
            .rule("AblSi", "ū")
            .rule("GenPl", "uum")
            .rule("DatPl", "ibus")
            .build();

        builder(fourthShared, "mf")
            .rule("DatSi", "uī")
            .rule("AccSi", "um")
            .rule("NomPl", "ūs")
            .record();

        builder(fourthShared, "n")
            .rule("NomPl", "ua")
            .record();

        builder("Fourth.mf", "us")
            .rule("NomSi", "ū")
            .record();

        builder("Fourth.n", "u")
            .rule("NomSi", "ū")
            .record();

        NounRules fifthShared = builder(Declension.Fifth)
            .rule("NomSi", "ēs")
            .rule("AccSi", "em")
            .rule("NomPl", "ēs")
            .rule("AblSi", "ē")
            .rule("GenPl", "ērum")
            .rule("DatPl", "ēbus")
            .build();

        builder(fifthShared, "c")
            .rule("GenSi", "eī")
            .rule("DatSi", "eī")
            .record();

        builder(fifthShared, "v")
            .rule("GenSi", "ēī")
            .rule("DatSi", "ēī")
            .record();
    }

}