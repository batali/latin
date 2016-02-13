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

    public static Rules getRules(String rs) {
        int p = rs.indexOf('.');
        Preconditions.checkState(p > 0);
        Declension declension = EkeyHelper.ekeyFromString(Declension.class, rs.substring(0, p),
                                                          false);
        Preconditions.checkNotNull(declension, "Unknown rules (declension) " + rs);
        Rules rules = declension.rulesMap.get(rs.substring(p+1));
        Preconditions.checkNotNull(rules, "Unknown rules (subname) " + rs);
        return rules;
    }

    public boolean gsiMatch(String s) {
        return Suffix.selectEndMatcher(s, gsiEndings) != null;
    }

    public String makeGstem(String gsi) {
        String ms = Suffix.selectEndMatcher(gsi, gsiEndings);
        return (ms == null) ? null : Suffix.butLast(gsi, ms.length());
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
        for (String ss : strings) {
            for (String es : Splitters.ssplitter(ss)) {
                Splitters.esplit(es, consumer);
            }
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
                     "GenSi=ī,-ī");
        Second.rules("ius", "use=us,iu",
                     "VocSi=-ī");
        Second.rules("ium", "use=um,iu");
        Second.rules("r", "use=mf",
                     "NomSi=-er");
        Second.rules("er", "use=mf",
                     "NomSi=:");

        Third.rules("sh",
                    "GenSi=is DatSi=ī AblSi=e GenPl=um DatPl=ibus");
        Third.rules("c.mf", "use=sh",
                    "AccSi=em NomPl=ēs AccPl=ēs");
        Third.rules("c.n", "use=sh",
                    "NomPl=a");
        Third.rules("ium",
                    "GenPl=ium");
        Third.rules("i",
                    "AblSi=ī");
        Third.rules("i.n", "use=sh,ium,i",
                    "NomPl=ia");
        Third.rules("i.mf", "use=c.mf,ium");
        Third.rules("a.mf", "use=i.mf,i");
        Third.rules("pi.mf", "use=a.mf",
                    "AccSi=im AccPl=īm,ēm");

        Fourth.rules("sh", "GenSi=ūs AblSi=ū GenPl=uum DatPl=ibus");
        Fourth.rules("mf", "use=sh",
                     "DatSi=uī,ū AccSi=um NomPl=ūs AccPl=ūs");
        Fourth.rules("n", "use=sh",
                     "DatSi=ū NomPl=ua");
        Fourth.rules("us", "use=mf",
                     "NomSi=us");
        Fourth.rules("ū", "use=n",
                     "NomSi=ū");

        Fifth.rules("sh", "NomSi=ēs AccSi=em AblSi=ē",
                    "NomPl=ēs AccPl=ēs GenPl=ērum DatPl=ēbus");
        Fifth.rules("c", "use=sh",
                    "GenSi=eī DatSi=eī");
        Fifth.rules("v", "use=sh",
                    "GenSi=ēī DatSi=ēī");

    }
}



