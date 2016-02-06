package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import latin.forms.ModRule;
import latin.forms.Suffix;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum Declension {

    First("ae"),
    Second("ī"),
    Third("is"),
    Fourth("ūs"),
    Fifth("eī,ēī");

    static class Rules extends EnumMap<CaseNumber, ModRule> {
        public final PathId pathId;
        public Rules(Declension declension, String subname) {
            super(CaseNumber.class);
            this.pathId = PathId.makePath(declension, subname);
        }
        public Declension getDeclension() {
            return (Declension) pathId.getParent().name;
        }
        public String getSubname() {
            return pathId.name.toString();
        }
        void addRule(String ks, String vs) {
            CaseNumber key = CaseNumber.fromString(ks);
            put(key, new ModRule(pathId.makeChild(key), vs));
        }
    }

    final ImmutableList<String> gsiEndings;
    Map<String, Rules> rulesMap;

    Declension(String gst) {
        this.gsiEndings = Splitters.csplit(gst);
        this.rulesMap = Maps.newHashMap();
    }

    public static Declension getDeclension(Object d) {
        if (d instanceof Declension) {
            return (Declension) d;
        }
        else {
            return EkeyHelper.ekeyFromString(Declension.class, d.toString());
        }
    }

    public Rules getRules(String subname, boolean errorp) {
        Rules rules = rulesMap.get(subname);
        if (errorp) {
            Preconditions.checkNotNull(rules, "Unknown rules " + toString());
        }
        return rules;
    }

    public Rules getRules(String subname) {
        return getRules(subname, true);
    }

    public boolean gsiMatch(String s) {
        return Suffix.selectEndMatcher(s, gsiEndings) != null;
    }

    public String makeGstem(String gsi) {
        String ms = Suffix.selectEndMatcher(gsi, gsiEndings);
        return (ms == null) ? null : Suffix.butlast(gsi, ms.length());
    }

    void rules(String subname, String... strings) {
        final Rules newRules = new Rules(this, subname);
        BiConsumer<String, String> consumer = new BiConsumer<String, String>() {
            @Override
            public void accept(String ks, String vs) {
                if (ks.equals("use")) {
                    for (String sn : Splitters.csplitter(vs)) {
                        Rules gotRules = rulesMap.get(sn);
                        Preconditions.checkNotNull(gotRules);
                        newRules.putAll(gotRules);
                    }
                } else {
                    newRules.addRule(ks, vs);
                }
            }
        };
        for (String string : strings) {
            Splitters.essplit(string, consumer);
        }
        rulesMap.put(subname, newRules);
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
        Third.rules("mf.i", "use=mf,ium");
        Third.rules("mf.pi", "use=mf.i,i");

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



