
package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import latin.forms.DocElement;
import latin.forms.Form;
import latin.forms.FormBuilder;
import latin.forms.FormMap;
import latin.forms.Formf;
import latin.forms.IFormBuilder;
import latin.forms.RuleMapBuilder;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.Suffix;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NounForms {

    public static class Erules implements Form.Rules<CaseNumber> {

        public final String name;
        private final EnumMap<CaseNumber,Rulef> enumMap;

        public Erules(String name,
                      EnumMap<CaseNumber,Rulef> enumMap) {
            this.name = name;
            this.enumMap = enumMap;
        }

        @Override
        public Rulef getRule(CaseNumber key) {
            return enumMap.get(key);
        }

        public boolean haveNomSiRule() {
            return enumMap.containsKey(CaseNumber.NomSi);
        }

        public String gsiEnding() {
           Rulef gsiRule = enumMap.get(CaseNumber.GenSi);
            if (gsiRule != null) {
                gsiRule = gsiRule.firstRule();
            }
            return gsiRule != null ? gsiRule.endingString() : null;
        }

        public List<String> makeGstems(List<String> gsistrings) {
            List<String> gstems = Lists.newArrayList();
            String estring = gsiEnding();
            if (estring != null && !gsistrings.isEmpty()) {
                String gsi = gsistrings.get(0);
                String gs = Suffix.removeEnding(gsi, estring);
                if (gs != null) {
                    gstems.add(gs);
                }
            }
            return gstems;
        }
    }

    private static Map<String, Erules> erulesMap = Maps.newHashMap();

    public static RuleMapBuilder<CaseNumber> makeMapBuilder(String name) {
        return new RuleMapBuilder<CaseNumber>(CaseNumber.class, name);
    }

    static class ErulesBuilder {
        public RuleMapBuilder<CaseNumber> mapBuilder;
        public ErulesBuilder(String name) {
            this.mapBuilder = makeMapBuilder(name);
        }
        public ErulesBuilder add(String ks, String afs) {
            mapBuilder.add(ks,afs);
            return this;
        }
        public ErulesBuilder add(Map<CaseNumber,Rulef> rmap) {
            mapBuilder.add(rmap);
            return this;
        }
        public ErulesBuilder add(String erulesName) {
            return add(erulesMap.get(erulesName).enumMap);
        }
        public Erules makeRules() {
            Erules erules = new Erules(mapBuilder.name, mapBuilder.enumMap);
            erulesMap.put(erules.name, erules);
            return erules;
        }
    }

    public static Erules getErules(String name, boolean errorp) {
        Erules erules = erulesMap.get(name);
        if (erules == null) {
            if (errorp) {
                throw new IllegalArgumentException("Unknown Erules " + name);
            }
        }
        return erules;
    }

    public static Erules getErules(String name) {
        return getErules(name, true);
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

    public static <ET> boolean getForm(CaseNumber key,
                                       Form.Stored<CaseNumber> storedForms,
                                       Stemf<ET> stemf,
                                       ET stemEntry,
                                       Form.Rules<CaseNumber> rules,
                                       IFormBuilder formBuilder,
                                       Alts.Chooser chooser) {
        if (storedForms != null) {
            Formf storedFormf = storedForms.getStored(key);
            if (storedFormf != null) {
                return storedFormf.apply(formBuilder, chooser);
            }
        }
        Rulef ruleFormf = rules.getRule(key);
        if (ruleFormf != null) {
            return stemf.apply(stemEntry, formBuilder, chooser) && ruleFormf.apply(formBuilder, chooser);
        }
        CaseNumber rkey = renamed.get(key);
        return rkey != null && getForm(rkey, storedForms, stemf, stemEntry, rules, formBuilder, chooser);
    }

    public static ErulesBuilder rulesBuilder(String name) {
        return new ErulesBuilder(name);
    }

    static {

        rulesBuilder("First.mf")
                .add("AccSi", "am")
                .add("GenSi", "ae")
                .add("DatSi", "ae")
                .add("AblSi", "ā")
                .add("LocSi", "ae")
                .add("NomPl", "ae")
                .add("AccPl", "ās")
                .add("GenPl", "ārum")
                .add("DatPl", "īs").makeRules();

        rulesBuilder("First.a")
                .add("First.mf")
                .add("NomSi", "a").makeRules();

        EnumMap<CaseNumber,Rulef> secondShared = makeMapBuilder("Second.shared")
                .add("GenSi", "ī")
                .add("DatSi", "ō")
                .add("AblSi", "ō")
                .add("LocSi", "ī")
                .add("GenPl", "ōrum")
                .add("DatPl", "īs").enumMap;

        rulesBuilder("Second.mf")
                .add(secondShared)
                .add("AccSi", "um")
                .add("NomPl", "ī")
                .add("AccPl", "ōs").makeRules();

        rulesBuilder("Second.n")
                .add(secondShared)
                .add("NomPl", "a").makeRules();

        rulesBuilder("Second.us")
                .add("Second.mf")
                .add("NomSi", "us")
                .add("VocSi", "e").makeRules();

        rulesBuilder("Second.um")
                .add("Second.n")
                .add("NomSi", "um").makeRules();

        EnumMap<CaseNumber,Rulef> secondiu = makeMapBuilder("Second.iu")
                .add("GenSi", "ī,-ī").enumMap;

        rulesBuilder("Second.ius")
                .add("Second.us")
                .add(secondiu)
                .add("VocSi", "<").makeRules();

        rulesBuilder("Second.ium")
                .add("Second.um")
                .add(secondiu).makeRules();

        rulesBuilder("Second.er")
                .add("Second.mf")
                .add("NomSi", "-er").makeRules();

        rulesBuilder("Second.r")
                .add("Second.mf")
                .add("NomSi", ":").makeRules();

        EnumMap<CaseNumber,Rulef> thirdShared = makeMapBuilder("Third.shared")
                .add("GenSi", "is")
                .add("DatSi", "ī")
                .add("AblSi", "e")
                .add("GenPl", "um")
                .add("DatPl", "ibus").enumMap;

        rulesBuilder("Third.c.mf")
                .add(thirdShared)
                .add("AccSi", "em")
                .add("NomPl", "ēs")
                .add("AccPl", "ēs").makeRules();

        rulesBuilder("Third.c.n")
                .add(thirdShared)
                .add("NomPl", "a").makeRules();

        EnumMap<CaseNumber,Rulef> thirdiShared = makeMapBuilder("Third.i.shared")
                .add("GenPl", "ium").enumMap;

        rulesBuilder("Third.i.mf")
                .add("Third.c.mf")
                .add(thirdiShared)
                .add("AccPl", "ēs,īs")
                .makeRules();

        rulesBuilder("Third.i.n")
                .add("Third.c.n")
                .add(thirdiShared)
                .add("NomPl", "ia").makeRules();

        EnumMap<CaseNumber,Rulef> thirdpiShared = makeMapBuilder("Third.pi.shared")
                .add(thirdiShared)
                .add("AblSi", "ī").enumMap;

        rulesBuilder("Third.pi.mf")
                .add("Third.i.mf")
                .add(thirdpiShared)
                .add("AccSi", "īm")
                .makeRules();

        rulesBuilder("Third.pi.n")
                .add("Third.i.n")
                .add(thirdpiShared).makeRules();

        rulesBuilder("Third.adj.mf")
                .add("Third.pi.mf")
                .add("AccSi", "em").makeRules();

        rulesBuilder("Third.adj.n")
                .add("Third.pi.n").makeRules();

        // amāns, amantis
        EnumMap<CaseNumber,Rulef> thirdpapShared = makeMapBuilder("Third.pap.shared")
                .add("NomSi", "--<ns").enumMap;

        rulesBuilder("Third.pap.mf")
                .add("Third.adj.mf")
                .add(thirdpapShared).makeRules();

        rulesBuilder("Third.pap.n")
                .add("Third.adj.n")
                .add(thirdpapShared).makeRules();

        EnumMap<CaseNumber,Rulef> fourthShared = makeMapBuilder("Fourth.shared")
                .add("GenSi", "ūs")
                .add("AblSi", "ū")
                .add("GenPl", "uum")
                .add("DatPl", "ibus").enumMap;

        rulesBuilder("Fourth.mf")
                .add(fourthShared)
                .add("AccSi", "um")
                .add("DatSi", "uī")
                .add("NomPl", "ūs")
                .add("AccPl", "ūs").makeRules();

        rulesBuilder("Fourth.n")
                .add(fourthShared)
                .add("DatSi", "ū")
                .add("NomPl", "ua").makeRules();

        rulesBuilder("Fourth.us")
                .add("Fourth.mf")
                .add("NomSi", "us").makeRules();

        rulesBuilder("Fourth.u")
                .add("Fourth.n")
                .add("NomSi", "ū").makeRules();

        EnumMap<CaseNumber,Rulef> fifthShared = makeMapBuilder("Fifth.shared")
                .add("NomSi", "ēs")
                .add("AccSi", "em")
                .add("NomPl", "ēs")
                .add("AblSi", "ē")
                .add("GenPl", "ērum")
                .add("DatPl", "ēbus").enumMap;

        rulesBuilder("Fifth.c")
                .add(fifthShared)
                .add("GenSi", "eī")
                .add("DatSi", "eī").makeRules();

        rulesBuilder("Fifth.v")
                .add(fifthShared)
                .add("GenSi", "ēī")
                .add("DatSi", "ēī").makeRules();

    }

    public static class FormEntry {

        public static final String gstemKey = "gstem";

        public final String id;
        public final Formf gstemf;
        public final Erules rules;
        public final Form.Stored<CaseNumber> stored;

        public FormEntry(String id, Formf gstemf, Erules rules, Form.Stored<CaseNumber> stored) {
            this.id = id;
            this.gstemf = gstemf;
            this.rules = rules;
            this.stored = stored;
        }

        public Formf getGstem() {
            return gstemf;
        }

        public boolean getForm(CaseNumber key, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return NounForms.getForm(key, stored, Gstem, this, rules, formBuilder, chooser);
        }

        public String getSpec() {
            List<String> sl = Lists.newArrayList();
            sl.add(id);
            if (rules != null) {
                sl.add(rules.name);
            }
            if (gstemf != null) {
                sl.add(gstemf.toString());
            }
            if (stored != null) {
                Formf nsif = stored.getStored(CaseNumber.NomSi);
                if (nsif != null) {
                    sl.add(nsif.toString());
                }
            }
            return sl.toString();
        }
    }

    public static class EntryBuilder {
        public final String id;
        Map<Object,Formf> formfMap;
        Set<CaseNumber> irregulars;
        Erules rules;
        public EntryBuilder(String id) {
            this.id = id;
            this.formfMap = Maps.newHashMap();
            this.irregulars = Sets.newHashSet();
            this.rules = null;
        }
        public FormEntry makeEntry () {
            if (rules == null || !rules.haveNomSiRule()) {
                irregulars.add(CaseNumber.NomSi);
            }
            Form.Stored<CaseNumber> stored = null;
            if (!irregulars.isEmpty()) {
                FormMap<CaseNumber> formMap = new FormMap<CaseNumber>(CaseNumber.class);
                for (CaseNumber key : irregulars) {
                    formMap.put(key, formfMap.get(key));
                }
                stored = formMap;
            }
            return new FormEntry(id, formfMap.get(FormEntry.gstemKey), rules, stored);
        }

        public EntryBuilder putFormf(Object key, List<String> sl) {
            formfMap.put(key, Suffix.makeFormf(id, key, sl));
            return this;
        }

        public EntryBuilder putGstem(List<String> sl) {
            return putFormf(FormEntry.gstemKey, sl);
        }

        public EntryBuilder putForm(CaseNumber key, List<String> sl) {
            return putFormf(key, sl);
        }

        public EntryBuilder putIrregular(CaseNumber key, List<String> sl) {
            irregulars.add(key);
            return putForm(key, sl);
        }

        public EntryBuilder putForm(String ks, String fs) {
            List<String> sl = Suffix.csplit(fs);
            if (ks.equalsIgnoreCase(FormEntry.gstemKey)) {
                return putGstem(sl);
            }
            else {
                return putForm(CaseNumber.valueOf(ks), sl);
            }
        }

        public EntryBuilder storeRules(Erules erules) {
            this.rules = erules;
            return this;
        }

        public EntryBuilder storeRules(String rs) {
            return storeRules(getErules(rs));
        }

        public EntryBuilder parseXml(DocElement docElement) {
            String rulesName = docElement.getAttribute("rules");
            if (!rulesName.isEmpty()) {
                storeRules(rulesName);
            }
            List<String> egstems = docElement.getStrings("forms", "gstem");
            if (!egstems.isEmpty()) {
                putGstem(egstems);
            }
            List<String> gsistrings = docElement.getStrings("forms", CaseNumber.GenSi);
            if (!gsistrings.isEmpty()) {
                List<String> mgstems = rules.makeGstems(gsistrings);
                if (egstems.isEmpty()) {
                    putGstem(mgstems);
                }
                else {
                    Preconditions.checkState(egstems.equals(mgstems));
                }
            }
            for (CaseNumber key : CaseNumber.values()) {
                List<String> kstrings = docElement.getStrings("forms", key);
                if (!kstrings.isEmpty()) {
                    putForm(key, kstrings);
                }
            }
            return this;
        }

        public FormMap<CaseNumber> getFormMap() {
            FormMap<CaseNumber> formMap = new FormMap<CaseNumber>(CaseNumber.class);
            for (CaseNumber key : CaseNumber.values()) {
                Formf formf = formfMap.get(key);
                if (formf != null) {
                    formMap.put(key, formf);
                }
            }
            return formMap;
        }
    }

    public static Stemf<FormEntry> Gstem = new Stemf<FormEntry>() {
        @Override
        public boolean test(FormEntry e) {
            return e.getGstem() != null;
        }
        @Override
        public boolean apply(FormEntry e, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return e.getGstem().apply(formBuilder, chooser);
        }
    };

    public static List<String> getForms(NounForms.FormEntry entry, CaseNumber key) {
        List<String> formList = Lists.newArrayList();
        CollectAlts collectAlts = new CollectAlts();
        do {
            FormBuilder formBuilder = new FormBuilder();
            if (entry.getForm(key, formBuilder, collectAlts)) {
                formList.add(formBuilder.getForm());
            }
        }
        while(collectAlts.incrementPositions());
        return formList;
    }

    public static Collection<Erules> getAllRules() {
        return erulesMap.values();
    }

}



