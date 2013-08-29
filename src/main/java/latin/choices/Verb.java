
package latin.choices;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.tuple.Pair;

import latin.forms.English;
import latin.forms.FormBuilder;
import latin.forms.FormMap;
import latin.forms.FormRule;
import latin.forms.Formf;
import latin.forms.Forms;
import latin.forms.IForm;
import latin.forms.IFormBuilder;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.StoredForms;
import latin.forms.Suffix;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Verb {

    public interface Traits {
        public boolean vtest(String name, Object target);
        public boolean ptest(String name, boolean target);
    }

    public static interface System {
        public String getName();
    }

    public static abstract class FormSystem implements System {
        public static List<String> formSystemNames = Lists.newArrayList();
        public static Map<String, FormSystem> formSystemMap = Maps.newHashMap();
        public final String name;
        public FormSystem(String name) {
            this.name = name;
            formSystemNames.add(name);
            formSystemMap.put(name, this);
        }
        public String getName() {
            return name;
        }
        public abstract boolean test(IEntry e) ;
    }

    public static FormSystem getFormSystem(String name, boolean errorp) {
        FormSystem formSystem = FormSystem.formSystemMap.get(name);
        if (formSystem == null && errorp) {
            throw new IllegalArgumentException("Unknown form system " + name);
        }
        return formSystem;
    }

    public static <FS extends FormSystem> List<FS> getFormSystems(Class<FS> fsClass) {
        List<FS> fslist = Lists.newArrayList();
        for (String fsn : FormSystem.formSystemNames) {
            FormSystem fs = getFormSystem(fsn, true);
            if (fsClass.isInstance(fs)) {
                fslist.add(fsClass.cast(fs));
            }
        }
        return fslist;
    }

    public interface IEntry {
        public Formf getAstem();
        public Formf getStem(String stemName);
        public Conjugation getConjugation();
        public System getSystem(FormSystem formSystem);
    }

    public static enum StemName {
        Astem,
        Pstem,
        Jstem
    };

    public static class StoredStemf implements Stemf<IEntry> {
        public final String stemName;
        public StoredStemf(String stemName) {
            this.stemName = stemName;
        }
        public boolean test(IEntry t) {
            return t.getStem(stemName) != null;
        }
        public IFormBuilder apply(IEntry entry, Alts.Chooser chooser) {
            return Forms.applyFormf(entry.getStem(stemName), chooser);
        }
    }

    public static final StoredStemf Astem = new StoredStemf("astem");
    public static final StoredStemf Pstem = new StoredStemf("pstem");
    public static final StoredStemf Jstem = new StoredStemf("jstem");

    public static boolean haveAstemAndConj(IEntry t) {
        return t.getAstem() != null && t.getConjugation() != null;
    }

    public static abstract class ConjModStemf implements Stemf<IEntry> {
        public boolean test(IEntry t) {
            return haveAstemAndConj(t);
        }
        public abstract Rulef getMod(Conjugation conjugation);
        public IFormBuilder apply(IEntry entry, Alts.Chooser chooser) {
            Formf astemf = entry.getAstem();
            Conjugation conjugation = entry.getConjugation();
            if (astemf != null && conjugation != null) {
                return Forms.applyRule(getMod(conjugation), Forms.applyFormf(astemf, chooser), chooser);
            }
            else return null;
        }
    }

    public static final Stemf<IEntry> StrongStem = new ConjModStemf() {
        @Override
        public Rulef getMod(Conjugation conjugation) {
            return conjugation.strong;
        }
    };

    public static class ModStemf implements Stemf<IEntry> {
        public final Stemf<IEntry> prev;
        public final Rulef modRule;
        public ModStemf(Stemf<IEntry> prev, Rulef modRule) {
            this.prev = prev;
            this.modRule = modRule;
        }
        public ModStemf(Stemf<IEntry> prev, String name, String mst) {
            this(prev, FormRule.parseRule(name, "mod", mst));
        }
        @Override
        public boolean test(IEntry e) {
            return prev.test(e);
        }
        public IFormBuilder apply(IEntry e, Alts.Chooser chooser) {
            return Forms.applyRule(modRule, prev.apply(e, chooser), chooser);
        }
    }

    public static interface InfinitiveSystem extends System {
        public IForm getForm(IEntry entry, Alts.Chooser chooser);
    }

    public static class InfinitiveFormSystem extends FormSystem implements InfinitiveSystem {
        public final Stemf<IEntry> stemf;
        public InfinitiveFormSystem(String name, Stemf<IEntry> stemf) {
            super(name);
            this.stemf = stemf;
        }
        public boolean test(IEntry entry) {
            return stemf.test(entry);
        }
        public IForm getForm(IEntry entry, Alts.Chooser chooser) {
            return stemf.apply(entry, chooser);
        }
    }

    public static InfinitiveFormSystem ActInf = new InfinitiveFormSystem("ActInf",
            new ModStemf(
                    new ConjModStemf() {
                        @Override
                        public Rulef getMod(Conjugation conjugation) {
                            return conjugation.ai;
                        }
                    },
                    "ActInf",
                    "re"));

    public static InfinitiveFormSystem PasInf = new InfinitiveFormSystem("PasInf",
            new ModStemf(
                    new ConjModStemf() {
                        @Override
                        public Rulef getMod(Conjugation conjugation) {
                            return conjugation.pi;
                        }
                    },
                    "PasInf",
                    "ī"));


    public abstract static class StoredFormSystem implements System {
        public final Formf formf;
        public final FormSystem formSystem;
        public StoredFormSystem(FormSystem formSystem, String formName, List<String> sl) {
            this.formf = Suffix.makeFormf(formSystem.getName(), formName, sl);
            this.formSystem = formSystem;
        }
        public IFormBuilder applyFormf(Alts.Chooser chooser) {
            return Forms.applyFormf(formf, chooser);
        }
        public String getName() {
            return formSystem.getName();
        }
    }

    public static class StoredInfinitiveSystem extends StoredFormSystem implements InfinitiveSystem {
        public StoredInfinitiveSystem(InfinitiveFormSystem fs, List<String> sl) {
            super(fs, "form", sl);
        }
        public IForm getForm(IEntry entry, Alts.Chooser chooser) {
            return applyFormf(chooser);
        }
    }

    public interface PersonNumberSystem extends System {
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser);
        public Mood getMood();
        public VerbChoices getVerbChoices();
    }

    public interface ImperfectEndings {
        public IFormBuilder applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static abstract class PersonNumberForms extends FormSystem implements PersonNumberSystem {
        public final Mood mood;
        public final VerbChoices verbChoices;
        public PersonNumberForms(String name) {
            super(name);
            this.mood = Mood.valueOf(name.substring(0,3));
            this.verbChoices = VerbChoices.valueOf(name.substring(3,9));
        }
        public VerbChoices getVerbChoices() {
            return verbChoices;
        }
        public Mood getMood() {
            return mood;
        }
    }

    public static Rulef makeEndingRule(String id, PersonNumber pn, Voice v, String es) {
        return FormRule.makeRule(Joiner.on('.').join(id, pn, v), Suffix.csplit(es));
    }

    public static Pair<Rulef,Rulef> makeEndingPair(String id, PersonNumber pn, String ae, String pe) {
        return Pair.of(makeEndingRule(id, pn, Voice.Act, ae), makeEndingRule(id, pn, Voice.Pas, pe));
    }

    public static EnumMap<PersonNumber, Pair<Rulef,Rulef>> pendingsMap
            = new EnumMap<PersonNumber, Pair<Rulef,Rulef>>(PersonNumber.class);

    public static EnumMap<PersonNumber, Rulef> perfEndingsMap =
            new EnumMap<PersonNumber, Rulef>(PersonNumber.class);

    private static void putEndingPair(String ks, String ae, String pe) {
        PersonNumber pn = PersonNumber.valueOf(ks);
        pendingsMap.put(pn, makeEndingPair("pendingsMap", pn, ae, pe));
    }

    public static void putPerfEnding(String ks, String e) {
        PersonNumber pn = PersonNumber.valueOf(ks);
        perfEndingsMap.put(pn, FormRule.makeRule("PerfEndings", pn, Suffix.csplit(e)));
    }

    static {
        putEndingPair("SpSi", "s",   "ris");
        putEndingPair("TpSi", "t",   "tur");
        putEndingPair("FpPl", "mus", "mur");
        putEndingPair("SpPl", "tis", "minī");
        putEndingPair("TpPl", "nt",  "ntur");

        putPerfEnding("FpSi", "ī");
        putPerfEnding("SpSi", "istī");
        putPerfEnding("TpSi", "it");
        putPerfEnding("FpPl", "imus");
        putPerfEnding("SpPl", "istis");
        putPerfEnding("TpPl", "ērunt");
    }

    public static Pair<Rulef,Rulef> piFpSiPair =  makeEndingPair("pi",  PersonNumber.FpSi, "ō", "or");
    public static Pair<Rulef,Rulef> regFpSiPair = makeEndingPair("reg", PersonNumber.FpSi, "m", "r");

    public static Rulef endingRule(PersonNumber personNumber, Voice voice,
                                   Pair<Rulef,Rulef> fpsipair) {
        if (personNumber.equals(PersonNumber.FpSi)) {
            return voice.select(fpsipair);
        }
        else {
            return voice.select(pendingsMap.get(personNumber));
        }
    }
    public static IFormBuilder conjugationEnding(Conjugation conjugation,
                                                 PersonNumber personNumber,
                                                 Voice voice,
                                                 Pair<Rulef,Rulef> fpSiPair,
                                                 IFormBuilder formBuilder,
                                                 Alts.Chooser chooser) {
        if (formBuilder == null || conjugation == null) {
            return null;
        }
        Rulef erule = endingRule(personNumber, voice, fpSiPair);
        Preconditions.checkNotNull(erule);
        if (erule == null) {
            return null;
        }
        Rulef ipmod = conjugation.ipmod(personNumber, voice);
        if (ipmod != null) {
            formBuilder = Forms.applyRule(ipmod, formBuilder, chooser);
        }
        return Forms.applyRule(erule, formBuilder, chooser);
    }

    public static IFormBuilder indPresentForm(IEntry entry, PersonNumber personNumber, Voice voice, Alts.Chooser chooser) {
        Formf astemf = entry.getAstem();
        if (astemf != null) {
            Conjugation conjugation = entry.getConjugation();
            if (conjugation != null) {
                IFormBuilder formBuilder = Forms.applyFormf(astemf, chooser);
                return conjugationEnding(conjugation, personNumber, voice, piFpSiPair, formBuilder, chooser);
            }
        }
        return null;

    }

    public static PersonNumberForms IndPreAct = new PersonNumberForms("IndPreAct") {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            return indPresentForm(entry, personNumber, verbChoices.voice, chooser);
        }
    };

    public static PersonNumberForms IndPrePas = new PersonNumberForms("IndPrePas") {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            return indPresentForm(entry, personNumber, verbChoices.voice, chooser);
        }
    };

    public static ModStemf indFutStemf1 = new ModStemf(Astem, "IndFutForms1.stem", "bi");

    public static ImperfectEndings indFutEndings1 = new ImperfectEndings() {
        @Override
        public IFormBuilder applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return conjugationEnding(Conjugation.thirdc, personNumber, voice, piFpSiPair, formBuilder, chooser);
        }
    };

    public static Pair<Rulef,Rulef> IndFut2FpSiPair = makeEndingPair("indFut2", PersonNumber.FpSi, "am", "ar");

    public static ImperfectEndings indFutEndings2 = new ImperfectEndings() {
        @Override
        public IFormBuilder applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return conjugationEnding(Conjugation.first, personNumber, voice, IndFut2FpSiPair, formBuilder, chooser);
        }
    };

    public static IFormBuilder indFutureForm(IEntry entry, PersonNumber personNumber, Voice voice, Alts.Chooser chooser) {
        if (entry.getAstem() == null) {
            return null;
        }
        Conjugation conjugation = entry.getConjugation();
        if (conjugation == null) {
            return null;
        }
        if (conjugation.hasi()) {
            IFormBuilder formBuilder = Forms.applyStemf(StrongStem, entry, chooser);
            return indFutEndings2.applyEnding(personNumber, voice, formBuilder, chooser);
        }
        else {
            IFormBuilder formBuilder = Forms.applyStemf(indFutStemf1, entry, chooser);
            return indFutEndings1.applyEnding(personNumber, voice, formBuilder, chooser);
        }
    }

    public static PersonNumberForms IndFutAct = new PersonNumberForms("IndFutAct") {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            return indFutureForm(entry, personNumber, verbChoices.voice, chooser);
        }
    };

    public static PersonNumberForms IndFutPas = new PersonNumberForms("IndFutPas") {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            return indFutureForm(entry, personNumber, verbChoices.voice, chooser);
        }
    };

    public static ImperfectEndings regEndings = new ImperfectEndings() {
        @Override
        public IFormBuilder applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return conjugationEnding(Conjugation.second, personNumber, voice, regFpSiPair, formBuilder, chooser);
        }
    };

    public static class RegularPersonNumberForms extends PersonNumberForms {
        public final Stemf<IEntry> stemf;
        public RegularPersonNumberForms(String name, Stemf<IEntry> stemf) {
            super(name);
            this.stemf = stemf;
        }
        public boolean test (IEntry t) {
            return stemf.test(t);
        }
        public IForm applyEnding(IFormBuilder formBuilder, PersonNumber personNumber, Alts.Chooser chooser) {
            return regEndings.applyEnding(
                    personNumber,
                    verbChoices.voice,
                    formBuilder,
                    chooser);
        }
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            return applyEnding(Forms.applyStemf(stemf, entry, chooser), personNumber, chooser);
        }
    }

    public static ModStemf indImpStem = new ModStemf(StrongStem, "IndImp.stem", "bā");
    public static RegularPersonNumberForms IndImpAct = new RegularPersonNumberForms("IndImpAct", indImpStem);
    public static RegularPersonNumberForms IndImpPas = new RegularPersonNumberForms("IndImpPas", indImpStem);

    public static ConjModStemf subPreStem = new ConjModStemf() {
        @Override
        public Rulef getMod(Conjugation conjugation) {
            return conjugation.subj;
        }
    };

    public static RegularPersonNumberForms SubPreAct = new RegularPersonNumberForms("SubPreAct", subPreStem);
    public static RegularPersonNumberForms SubPrePas = new RegularPersonNumberForms("SubPrePas", subPreStem);

    public static final Stemf<IEntry> actInfStem = new Stemf<IEntry>() {
        @Override
        public boolean test(IEntry e) {
            return e.getSystem(ActInf) != null;
        }
        @Override
        public IFormBuilder apply(IEntry e, Alts.Chooser chooser) {
            InfinitiveSystem sys = (InfinitiveSystem) e.getSystem(ActInf);
            return (sys != null) ? new FormBuilder(sys.getForm(e, chooser)) : null;
        }
    };

    public static final ModStemf subImpStem = new ModStemf(actInfStem, "SubImp.stem", "<");

    public static RegularPersonNumberForms SubImpAct = new RegularPersonNumberForms("SubImpAct", subImpStem);
    public static RegularPersonNumberForms SubImpPas = new RegularPersonNumberForms("SubImpPas", subImpStem);

    public static final RegularPersonNumberForms IndPerAct = new RegularPersonNumberForms("IndPerAct", Pstem) {
        @Override
        public IForm applyEnding(IFormBuilder formBuilder, PersonNumber personNumber, Alts.Chooser chooser) {
            return Forms.applyRule(perfEndingsMap.get(personNumber), formBuilder, chooser);
        }
    };

    public static final RegularPersonNumberForms SubPerAct =
            new RegularPersonNumberForms("SubPerAct", new ModStemf(Pstem, "SubPer.stem", "erī"));

    public static final RegularPersonNumberForms IndPluAct =
            new RegularPersonNumberForms("IndPluAct", new ModStemf(Pstem, "IndPlu.stem", "erā"));

    public static final RegularPersonNumberForms SubPluAct =
            new RegularPersonNumberForms("SubPluAct", new ModStemf(Pstem, "SubPlu.stem", "issē"));

    public static final RegularPersonNumberForms IndFupAct =
            new RegularPersonNumberForms("IndFupAct", new ModStemf(Pstem, "IndFup.stem", "eri")) {
                @Override
                public IFormBuilder applyEnding(IFormBuilder iFormBuilder, PersonNumber personNumber, Alts.Chooser chooser) {
                    return conjugationEnding(Conjugation.first, personNumber, Voice.Act, piFpSiPair, iFormBuilder, chooser);
                }
            };

    public static final List<String> stemNames = Lists.newArrayList("astem", "pstem", "jstem");

    public static class FormsEntry implements IEntry {

        public final String id;
        public final Map<String, Formf> formfMap;
        public final Conjugation conjugation;
        public final Map<String, Verb.System> irregularSystems;
        public final Traits traits;
        public final English.TensedEntry english;

        public FormsEntry (String id, Map<String,Formf> formfMap, Conjugation conjugation, Traits traits, Map<String,System> irregularSystems,
                           English.TensedEntry english) {
            this.id = id;
            this.formfMap = formfMap;
            this.traits = traits;
            this.conjugation = conjugation;
            this.irregularSystems = irregularSystems;
            this.english = english;

        }

        public String toString() {
            if (conjugation == null) {
                return id;
            }
            else {
                return id + ":" + conjugation.toString();
            }
        }

        public Formf getStem(String name) {
            return formfMap.get(name);
        }

        public Formf getAstem() {
            return formfMap.get("astem");
        }

        public Conjugation getConjugation() {
            return conjugation;
        }

        public System getSystem(FormSystem formSystem) {
            String n = formSystem.name;
            if (irregularSystems.containsKey(n)) {
                return irregularSystems.get(n);
            }
            else if (formSystem.test(this)) {
                return formSystem;
            }
            else {
                return null;
            }
        }
    }

    public static final Traits emptyTraits = new Traits() {
        @Override
        public boolean vtest(String name, Object target) {
            return false;
        }
        @Override
        public boolean ptest(String name, boolean target) {
            return false;
        }
    };

    public static class TraitsMap extends HashMap<String,Object> implements Traits {
        public TraitsMap() {
            super();
        }
        public boolean vtest(String key, Object tvalue) {
            return Objects.equal(get(key), tvalue);
        }
        public boolean ptest(String key, boolean tvalue) {
            return (get(key)!=null)==tvalue;
        }
    }

    public static class EntryBuilder {
        String id;
        Map<String,Formf> formfMap;
        Conjugation conjugation;
        Map<String,System> irregularSystems;
        TraitsMap traitsMap;
        English.VerbForms english;
        public EntryBuilder(String id) {
            this.id = id;
            this.formfMap = Maps.newHashMap();
            this.conjugation = null;
            this.irregularSystems = Maps.newHashMap();
            this.traitsMap = new TraitsMap();
            this.english = null;
        }
        public EntryBuilder () {
            this(null);
        }

        public FormsEntry makeEntry() {
            return new FormsEntry(id, formfMap, conjugation, traitsMap, irregularSystems, english);
        }

        public EntryBuilder setId(String nid) {
            this.id = Suffix.unaccentString(nid);
            return this;
        }

        public Formf makeFormf(String name, List<String> sl) {
            return Suffix.makeFormf(id, name, sl);
        }

        public EntryBuilder storeFormf(String name, List<String> sl) {
            formfMap.put(name, makeFormf(name, sl));
            return this;
        }

        public EntryBuilder storeStem(String sn, String ss) {
            if (id == null && sn == "astem") {
                setId(sn);
            }
            return storeFormf(sn, Suffix.csplit(ss));
        }

        public EntryBuilder storeStems(List<String> sl) {
            int n = sl.size();
            if (n > 0) {
                storeStem("astem", sl.get(0));
            }
            if (n > 1) {
                storeStem("pstem", sl.get(1));
            }
            if (n > 2) {
                storeStem("jstem", sl.get(2));
            }
            return this;
        }

        public EntryBuilder storeStems(String ss) {
            return storeStems(Suffix.ssplit(ss));
        }

        public EntryBuilder setConjugation(Conjugation c) {
            this.conjugation = c;
            return this;
        }

        public EntryBuilder setConjugation(String cs) {
            return setConjugation(Conjugation.valueOf(cs));
        }

        public EntryBuilder setIrregular(System isys) {
            irregularSystems.put(isys.getName(), isys);
            return this;
        }

        public EntryBuilder makeStoredInfinitive(InfinitiveFormSystem fs, List<String> sl) {
            return setIrregular(new StoredInfinitiveSystem(fs, sl));
        }

        public EntryBuilder makeStoredInfinitive(InfinitiveFormSystem fs, String es) {
            return makeStoredInfinitive(fs, Suffix.csplit(es));
        }

        public EntryBuilder setEnglish(English.VerbForms e) {
            this.english = e;
            return this;
        }

        public EntryBuilder setEnglish(String es) {
            return setEnglish(English.verbForms(es));
        }

        public EntryBuilder makeStoredStem(RegularPersonNumberForms fs, List<String> sl) {
            return setIrregular(new StemPersonNumberSystem(fs, sl));
        }

        public EntryBuilder makeStoredStem(RegularPersonNumberForms fs, String ss) {
            return makeStoredStem(fs, Suffix.csplit(ss));
        }

        public EntryBuilder makeStoredForms(PersonNumberForms fs, StoredForms<PersonNumber> stored) {
            return setIrregular(new StoredPersonNumberSystem(fs, stored));
        }

        public EntryBuilder makeStoredForms(PersonNumberForms fs, List<String> sll) {
            FormMap<PersonNumber> formMap = new FormMap<PersonNumber>(PersonNumber.class);
            formMap.putForms(id + "." + fs.getName(), PersonNumber.values(), sll);
            return makeStoredForms(fs, formMap);
        }

        public EntryBuilder makeStoredForms(PersonNumberForms fs, String ss) {
            return makeStoredForms(fs, Suffix.ssplit(ss));
        }
    }

    public static abstract class IrregularPersonNumberSystem implements PersonNumberSystem {
        public final PersonNumberForms formSystem;
        public IrregularPersonNumberSystem(PersonNumberForms formSystem) {
            this.formSystem = formSystem;
        }
        public String getName() {
            return formSystem.getName();
        }
        public Mood getMood() {
            return formSystem.getMood();
        }
        public VerbChoices getVerbChoices() {
            return formSystem.getVerbChoices();
        }
    }

    public static class StoredPersonNumberSystem extends IrregularPersonNumberSystem {
            public final StoredForms<PersonNumber> stored;
        public StoredPersonNumberSystem(PersonNumberForms personNumberForms,
                                        StoredForms<PersonNumber> stored) {
            super(personNumberForms);
            this.stored = stored;
        }
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            return Forms.applyFormf(stored.getStored(personNumber), chooser);
        }

    }

    public static class StemPersonNumberSystem extends IrregularPersonNumberSystem {
        public final Formf stem;

        public StemPersonNumberSystem(RegularPersonNumberForms formSystem, List<String> sl) {
            super(formSystem);
            this.stem = Suffix.makeFormf(formSystem.getName(), "stem", sl);
        }
        @Override
        public IForm getForm(IEntry entry, PersonNumber personNumber, Alts.Chooser chooser) {
            RegularPersonNumberForms regs = (RegularPersonNumberForms) formSystem;
            return regs.applyEnding(Forms.applyFormf(stem, chooser), personNumber, chooser);
        }
    }
}