package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import latin.forms.Token;
import latin.forms.Tokens;
import latin.util.PathId;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public enum Declension {

    First("ae"),
    Second("ī"),
    Third("is"),
    Fourth("ūs"),
    Fifth("eī,ēī");

    public final List<Token> gsiEndings;
    final PathId path;
    Map<String, LatinNoun.Rules> rulesMap;

    Declension(String gst) {
        this.gsiEndings = Tokens.parseTokens(gst);
        this.path = PathId.makeRoot(this);
        this.rulesMap = Maps.newHashMap();
    }

    public LatinNoun.Rules getRules(String subname) {
        LatinNoun.Rules rules = rulesMap.get(subname);
        Preconditions.checkNotNull(rules);
        return rules;
    }

    void rules(String sn, @Nullable String uses, String... addStrings) {
        LatinNoun.MapRules mapRules = new LatinNoun.MapRules(path.makeChild(sn));
        mapRules.use(uses, this::getRules);
        for (String adds : addStrings) {
            mapRules.add(adds, CaseNumber::fromString);
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
        First.rules("mf", null,
                    "AccSi=am GenSi=ae DatSi=ae AblSi=ā LocSi=ae",
                    "NomPl=ae AccPl=ās GenPl=ārum DatPl=īs");
        First.rules("a", "mf", "NomSi=a");

        Second.rules("sh", null, "GenSi=ī DatSi=ō AblSi=ō LocSi=ī GenPl=ōrum DatPl=īs");
        Second.rules("mf", "sh",  "AccSi=um NomPl=ī AccPl=ōs");
        Second.rules("n", "sh", "NomPl=a");
        Second.rules("us", "mf", "NomSi=us VocSi=e");
        Second.rules("um", "n", "NomSi=um");
        Second.rules("iu", null, "GenSi=ī,<");
        Second.rules("ius", "us iu", "VocSi=<");
        Second.rules("ium", "um iu");
        Second.rules("r", "mf", "NomSi=-er");
        Second.rules("er", "mf", "NomSi=:");

        Third.rules("sh", null, "GenSi=is DatSi=ī AblSi=e GenPl=um DatPl=ibus");
        Third.rules("mf", "sh", "AccSi=em NomPl=ēs AccPl=ēs");
        Third.rules("n", "sh", "NomPl=a");
        Third.rules("ium", null, "GenPl=ium");
        Third.rules("i", null, "AblSi=ī");
        Third.rules("ie", null, "AblSi=ī,e");
        Third.rules("n.i", "sh ium i", "NomPl=ia");
        Third.rules("mf.mi", "mf ium");
        Third.rules("mf.pi", "mf.mi i");

        Fourth.rules("sh", null, "GenSi=ūs AblSi=ū GenPl=uum DatPl=ibus");
        Fourth.rules("mf", "sh", "DatSi=uī,ū AccSi=um NomPl=ūs AccPl=ūs");
        Fourth.rules("n", "sh", "DatSi=ū NomPl=ua");
        Fourth.rules("us", "mf", "NomSi=us");
        Fourth.rules("u", "n", "NomSi=ū");

        Fifth.rules("sh", null, "NomSi=ēs AccSi=em AblSi=ē GenPl=ērum DatPl=ēbus");
        Fifth.rules("c", "sh", "GenSi=eī DatSi=eī");
        Fifth.rules("v", "sh", "GenSi=ēī DatSi=ēī");

    }
}



