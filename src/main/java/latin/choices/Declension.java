package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import latin.util.PathId;
import latin.util.Splitters;

import java.util.Map;
import java.util.function.BiConsumer;

public enum Declension {

    First("ae"),
    Second("ī"),
    Third("is"),
    Fourth("ūs"),
    Fifth("eī,ēī");

    final PathId path;
    final ImmutableList<String> gsiEndings;
    Map<String, KeyRules<CaseNumber>> rulesMap;

    Declension(String gst) {
        this.gsiEndings = Splitters.csplit(gst);
        this.path = PathId.makeRoot(this);
        this.rulesMap = Maps.newHashMap();
    }

    public static Declension getDeclension(Object d) {
        if (d instanceof Declension) {
            return (Declension) d;
        }
        else {
            return Declension.valueOf(d.toString());
        }
    }

    public KeyRules<CaseNumber> getRules(String subname, boolean errorp) {
        KeyRules<CaseNumber> keyRules = rulesMap.get(subname);
        if (errorp) {
            Preconditions.checkNotNull(keyRules, "Unknown rules " + toString());
        }
        return keyRules;
    }

    void rules(String subname, String... strings) {
        final PathId rulesId = path.makeChild(subname);
        final KeyRulesMap<CaseNumber> keyRulesMap = new KeyRulesMap<>(CaseNumber.class, rulesId);
        BiConsumer<String, String> consumer = new BiConsumer<String, String>() {
            @Override
            public void accept(String ks, String vs) {
                if (ks.equals("use")) {
                    for (String sn : Splitters.csplitter(vs)) {
                        KeyRules<CaseNumber> keyRules = rulesMap.get(sn);
                        keyRulesMap.putAll(keyRules);
                    }
                } else {
                    CaseNumber key = CaseNumber.fromString(ks);
                    keyRulesMap.addRule(key, vs);
                }
            }
        };
        for (String string : strings) {
            Splitters.essplit(string, consumer);
        }
        rulesMap.put(subname, keyRulesMap);
    }

    static {
        First.rules("mf",
                    "AccSi=am GenSi=ae DatSi=ae AblSi=ā LocSi=ae",
                    "NomPl=ae AccPl=ās GenPl=ārum DatPl=īs");
        First.rules("a", "use=mf",
                    "NomSi=a");

        Second.rules("sh",
                     "GenSi=ī DatSi=ō AblSi=ō LocSi=ī GenPl=ōrum DatPl=īs");
        Second.rules("mf", "use=sh",
                     "AccSi=um NomPl=ī AccPl=ōs");
        Second.rules("n", "use=sh",
                     "NomPl=a");
        Second.rules("us", "use=mf",
                     "NomSi=us VocSi=e");
        Second.rules("um", "use=n",
                     "NomSi=um");
        Second.rules("iu",
                     "GenSi=ī,<");
        Second.rules("ius", "use=us,iu",
                     "VocSi=<");
        Second.rules("ium", "use=um,iu");
        Second.rules("r", "use=mf",
                     "NomSi=-er");
        Second.rules("er", "use=mf",
                     "NomSi=:");

        Third.rules("sh",
                    "GenSi=is DatSi=ī AblSi=e GenPl=um DatPl=ibus");
        Third.rules("mf", "use=sh",
                    "AccSi=em NomPl=ēs AccPl=ēs");
        Third.rules("n", "use=sh",
                    "NomPl=a");
        Third.rules("ium",
                    "GenPl=ium");
        Third.rules("i",
                    "AblSi=ī");
        Third.rules("ie",
                    "AblSi=ī,e");
        Third.rules("n.i", "use=sh,ium,i",
                    "NomPl=ia");
        Third.rules("mf.mi", "use=mf,ium");
        Third.rules("mf.pi", "use=mf.mi,i");

        Fourth.rules("sh", "GenSi=ūs AblSi=ū GenPl=uum DatPl=ibus");
        Fourth.rules("mf", "use=sh",
                     "DatSi=uī,ū AccSi=um NomPl=ūs AccPl=ūs");
        Fourth.rules("n", "use=sh",
                     "DatSi=ū NomPl=ua");
        Fourth.rules("us", "use=mf",
                     "NomSi=us");
        Fourth.rules("u", "use=n",
                     "NomSi=ū");

        Fifth.rules("sh", "NomSi=ēs AccSi=em AblSi=ē GenPl=ērum DatPl=ēbus");
        Fifth.rules("c", "use=sh",
                    "GenSi=eī DatSi=eī");
        Fifth.rules("v", "use=sh",
                    "GenSi=ēī DatSi=ēī");

    }
}



