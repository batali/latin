
package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import latin.forms.DocElement;
import latin.forms.FormMap;
import latin.forms.FormRule;
import latin.forms.Formf;
import latin.forms.Forms;
import latin.forms.IForm;
import latin.forms.IFormBuilder;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.Suffix;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NounForms {

    public static class Erule implements Rulef {
        public final String name;
        public final ImmutableSet<String> features;
        public final ImmutableList<FormRule> rules;

        public Erule (String name,
                      ImmutableSet<String> features,
                      ImmutableList<FormRule> rules) {
            this.name = name;
            this.features = features;
            this.rules = rules;
        }

        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            return Alts.chooseElement(rules, this, chooser).apply(formBuilder, chooser);
        }

        public String toString() {
            return name + ":" + rules.toString();
        }
    }

    public static class Erules implements Noun.Rules {

        public final String name;
        private final EnumMap<CaseNumber,Erule> enumMap;

        public Erules(String name,
                      EnumMap<CaseNumber,Erule> enumMap) {
            this.name = name;
            this.enumMap = enumMap;
        }

        @Override
        public Erule getRule(CaseNumber key) {
            return enumMap.get(key);
        }

        public boolean haveNomSiRule() {
            return enumMap.containsKey(CaseNumber.NomSi);
        }

        public String gsiEnding() {
            Erule gsiRule = enumMap.get(CaseNumber.GenSi);
            if (gsiRule != null) {
                FormRule fr = gsiRule.rules.get(0);
                return fr.endingString();
            }
            else {
                return null;
            }
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

    public static Iterable<String> splitFeatures(String fs) {
        return Splitter.on('.').omitEmptyStrings().split(fs);
    }

    public static class ErulesEntriesMap extends EnumMap<CaseNumber, List<Erule>> {

        private String id;
        public ErulesEntriesMap(String id) {
            super(CaseNumber.class);
            this.id = id;
        }

        public ErulesEntriesMap add(String ks, String es) {
            CaseNumber key = null;
            ImmutableSet<String> features = null;
            int p = ks.indexOf('.');
            if (p > 0) {
                key = CaseNumber.valueOf(ks.substring(0,p));
                features = ImmutableSet.copyOf(splitFeatures(ks.substring(p+1,ks.length())));
            }
            else {
                key = CaseNumber.valueOf(ks);
                features = ImmutableSet.of();
            }
            ImmutableList<FormRule> rules =
                    ImmutableList.copyOf(Suffix.csplitter(es, FormRule.toFormRule));
            putEntry(key, new Erule(ks, features, rules));
            return this;
        }

        public ErulesEntriesMap putEntry(CaseNumber key, Erule erule) {
            List<Erule> erules = get(key);
            if (erules == null) {
                erules = Lists.newArrayList();
                put(key, erules);
            }
            erules.add(erule);
            return this;
        }

        public Erule selectRule(CaseNumber key, Set<String> targetSet) {
            List<Erule> matchers = Lists.newArrayList();
            for (Erule nrule : get(key)) {
                Set<String> nset = nrule.features;
                if (targetSet.containsAll(nset)) {
                    boolean addp = true;
                    List<Erule> removed = Lists.newArrayList();
                    for (Erule mrule : matchers) {
                        Set<String> mset = mrule.features;
                        if (mset.containsAll(nset)) {
                            addp = false;
                            break;
                        }
                        if (nset.containsAll(mset)) {
                            removed.add(mrule);
                        }
                    }
                    if (addp) {
                        matchers.removeAll(removed);
                        matchers.add(nrule);
                    }
                }
            }
            if (matchers.isEmpty()) {
                return null;
            }
            else {
                Preconditions.checkState(matchers.size()==1);
                return matchers.get(0);
            }
        }

        public EnumMap<CaseNumber,Erule> selectRules(Set<String> tfeatures) {
            EnumMap<CaseNumber,Erule> rmap = new EnumMap<CaseNumber,Erule>(CaseNumber.class);
            for (Map.Entry<CaseNumber,List<Erule>> e : entrySet()) {
                CaseNumber key = e.getKey();
                Erule rule = selectRule(key, tfeatures);
                if (rule != null) {
                    rmap.put(key, rule);
                }
            }
            return rmap;
        }

        public EnumMap<CaseNumber,Erule> selectRules(String tfs) {
            return selectRules(Sets.newHashSet(splitFeatures(tfs)));
        }

        public ErulesEntriesMap makeRules(String name, String tfs) {
            Erules erules = new Erules(name, selectRules(tfs));
            erulesMap.put(erules.name, erules);
            return this;
        }

    }

    public static class ErulesEntries {

        private String id;
        private ListMultimap<CaseNumber,Erule> listMultimap;
        public ErulesEntries(String id) {
            this.id = id;
            this.listMultimap = ArrayListMultimap.create();
        }

        public ErulesEntries add(String ks, String es) {
            int p = ks.indexOf('.');
            CaseNumber key = null;
            ImmutableSet<String> features = ImmutableSet.of();
            if (p > 0) {
                key = CaseNumber.valueOf(ks.substring(0,p));
                features = ImmutableSet.copyOf(splitFeatures(ks.substring(p + 1, ks.length())));
            }
            else {
                key = CaseNumber.valueOf(ks);
            }
            ImmutableList<FormRule> rules =
                    ImmutableList.copyOf(Suffix.csplitter(es, FormRule.toFormRule));
            listMultimap.put(key, new Erule(ks, ImmutableSet.copyOf(features), rules));
            return this;
        }

        public Erule selectRule(CaseNumber key, Set<String> targetSet) {
            List<Erule> matchers = Lists.newArrayList();
            for (Erule nrule : listMultimap.get(key)) {
                Set<String> nset = nrule.features;
                if (targetSet.containsAll(nset)) {
                    boolean addp = true;
                    List<Erule> removed = Lists.newArrayList();
                    for (Erule mrule : matchers) {
                        Set<String> mset = mrule.features;
                        if (mset.containsAll(nset)) {
                            addp = false;
                            break;
                        }
                        if (nset.containsAll(mset)) {
                            removed.add(mrule);
                        }
                    }
                    if (addp) {
                        matchers.removeAll(removed);
                        matchers.add(nrule);
                    }
                }
            }
            if (matchers.isEmpty()) {
                return null;
            }
            else {
                Preconditions.checkState(matchers.size()==1);
                return matchers.get(0);
            }
        }

        public EnumMap<CaseNumber,Erule> selectRules(Set<String> tfeatures) {
            EnumMap<CaseNumber,Erule> rmap = new EnumMap<CaseNumber,Erule>(CaseNumber.class);
            for (CaseNumber key : listMultimap.keys()) {
                Erule rule = selectRule(key, tfeatures);
                if (rule != null) {
                    rmap.put(key, rule);
                }
            }
            return rmap;
        }

        public EnumMap<CaseNumber,Erule> selectRules(String tfs) {
            return selectRules(Sets.newHashSet(splitFeatures(tfs)));
        }

        public ErulesEntries makeRules(String name, String tfs) {
            Erules erules = new Erules(name, selectRules(tfs));
            erulesMap.put(erules.name, erules);
            return this;
        }

    }


    static {

        new ErulesEntries("First")
                .add("NomSi.a", "a")
                .add("AccSi", "am")
                .add("GenSi", "ae")
                .add("DatSi", "ae")
                .add("AblSi", "ā")
                .add("LocSi", "ae")
                .add("NomPl", "ae")
                .add("AccPl", "ās")
                .add("GenPl", "ārum")
                .add("DatPl", "īs")
                .makeRules("First.mf", "")
                .makeRules("First.a", "a");

        new ErulesEntries("Second")
                .add("NomSi.us", "us")
                .add("NomSi.um", "um")
                .add("NomSi.er", "-er")
                .add("NomSi.r", ":")
                .add("VocSi.us", "e")
                .add("VocSi.iu.us", "-ī")
                .add("GenSi", "ī")
                .add("GenSi.iu", "ī,-ī")
                .add("DatSi", "ō")
                .add("AblSi", "ō")
                .add("LocSi", "ī")
                .add("GenPl", "ōrum")
                .add("DatPl", "īs")
                .add("AccSi.mf", "um")
                .add("NomPl.mf", "ī")
                .add("NomPl.n", "a")
                .add("AccPl.mf", "ōs")
                .makeRules("Second.mf", "mf")
                .makeRules("Second.n", "n")
                .makeRules("Second.us", "mf.us")
                .makeRules("Second.ius", "mf.iu.us")
                .makeRules("Second.um", "n.um")
                .makeRules("Second.ium", "n.iu.um")
                .makeRules("Second.er", "mf.er")
                .makeRules("Second.r", "mf.r");

        new ErulesEntries("Third")
                .add("NomSi.pap", "--<ns")
                .add("AccSi.mf", "em")
                .add("AccSi.mf.pi", "īm")
                .add("GenSi", "is")
                .add("DatSi", "ī")
                .add("AblSi", "e")
                .add("AblSi.pi", "ī")
                .add("NomPl.mf", "ēs")
                .add("NomPl.n", "a")
                .add("NomPl.n.i", "ia")
                .add("AccPl.mf", "ēs")
                .add("AccPl.mf.i", "ēs,īs")
                .add("GenPl", "um")
                .add("GenPl.i", "ium")
                .add("DatPl", "ibus")
                .makeRules("Third.c.mf", "mf")
                .makeRules("Third.c.n", "n")
                .makeRules("Third.i.mf", "mf.i")
                .makeRules("Third.i.n", "n.i")
                .makeRules("Third.pi.mf", "mf.i.pi")
                .makeRules("Third.pi.n", "n.i.pi")
                .makeRules("Third.adj.mf", "mf.i")
                .makeRules("Third.adj.n", "n.i");

        new ErulesEntries("Fourth")
                .add("NomSi.us", "us")
                .add("NomSi.u", "ū")
                .add("GenSi", "ūs")
                .add("AblSi", "ū")
                .add("GenPl", "uum")
                .add("DatPl", "ibus")
                .add("AccSi.mf", "um")
                .add("DatSi.mf", "uī")
                .add("NomPl.mf", "ūs")
                .add("AccPl.mf", "ūs")
                .add("DatSi.n", "ū")
                .add("NomPl.n", "ua")
                .makeRules("Fourth.mf", "mf")
                .makeRules("Fourth.n", "n")
                .makeRules("Fourth.us", "mf.us")
                .makeRules("Fourth.u", "n.u");

        new ErulesEntries("Fifth")
                .add("NomSi", "ēs")
                .add("AccSi", "em")
                .add("NomPl", "ēs")
                .add("AblSi", "ē")
                .add("GenPl", "ērum")
                .add("DatPl", "ēbus")
                .add("GenSi.c", "eī")
                .add("DatSi.c", "eī")
                .add("GenSi.v", "ēī")
                .add("DatSi.v", "ēī")
                .makeRules("Fifth.c", "c")
                .makeRules("Fifth.v", "v");

    }

    public static class FormEntry {

        public static final String gstemKey = "gstem";

        public final String id;
        public final Formf gstemf;
        public final Erules rules;
        public final FormMap<CaseNumber> stored;

        public FormEntry(String id, Formf gstemf, Erules rules, FormMap<CaseNumber> stored) {
            this.id = id;
            this.gstemf = gstemf;
            this.rules = rules;
            this.stored = stored;
        }

        public Formf getGstem() {
            return gstemf;
        }

        public IForm getForm(CaseNumber key, Alts.Chooser chooser) {
            return Noun.getForm(key, stored, Gstem, this, rules, chooser);
        }

        public String getId() {
            return id;
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
            FormMap<CaseNumber> stored = null;
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
        public IFormBuilder apply(FormEntry e, Alts.Chooser chooser) {
            return Forms.applyFormf(e.getGstem(), chooser);
        }
    };

    public static List<String> getForms(NounForms.FormEntry entry, CaseNumber key) {
        List<String> formList = Lists.newArrayList();
        CollectAlts collectAlts = new CollectAlts();
        do {
            Object fs = entry.getForm(key, collectAlts);
            if (fs != null) {
                formList.add(fs.toString());
            }
        }
        while(collectAlts.incrementPositions());
        return formList;
    }

    public static Collection<Erules> getAllRules() {
        return erulesMap.values();
    }

}



