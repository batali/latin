
package latin.choices;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import latin.forms.Form;
import latin.forms.FormMap;
import latin.forms.FormRule;
import latin.forms.Formf;
import latin.forms.IFormBuilder;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.Suffix;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Verb {

    public interface Traits {
        public boolean vtest(String name, Object target);
        public boolean ptest(String name, boolean target);
    }

    public interface IEntry {
        public Formf getAstem();
        public Formf getStem(String stemName);
        public Conjugation getConjugation();
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
        public boolean apply(IEntry entry, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf formf = entry.getStem(stemName);
            return formf != null && formf.apply(formBuilder, chooser);
        }
    }

    public static final StoredStemf Astem = new StoredStemf("astem");

    public static boolean haveAstemAndConj(IEntry t) {
        return t.getAstem() != null && t.getConjugation() != null;
    }

    public static abstract class ConjModStemf implements Stemf<IEntry> {
        public boolean test(IEntry t) {
            return haveAstemAndConj(t);
        }
        public abstract Rulef getMod(Conjugation conjugation);
        public boolean apply(IEntry entry, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf astemf = entry.getAstem();
            Conjugation conjugation = (astemf != null) ? entry.getConjugation() : null;
            Rulef rule = (conjugation != null) ? getMod(conjugation) : null;
            return (rule != null) && astemf.apply(formBuilder, chooser) && rule.apply(formBuilder, chooser);
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
        public final Rulef mod;
        public ModStemf(Stemf<IEntry> prev, Rulef mod) {
            this.prev = prev;
            this.mod = mod;
        }
        public ModStemf(Stemf<IEntry> prev, String name, String mst) {
            this(prev, FormRule.parseRule(name, "mod", mst));
        }
        @Override
        public boolean test(IEntry e) {
            return prev.test(e);
        }
        @Override
        public boolean apply(IEntry e, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return prev.apply(e, formBuilder, chooser) && mod.apply(formBuilder, chooser);
        }
    }

    public static interface System {
        public String getName();
        public boolean test(IEntry e);
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

    public static interface InfinitiveSystem extends System {
        public boolean getForm(IEntry entry, IFormBuilder formBuilder, Alts.Chooser chooser);
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
        public boolean getForm(IEntry entry, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return stemf.apply(entry, formBuilder, chooser);
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

    public static class StoredForm implements Stemf<IEntry>, InfinitiveSystem {
        public final Formf formf;
        public final String name;
        public StoredForm(String prefix, String name, List<String> sl) {
            this.formf = Suffix.makeFormf(prefix, name, sl);
            this.name = name;
        }
        @Override
        public boolean getForm(IEntry entry, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return formf.apply(formBuilder, chooser);
        }

        @Override
        public boolean test(IEntry e) {
            return true;
        }

        @Override
        public boolean apply(IEntry e, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return formf.apply(formBuilder, chooser);
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PersonNumberSystemKey {

        IndPreAct (Mood.Ind, Time.Pre, Completeness.In, Voice.Act),
        IndPrePas (Mood.Ind, Time.Pre, Completeness.In, Voice.Pas),
        IndImpAct (Mood.Ind, Time.Pas, Completeness.In, Voice.Act),
        IndImpPas (Mood.Ind, Time.Pas, Completeness.In, Voice.Pas),
        IndFutAct (Mood.Ind, Time.Fut, Completeness.In, Voice.Act),
        IndFutPas (Mood.Ind, Time.Fut, Completeness.In, Voice.Pas),
        SubPreAct (Mood.Sub, Time.Pre, Completeness.In, Voice.Act),
        SubPrePas (Mood.Sub, Time.Pre, Completeness.In, Voice.Pas),
        SubImpAct (Mood.Sub, Time.Pas, Completeness.In, Voice.Act),
        SubImpPas (Mood.Sub, Time.Pas, Completeness.In, Voice.Pas),

        IndPerAct (Mood.Ind, Time.Pre, Completeness.Cm, Voice.Act),
        IndPerPas (Mood.Ind, Time.Pre, Completeness.Cm, Voice.Pas),
        IndPluAct (Mood.Ind, Time.Pas, Completeness.Cm, Voice.Act),
        IndPluPas (Mood.Ind, Time.Pas, Completeness.Cm, Voice.Pas),
        IndFupAct (Mood.Ind, Time.Fut, Completeness.Cm, Voice.Act),
        IndFupPas (Mood.Ind, Time.Fut, Completeness.Cm, Voice.Pas),
        SubPerAct (Mood.Sub, Time.Pre, Completeness.Cm, Voice.Act),
        SubPerPas (Mood.Sub, Time.Pre, Completeness.Cm, Voice.Pas),
        SubPluAct (Mood.Sub, Time.Pas, Completeness.Cm, Voice.Act),
        SubPluPas (Mood.Sub, Time.Pas, Completeness.Cm, Voice.Pas);

        public final Mood mood;
        public final Time time;
        public final Completeness completeness;
        public final Voice voice;

        PersonNumberSystemKey(Mood m, Time t, Completeness c, Voice v) {
            this.mood = m;
            this.time = t;
            this.completeness = c;
            this.voice = v;
        }

    }

    public interface PersonNumberSystem extends System {
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public interface ImperfectEndings {
        public boolean applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static abstract class PersonNumberForms extends FormSystem implements PersonNumberSystem {
        public final Voice voice;
        public final Completeness completeness;
        public PersonNumberForms(String name, Completeness completeness, Voice voice) {
            super(name);
            this.completeness = completeness;
            this.voice = voice;
        }
    }

    public static boolean conjugationEndings(Conjugation conjugation,
                                             PersonNumber personNumber,
                                             Voice voice,
                                             Pair<Rulef,Rulef> fpSiPair,
                                             IFormBuilder formBuilder,
                                             Alts.Chooser chooser) {
        Rulef erule = Conjugation.endingRule(personNumber, voice, fpSiPair);
        Preconditions.checkNotNull(erule);
        if (erule == null) {
            return false;
        }
        Rulef ipmod = conjugation.ipmod(personNumber, voice);
        return ((ipmod == null) || ipmod.apply(formBuilder, chooser)) && erule.apply(formBuilder, chooser);
    }

    public static boolean indPresentForm(IEntry entry, PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
        Formf astemf = entry.getStem("astem");
        if (astemf != null) {
            Conjugation conjugation = entry.getConjugation();
            return (conjugation != null && astemf.apply(formBuilder, chooser) &&
                    conjugationEndings(conjugation, personNumber, voice, Conjugation.piFpSiPair, formBuilder, chooser));
        }
        return false;
    }

    public static PersonNumberForms IndPreAct = new PersonNumberForms("IndPreAct", Completeness.In, Voice.Act) {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return indPresentForm(entry, personNumber, voice, formBuilder, chooser);
        }
    };

    public static PersonNumberForms IndPrePas = new PersonNumberForms("IndPrePas", Voice.Pas) {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return indPresentForm(entry, personNumber, voice, formBuilder, chooser);
        }
    };

    public static ModStemf indFutStemf1 = new ModStemf(Astem, "IndFutForms1.stem", "bi");

    public static ImperfectEndings indFutEndings1 = new ImperfectEndings() {
        @Override
        public boolean applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return conjugationEndings(Conjugation.thirdc, personNumber, voice, Conjugation.piFpSiPair, formBuilder, chooser);
        }
    };

    public static Pair<Rulef,Rulef> IndFut2FpSiPair =
            Pair.of(FormRule.parseRule("IndFut2.act", PersonNumber.FpSi, "am"),
                    FormRule.parseRule("IndFut2.pas", PersonNumber.FpSi, "ar"));

    public static ImperfectEndings indFutEndings2 = new ImperfectEndings() {
        @Override
        public boolean applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return conjugationEndings(Conjugation.first, personNumber, voice, IndFut2FpSiPair, formBuilder, chooser);
        }
    };

    public static boolean indFutureForm(IEntry entry, PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
        if (entry.getAstem() == null) {
            return false;
        }
        Conjugation conjugation = entry.getConjugation();
        if (conjugation == null) {
            return false;
        }
        if (conjugation.hasi()) {
           return StrongStem.apply(entry, formBuilder, chooser) && indFutEndings2.applyEnding(personNumber, voice, formBuilder, chooser);
        }
        else {
            return indFutStemf1.apply(entry, formBuilder, chooser) && indFutEndings1.applyEnding(personNumber, voice, formBuilder, chooser);
        }
    }

    public static PersonNumberForms IndFutAct = new PersonNumberForms("IndFutAct", Voice.Act) {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return indFutureForm(entry, personNumber, voice, formBuilder, chooser);
        }
    };

    public static PersonNumberForms IndFutPas = new PersonNumberForms("IndFutPas", Voice.Pas) {
        @Override
        public boolean test(IEntry entry) {
            return haveAstemAndConj(entry);
        }
        @Override
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return indFutureForm(entry, personNumber, voice, formBuilder, chooser);
        }
    };

    public static ImperfectEndings regEndings = new ImperfectEndings() {
        @Override
        public boolean applyEnding(PersonNumber personNumber, Voice voice, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return conjugationEndings(Conjugation.second, personNumber, voice, Conjugation.regFpSiPair, formBuilder, chooser);
        }
    };

    public static class RegularPersonNumberForms extends PersonNumberForms {
        public final Stemf<IEntry> stemf;
        public RegularPersonNumberForms(String name, Voice voice, Stemf<IEntry> stemf) {
            super(name, voice);
            this.stemf = stemf;
        }
        public boolean test (IEntry t) {
            return stemf.test(t);
        }
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser) {
            return stemf.apply(entry, formBuilder, chooser) && regEndings.applyEnding(personNumber, voice, formBuilder, chooser);
        }
    }

    public static ModStemf indImpStem = new ModStemf(StrongStem, "IndImp.stem", "bā");
    public static RegularPersonNumberForms IndImpAct = new RegularPersonNumberForms("IndImpAct", Voice.Act, indImpStem);
    public static RegularPersonNumberForms IndImpPas = new RegularPersonNumberForms("IndImpPas", Voice.Pas, indImpStem);

    public static ConjModStemf subPreStem = new ConjModStemf() {
        @Override
        public Rulef getMod(Conjugation conjugation) {
            return conjugation.subj;
        }
    };

    public static RegularPersonNumberForms SubPreAct = new RegularPersonNumberForms("SubPreAct", Voice.Act, subPreStem);
    public static RegularPersonNumberForms SubPrePas = new RegularPersonNumberForms("SubPrePas", Voice.Pas, subPreStem);

    public static ModStemf subImpStem = new ModStemf(ActInf.stemf, "SubImp.stem", "<");

    public static RegularPersonNumberForms SubImpAct = new RegularPersonNumberForms("SubImpAct", Voice.Act, subImpStem);
    public static RegularPersonNumberForms SubImpPas = new RegularPersonNumberForms("SubImpPas", Voice.Pas, subImpStem);

    public static final List<String> stemNames = Lists.newArrayList("astem", "pstem", "jstem");

    public static class FormsEntry implements IEntry {

        public final String id;
        public final Map<String, Formf> formfMap;
        public final Conjugation conjugation;
        public final Map<String, Verb.System> irregularSystems;
        public final Traits traits;

        public FormsEntry (String id, Map<String,Formf> formfMap, Conjugation conjugation, Traits traits, Map<String,System> irregularSystems) {
            this.id = id;
            this.formfMap = formfMap;
            this.traits = traits;
            this.conjugation = conjugation;
            this.irregularSystems = irregularSystems;
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
        public final String id;
        Map<String,Formf> formfMap;
        Conjugation conjugation;
        Map<String,System> irregularSystems;
        TraitsMap traitsMap;
        public EntryBuilder(String id) {
            this.id = id;
            this.formfMap = Maps.newHashMap();
            this.conjugation = null;
            this.irregularSystems = Maps.newHashMap();
            this.traitsMap = new TraitsMap();
        }

        public FormsEntry makeEntry() {
            return new FormsEntry(id, formfMap, conjugation, traitsMap, irregularSystems);
        }

        public Formf makeFormf(String name, List<String> sl) {
            return Suffix.makeFormf(id, name, sl);
        }

        public EntryBuilder storeFormf(String name, List<String> sl) {
            formfMap.put(name, makeFormf(name, sl));
            return this;
        }

        public EntryBuilder storeStem(String sn, List<String> sl) {
            return storeFormf(sn, sl);
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

        public StoredForm makeStoredForm(String name, List<String> sl) {
            return new StoredForm(id, name, sl);
        }

        public EntryBuilder makeStoredInfinitive(InfinitiveFormSystem fs, List<String> sl) {
            return setIrregular(makeStoredForm(fs.getName(), sl));
        }

        public EntryBuilder makeStoredStemSystem(RegularPersonNumberForms fs, List<String> sl) {
            return setIrregular(new RegularPersonNumberForms(fs.getName(), fs.voice, makeStoredForm(fs.getName() + ".stem", sl)));
        }

        public EntryBuilder makeStoredForms(PersonNumberForms fs, Form.Stored<PersonNumber> stored) {
            return setIrregular(new StoredPersonNumberSystem(fs, stored));
        }

        public EntryBuilder makeStoredForms(PersonNumberForms fs, List<String> sll) {
            FormMap<PersonNumber> formMap = new FormMap<PersonNumber>(PersonNumber.class);
            formMap.putForms(id + "." + fs.getName(), PersonNumber.values(), sll);
            return makeStoredForms(fs, formMap);
        }

    }

    public static class StoredPersonNumberSystem implements PersonNumberSystem {
        public final PersonNumberForms formSystem;
        public final Form.Stored<PersonNumber> stored;
        public StoredPersonNumberSystem(PersonNumberForms personNumberForms,
                                        Form.Stored<PersonNumber> stored) {
            this.formSystem = personNumberForms;
            this.stored = stored;
        }
        public boolean apply(IEntry entry, PersonNumber personNumber, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf formf = stored.getStored(personNumber);
            return formf != null && formf.apply(formBuilder, chooser);
        }
        public boolean test(IEntry entry) {
            return true;
        }
        public String getName() {
            return formSystem.getName();
        }
    }


}