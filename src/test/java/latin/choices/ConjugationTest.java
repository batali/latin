
package latin.choices;

import com.google.common.collect.Lists;
import latin.forms.English;
import latin.forms.FormBuilder;
import latin.forms.Forms;
import latin.forms.IPhrase;
import latin.forms.Rulef;
import latin.forms.Suffix;
import org.junit.Test;

import java.util.List;

public class ConjugationTest {

    public void showEndings(Conjugation c) {
        System.out.println(c.toString());
        String st = c.stemEnding;
        for (Voice voice : Voice.values()) {
            for (PersonNumber personNumber : PersonNumber.values()) {
                FormBuilder fb = new FormBuilder();
                fb.add(c.stemEnding);
                Rulef r = c.ipmod(personNumber, voice);
                if (r != null) {
                    r.apply(fb, Alts.firstAlt);
                }
                Rulef e = Verb.endingRule(personNumber, voice, Verb.piFpSiPair);
                e.apply(fb, Alts.firstAlt);
                String fs = fb.getForm();
                System.out.println(personNumber.toString() + " " + voice.toString() + " " + fs);
            }
        }
    }


    @Test
    public void showConjugations() {
        for (Conjugation c : Conjugation.values()) {
            System.out.println(c.toString() + " " + c.stemEnding + " " + c.hasi() + " " + c.longStem() + " " + c.shorti());
        }
    }

    @Test
    public void testShowEndings() {
        showEndings(Conjugation.first);
        showEndings(Conjugation.second);
        showEndings(Conjugation.thirdc);
        showEndings(Conjugation.thirdi);
        showEndings(Conjugation.fourth);
    }

    public static List<String> getForms(Verb.FormsEntry entry, Verb.PersonNumberSystem personNumberForms,
                                        PersonNumber personNumber) {
        List<String> formList = Lists.newArrayList();
        CollectAlts collectAlts = new CollectAlts();
        do {
            Object f = personNumberForms.getForm(entry, personNumber, collectAlts);
            if (f != null) {
                formList.add(f.toString());
            }
        }
        while(collectAlts.incrementPositions());
        return formList;
    }


    public void showVerbEndings(Verb.FormsEntry entry) {
        System.out.println(entry.toString());
        for (Verb.PersonNumberForms pnf : Verb.getFormSystems(Verb.PersonNumberForms.class)) {
            System.out.println(pnf.name);
            Verb.PersonNumberSystem fs = (Verb.PersonNumberSystem) entry.getSystem(pnf);
            if (fs != null) {
                showVerbForms(entry, fs);
            }
        }
    }

    public void showVerbForms(Verb.FormsEntry entry, Verb.PersonNumberSystem personNumberForms) {
        for (PersonNumber personNumber : PersonNumber.values()) {
            List<String> forms = getForms(entry, personNumberForms, personNumber);
            String es = "";
            if (personNumberForms.getMood().isIndicative()) {
                IPhrase iPhrase = English.getVerbGroup(entry.english, personNumberForms.getVerbChoices(), personNumber, Alts.firstAlt);
                es = Forms.printPhrase(iPhrase).toString();
            }
            System.out.println(personNumber.toString() + " " + forms.toString() + " " + es);
        }
    }


    public static List<Verb.FormsEntry> verbs = Lists.newArrayList();

    public static Verb.FormsEntry makeVerb(String astem, String cn) {
        Verb.EntryBuilder b = new Verb.EntryBuilder(Suffix.unaccentString(astem));
        b.setConjugation(cn);
        b.storeStem("astem", astem);
        return b.makeEntry();
    }
    public static Verb.FormsEntry makeVerb(String astem, String cn, String es) {
        Verb.EntryBuilder b = new Verb.EntryBuilder(Suffix.unaccentString(astem));
        b.setConjugation(cn);
        b.storeStem("astem", astem);
        b.setEnglish(es);
        return b.makeEntry();
    }

    static {
        verbs.add(makeVerb("amā", "first", "love"));
        verbs.add(makeVerb("tenē", "second", "hold held"));
        verbs.add(makeVerb("dici", "thirdc", "say says said"));
        verbs.add(makeVerb("capi", "thirdi", "capture"));
        verbs.add(makeVerb("audī", "fourth", "hear heard"));

        Verb.EntryBuilder b = new Verb.EntryBuilder("esse")
                .setEnglish(English.beForms)
                .makeStoredInfinitive(Verb.ActInf, "esse")
                .makeStoredForms(Verb.IndPreAct, "sum es est sumus estis sunt")
                .makeStoredStem(Verb.IndImpAct, "erā")
                .makeStoredForms(Verb.IndFutAct, "erō eris erit erimus eritis erunt")
                .makeStoredStem(Verb.SubPreAct, "sī");
        verbs.add(b.makeEntry());
    }

    @Test
    public void testEntries() {
        for (Verb.FormsEntry formsEntry : verbs) {
            showVerbEndings(formsEntry);
        }
    }

    public void showInfinitives(Verb.FormsEntry e) {
        for (Verb.InfinitiveFormSystem fs : Verb.getFormSystems(Verb.InfinitiveFormSystem.class)) {
            Verb.InfinitiveSystem ss = (Verb.InfinitiveSystem) e.getSystem(fs);
            if (ss != null) {
                System.out.println(fs.getName() + " " + ss.getForm(e, Alts.firstAlt).toString());
            }
        }
    }

    @Test
    public void testInfinitives() {
        for (Verb.FormsEntry e : verbs) {
            showInfinitives(e);
        }
    }
}