package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import latin.forms.Token;
import latin.forms.Tokens;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.List;
import java.util.Map;

public enum Declension {

    First("ae"),
    Second("ī"),
    Third("is"),
    Fourth("ūs"),
    Fifth("eī,ēī");

    public final List<Token> gsiEndings;
    final PathId.Element path;
    Map<String, LatinNoun.Rules> rulesMap;

    Declension(String gst) {
        this.gsiEndings = Tokens.parseTokens(gst);
        this.path = new PathId.Root(this);
        this.rulesMap = Maps.newHashMap();
    }

    public PathId.Element getPath() {
        return this.path;
    }

    LatinNoun.RulesId rulesId(String subname) {
        return new LatinNoun.RulesId(this, subname);
    }

    public LatinNoun.Rules getRules(String subname) {
        LatinNoun.Rules rules = rulesMap.get(subname);
        Preconditions.checkNotNull(rules);
        return rules;
    }

    void rules(String sn, String... strings) {
        LatinNoun.MapRules mapRules = new LatinNoun.MapRules(rulesId(sn));
        for (String string : strings) {
            for (String ss : Splitters.ssplitter(string)) {
                int p = ss.indexOf('=');
                if (p < 0) {
                    mapRules.putAll(getRules(ss));
                } else {
                    mapRules.addRule(ss.substring(0, p), ss.substring(p + 1));
                }
            }
        }
        rulesMap.put(sn, mapRules);
    }

    public static Declension getDeclension(Object d) {
        if (d instanceof Declension) {
            return (Declension) d;
        }
        else {
            return Declension.valueOf(d.toString());
        }
    }

    static {
        First.rules("mf",
                    "AccSi=am GenSi=ae DatSi=ae AblSi=ā LocSi=ae",
                    "NomPl=ae AccPl=ās GenPl=ārum DatPl=īs");
        First.rules("a", "mf NomSi=a");

        Second.rules("sh", "GenSi=ī DatSi=ō AblSi=ō LocSi=ī GenPl=ōrum DatPl=īs");
        Second.rules("mf", "sh AccSi=um NomPl=ī AccPl=ōs");
        Second.rules("n", "sh NomPl=a");
        Second.rules("us", "mf NomSi=us VocSi=e");
        Second.rules("um", "n NomSi=um");
        Second.rules("iu", "GenSi=ī,<");
        Second.rules("ius", "us iu VocSi=<");
        Second.rules("ium", "um iu");

        Third.rules("sh", "GenSi=is DatSi=ī AblSi=e GenPl=um DatPl=ibus");
        Third.rules("mf", "sh AccSi=em NomPl=ēs AccPl=ēs");
        Third.rules("n", "sh NomPl=a");
        Third.rules("ium", "GenPl=ium");
        Third.rules("i", "AblSi=ī");
        Third.rules("ie", "AblSi=ī,e");
        Third.rules("n.i", "sh ium i NomPl=ia");
        Third.rules("mf.mi", "mf ium");
        Third.rules("mf.pi", "mf.mi i");
    }
}



