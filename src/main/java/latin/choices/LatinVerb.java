package latin.choices;

import com.google.common.base.Preconditions;

import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.Rule;
import latin.util.PathId;

import java.util.Objects;

public abstract class LatinVerb {

    static final PersonNumber fpsiKey = PersonNumber.FpSi;

    public interface PnVoiceForms<T> {
        public Form apply(T t, PersonNumber pn, Voice v);
    }

    public interface PnEndings extends PnVoiceForms<Form> {
    }

    public interface PnRules extends PnEndings {

        Rule getRule(PersonNumber pn, Voice v);

        default Form apply(Form stem, PersonNumber pn, Voice v) {
            return getRule(pn, v).apply(stem);
        }
    }

    public interface FormsEntry {

        Form getForm(PersonNumber personNumber);
    }

    public interface IEntry {

        public Form getAstem();

        public Conjugation getConjugation();

        public Form getPstem();

        public Form getJstem();

        default public boolean hasAstemAndConjugation() {
            return getAstem() != null && getConjugation() != null;
        }

    }

    public interface StemFunction {

        public Form apply(IEntry entry);

        public boolean test(IEntry entry);
    }

    public static abstract class StoredStemFunction implements StemFunction {

        @Override
        public boolean test(IEntry entry) {
            return apply(entry) != null;
        }
    }

    public static StemFunction Astem = new StoredStemFunction() {
        @Override
        public Form apply(IEntry entry) {
            return entry.getAstem();
        }
    };

    public static StemFunction Pstem = new StoredStemFunction() {
        @Override
        public Form apply(IEntry entry) {
            return entry.getPstem();
        }
    };

    public static StemFunction Jstem = new StoredStemFunction() {
        @Override
        public Form apply(IEntry entry) {
            return entry.getJstem();
        }
    };

    static abstract class ConjModStemFunction implements StemFunction {

        abstract Rule getModRule(Conjugation conjugation);

        @Override
        public boolean test(IEntry iEntry) {
            return iEntry.hasAstemAndConjugation();
        }

        @Override
        public Form apply(IEntry entry) {
            return getModRule(entry.getConjugation()).apply(entry.getAstem());
        }
    }

    public static StemFunction StrongStem = new ConjModStemFunction() {
        @Override
        Rule getModRule(Conjugation conjugation) {
            return conjugation.strongRule;
        }
    };

    public static StemFunction AiStem = new ConjModStemFunction() {
        @Override
        Rule getModRule(Conjugation conjugation) {
            return conjugation.aiRule;
        }
    };

    public static StemFunction SubjStem = new ConjModStemFunction() {
        @Override
        Rule getModRule(Conjugation conjugation) {
            return conjugation.subjRule;
        }
    };

    static class ModifiedStemFunction implements StemFunction {

        StemFunction prev;
        Rule rule;

        ModifiedStemFunction(StemFunction prev, Rule rule) {
            this.prev = prev;
            this.rule = rule;
        }

        public ModifiedStemFunction(PathId parentPathId, StemFunction prev, String rs) {
            this(prev, new ModRule(parentPathId.makeChild("mod"), rs));
        }

        @Override
        public Form apply(IEntry e) {
            return rule.apply(prev.apply(e));
        }

        @Override
        public boolean test(IEntry e) {
            return prev.test(e);
        }
    }

    static KeyRulesMap<PersonNumber> makeRulesMap(String name, Voice v, String adds) {
        KeyRulesMap<PersonNumber> keyRulesMap = new KeyRulesMap<>(PersonNumber.class, PathId.makePath(name, v));
        keyRulesMap.add(adds, PersonNumber::fromString);
        return keyRulesMap;
    }

    static KeyRulesMap<PersonNumber> presentActRules =
        makeRulesMap("present", Voice.Act, "SpSi=s TpSi=t FpPl=mus SpPl=tis TpPl=nt");
    static KeyRulesMap<PersonNumber> presentPasRules =
        makeRulesMap("present", Voice.Pas, "SpSi=ris TpSi=tur FpPl=mur SpPl=mini TpPl=ntur");

    public static class PresentEndings implements PnRules {

        PathId parentPathId;
        Rule actFpSiRule;
        Rule pasFpSiRule;

        public PresentEndings(PathId parentPathId, String ars, String prs) {
            this.parentPathId = parentPathId;
            this.actFpSiRule = new ModRule(parentPathId.makeChild(Voice.Act).makeChild(fpsiKey), ars);
            this.pasFpSiRule = new ModRule(parentPathId.makeChild(Voice.Pas).makeChild(fpsiKey), prs);
        }

        @Override
        public Rule getRule(PersonNumber personNumber, Voice voice) {
            if (personNumber.equals(fpsiKey)) {
                return voice.select(actFpSiRule, pasFpSiRule);
            } else {
                return voice.select(presentActRules, presentPasRules).get(personNumber);
            }
        }
    }

    public static Form conjugationModApply(Form stem, Conjugation conjugation, PnEndings endings,
                                           PersonNumber pn, Voice v) {
        return endings.apply(conjugation.applyIndPreRule(stem, pn, v), pn, v);
    }

    public static class ConjugationModEndings implements PnEndings {

        public Conjugation conjugation;
        public PnEndings endings;

        public ConjugationModEndings(Conjugation conjugation, PnEndings endings) {
            this.conjugation = conjugation;
            this.endings = endings;
        }

        public Form apply(Form stem, PersonNumber pn, Voice v) {
            return conjugationModApply(stem, conjugation, endings, pn, v);
        }
    }

    static PresentEndings indPreEndings =
        new PresentEndings(PathId.makePath("IndPre", "endings"), "ō", "or");

    static class StemEndingsBuilder {

        PathId pathId;
        StemFunction stemFunction;
        PnEndings endings;

        StemEndingsBuilder(PathId pathId) {
            this.pathId = pathId;
            this.stemFunction = null;
            this.endings = null;
        }

        public StemEndingsBuilder setStemFunction(StemFunction stemFunction) {
            this.stemFunction = stemFunction;
            return this;
        }

        public StemEndingsBuilder setEndings(PnEndings endings) {
            this.endings = endings;
            return this;
        }

        ModifiedStemFunction makeStemFunction(StemFunction prev, String ms) {
            return new ModifiedStemFunction(prev, new ModRule(pathId.makeChild("stem").makeChild
                ("mod"),
                                                              ms));
        }

        public StemEndingsBuilder setStemFunction(StemFunction prev, String ms) {
            return setStemFunction(makeStemFunction(prev, ms));
        }

        PnEndings makeEndings(String ars, String prs) {
            return new PresentEndings(pathId.makeChild("endings"), ars, prs);
        }

        public StemEndingsBuilder setEndings(Conjugation conjugation, PnEndings endings) {
            return setEndings(new ConjugationModEndings(conjugation, endings));
        }

        public StemEndingsBuilder setEndings(Conjugation conjugation, String ars, String prs) {
            return setEndings(conjugation, makeEndings(ars, prs));
        }

        public StemFunction getStemFunction() {
            Preconditions.checkNotNull(stemFunction);
            return stemFunction;
        }

        public boolean hasEndings() {
            return endings != null;
        }

        public PnEndings getEndings() {
            Preconditions.checkNotNull(endings);
            return endings;
        }

        public Form applyActiveEnding(Form stem, PersonNumber pn) {
            Preconditions.checkNotNull(endings);
            return endings.apply(stem, pn, Voice.Act);
        }

        public Form applyPassiveEnding(Form stem, PersonNumber pn) {
            Preconditions.checkNotNull(endings);
            return endings.apply(stem, pn, Voice.Pas);
        }

        public Form apply(IEntry entry, PersonNumber pn, Voice v) {
            Form stem = getStemFunction().apply(entry);
            return getEndings().apply(stem, pn, v);
        }

        public Form applyActive(IEntry entry, PersonNumber pn) {
            return apply(entry, pn, Voice.Act);
        }

        public Form applyPassive(IEntry entry, PersonNumber pn) {
            return apply(entry, pn, Voice.Pas);
        }
    }


    static class MoodTense {

        Mood mood;
        Tense tense;

        MoodTense(Mood mood, Tense tense) {
            this.mood = mood;
            this.tense = tense;
        }

        public String toString() {
            return mood.toString() + tense.toString();
        }

        public static MoodTense fromEnums(Mood mood, Tense tense) {
            return new MoodTense(mood, tense);
        }

        public static MoodTense fromString(String s) {
            Mood mood = Mood.fromString(s.substring(0, 3));
            Tense tense = Tense.fromString(s.substring(3, 6));
            return fromEnums(mood, tense);
        }

        public Mood getMood() {
            return mood;
        }

        public Tense getTense() {
            return tense;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mood, tense);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof MoodTense)) {
                return false;
            }
            MoodTense omt = (MoodTense) o;
            return mood == omt.mood && tense == omt.tense;
        }

        public MoodTenseVoice forVoice(Voice voice) {
            return MoodTenseVoice.get(this, voice);
        }
    }

    static class MoodTenseVoice {

        MoodTense moodTense;
        Voice voice;

        MoodTenseVoice(MoodTense moodTense, Voice voice) {
            this.moodTense = moodTense;
            this.voice = voice;
        }

        public String toString() {
            return moodTense.toString() + voice.toString();
        }

        public static MoodTenseVoice fromEnums(Mood mood, Tense tense, Voice voice) {
            return new MoodTenseVoice(MoodTense.fromEnums(mood, tense), voice);
        }

        public static MoodTenseVoice fromString(String s) {
            MoodTense moodTense = MoodTense.fromString(s);
            Voice voice = Voice.fromString(s.substring(6, 9));
            return new MoodTenseVoice(moodTense, voice);
        }

        public static MoodTenseVoice get(MoodTense moodTense, Voice voice) {
            return new MoodTenseVoice(moodTense, voice);
        }

        public Mood getMood() {
            return moodTense.getMood();
        }

        public Tense getTense() {
            return moodTense.getTense();
        }

        public Voice getVoice() {
            return voice;
        }

        public MoodTenseVoice forVoice(Voice newVoice) {
            if (voice.equals(newVoice)) {
                return this;
            } else {
                return get(moodTense, newVoice);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(moodTense, voice);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof MoodTenseVoice)) {
                return false;
            }
            MoodTenseVoice omt = (MoodTenseVoice) o;
            return moodTense.equals(omt.moodTense) && voice.equals(omt.voice);
        }
    }

    abstract static class PnSystem {

        MoodTenseVoice choices;

        public PnSystem(MoodTenseVoice choices) {
            this.choices = choices;
        }

        public Voice getVoice() {
            return choices.getVoice();
        }

        abstract public boolean test(IEntry iEntry);

        public PnEndings getEndings() {
            return null;
        }

        abstract public Form apply(IEntry e, PersonNumber pn);

    }

    static Form indPreApply(IEntry e, PersonNumber pn, Voice v) {
        return conjugationModApply(e.getAstem(), e.getConjugation(), indPreEndings, pn, v);
    }

    static PnSystem indPreAct = new PnSystem (MoodTenseVoice.fromString("IndPreAct")) {
        @Override
        public boolean test(IEntry entry) {
            return entry.hasAstemAndConjugation();
        }
        @Override
        public Form apply(IEntry e, PersonNumber pn) {
            return indPreApply(e, pn, getVoice());
        }
    };

    static PnSystem indPrePas = new PnSystem (MoodTenseVoice.fromString("IndPrePas")) {
        @Override
        public boolean test(IEntry entry) {
            return entry.hasAstemAndConjugation();
        }
        @Override
        public Form apply(IEntry e, PersonNumber pn) {
            return indPreApply(e, pn, getVoice());
        }
    };

    static StemEndingsBuilder indFutBuilder1 = new StemEndingsBuilder(PathId.makePath("IndFut").makeChild("1"))
        .setStemFunction(Astem, "bi").setEndings(Conjugation.thirdc, indPreEndings);
    static StemEndingsBuilder indFutBuilder2 = new StemEndingsBuilder(PathId.makePath("IndFut").makeChild("2"))
        .setStemFunction(StrongStem).setEndings(Conjugation.first, "am", "ar");

    static Form indFutApply(IEntry e, PersonNumber pn, Voice v) {
        Conjugation conjugation = e.getConjugation();
        return (conjugation.hasi() ? indFutBuilder2 : indFutBuilder1).apply(e, pn, v);
    };

    static PnSystem indFutAct = new PnSystem(MoodTenseVoice.fromString("IndFutAct")) {
        public boolean test(IEntry iEntry) {
            return iEntry.hasAstemAndConjugation();
        }
        @Override
        public Form apply(IEntry e, PersonNumber pn) {
            return indFutApply(e, pn, getVoice());
        }
    };

    static PnSystem indFutPas = new PnSystem(MoodTenseVoice.fromString("IndFutPas")) {
        public boolean test(IEntry iEntry) {
            return iEntry.hasAstemAndConjugation();
        }
        @Override
        public Form apply(IEntry e, PersonNumber pn) {
            return indFutApply(e, pn, getVoice());
        }
    };

    static PnEndings regularEndings = new ConjugationModEndings(Conjugation.second, new PresentEndings(PathId.makeRoot(
        "RegularEndings"), "m", "r"));


    static class StemEndingsPnSystem extends PnSystem {

        StemFunction stemFunction;
        PnEndings endings;

        public StemEndingsPnSystem(MoodTenseVoice choices,
                                   StemFunction stemFunction,
                                   PnEndings endings) {
            super(choices);
            this.stemFunction = stemFunction;
            this.endings = endings;
        }

        public StemEndingsPnSystem(StemEndingsBuilder builder) {
            this(MoodTense.fromString(builder.pathId.name.toString()).forVoice(Voice.Act),
                 builder.getStemFunction(),
                 (builder.hasEndings() ? builder.getEndings() : regularEndings));
        }

        @Override
        public boolean test(IEntry e) {
            return stemFunction.test(e);
        }

        @Override
        public PnEndings getEndings() {
            return endings;
        }

        @Override
        public Form apply(IEntry e, PersonNumber pn) {
            Form stem = stemFunction.apply(e);
            return endings.apply(stem, pn, getVoice());
        }

        public StemEndingsPnSystem makePassiveSystem() {
            return new StemEndingsPnSystem(choices.forVoice(Voice.Pas), stemFunction, endings);
        }
    }

    static StemEndingsBuilder moodTenseBuilder(String mt) {
        return new StemEndingsBuilder(PathId.makeRoot(MoodTense.fromString(mt)));
    }

    static StemEndingsPnSystem indImpAct =
        new StemEndingsPnSystem(moodTenseBuilder("IndImp").setStemFunction(StrongStem, "bā"));

    static PnSystem indImpPas = indImpAct.makePassiveSystem();

    static StemEndingsPnSystem subPreAct =
        new StemEndingsPnSystem(moodTenseBuilder("SubPre").setStemFunction(SubjStem));

    static PnSystem subPrePas = subPreAct.makePassiveSystem();

    static StemEndingsPnSystem subImpAct =
        new StemEndingsPnSystem(moodTenseBuilder("SubImp").setStemFunction(AiStem));

    static PnSystem subImpPas = subImpAct.makePassiveSystem();

    static PnRules perfectRules = new PnRules() {
        KeyRulesMap<PersonNumber> rulesMap = makeRulesMap("perfect", Voice.Act,
                                                          "FpSi=ī SpSi=istī TpSi=it FpPl=imus SpPl=istis TpPl=ērunt");

        @Override
        public Rule getRule(PersonNumber pn, Voice v) {
            Preconditions.checkState(v.isActive());
            return rulesMap.get(pn);
        }
    };

    static StemEndingsPnSystem indPer =
        new StemEndingsPnSystem(moodTenseBuilder("IndPer").setStemFunction(Pstem).setEndings(perfectRules));

    static StemEndingsPnSystem subPer =
        new StemEndingsPnSystem(moodTenseBuilder("SubPer").setStemFunction(Pstem, "erī"));

    static StemEndingsPnSystem indPlu =
        new StemEndingsPnSystem(moodTenseBuilder("IndPlu").setStemFunction(Pstem, "erā"));

    static StemEndingsPnSystem subPlu =
        new StemEndingsPnSystem(moodTenseBuilder("SubPlu").setStemFunction(Pstem, "issē"));

    static StemEndingsPnSystem indFup =
        new StemEndingsPnSystem(
            moodTenseBuilder("IndFup").setStemFunction(Pstem, "eri")
                                      .setEndings(Conjugation.first, indPreEndings));
}


