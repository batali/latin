
package latin.choices;

import com.google.common.collect.Lists;
import latin.forms.FormBuilder;
import latin.forms.Rulef;
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
                Rulef e = Conjugation.endingRule(personNumber, voice, Conjugation.piFpSiPair);
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
            System.out.println(personNumber.toString() + " " + forms.toString());
        }
    }

    public static List<Verb.FormsEntry> verbs = Lists.newArrayList();

    static {
        /*
        verbs.add(Verb.makeEntry("amā", "first"));
        verbs.add(Verb.makeEntry("tenē", "second"));
        verbs.add(Verb.makeEntry("dici", "thirdc"));
        verbs.add(Verb.makeEntry("capi", "thirdi"));
        verbs.add(Verb.makeEntry("audī", "fourth"));
        Verb.FormsEntry esse = new Verb.FormsEntry("esse");
        esse.setStoredForms(Verb.IndPreAct, "sum es est sumus estis sunt");
        esse.setIrregularSystem(Verb.ActInf, "esse");
        esse.setIrregularStem(Verb.IndImpAct, "erā");
        esse.setStoredForms(Verb.IndFutAct, "erō eris erit erimus eritis erunt");
        esse.setIrregularStem(Verb.SubPreAct, "sī");
        esse.setIrregularStem(Verb.SubImpAct, "essē");
        verbs.add(esse);
        */
    }

    @Test
    public void testEntries() {
        for (Verb.FormsEntry formsEntry : verbs) {
            showVerbEndings(formsEntry);
        }
    }

    public void showInfinitives(Verb.FormsEntry e) {
        for (Verb.InfinitiveFormSystem fs : Verb.getFormSystems(Verb.InfinitiveFormSystem.class)) {
            FormBuilder fb = new FormBuilder();
            Verb.InfinitiveSystem ss = (Verb.InfinitiveSystem) e.getSystem(fs);
            if (ss != null) {
                System.out.println(fs.getName() + " " + fb.getForm());
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