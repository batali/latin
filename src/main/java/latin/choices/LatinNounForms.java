package latin.choices;

import latin.forms.Form;
import latin.forms.Rule;

import java.util.function.BiFunction;

public class LatinNounForms {

    static CaseNumber getRenamed(CaseNumber key, Gender gender) {
        if (key.equals(CaseNumber.AblPl)) {
            return CaseNumber.DatPl;
        }
        if (key.caseKey.isVocative()) {
            return key.toCase(Case.Nom);
        }
        if (key.caseKey.isLocative()) {
            return key.toCase(Case.Abl);
        }
        if (gender.isNeuter()) {
            if (key.caseKey.isAccusative()) {
                return key.toCase(Case.Nom);
            }
        }
        return null;
    }

    static Form getForm(BiFunction<CaseNumber, Gender, Form> formFunction,
                        CaseNumber cn, Gender g) {
        Form f = formFunction.apply(cn, g);
        if (f != null) {
            return f;
        }
        CaseNumber rcn = getRenamed(cn, g);
        if (rcn != null) {
            return getForm(formFunction, rcn, g);
        }
        return null;
    }

    static interface GstemFormFunction extends BiFunction<CaseNumber, Gender, Form> {

        default public Form getIrregularForm(CaseNumber cn, Gender g) {
            return null;
        }

        public Form getGstem();

        public Rule getGstemRule(CaseNumber cn, Gender g);

        default Form apply(CaseNumber cn, Gender g) {
            Form i = getIrregularForm(cn, g);
            if (i != null) {
                return i;
            }
            Rule r = getGstemRule(cn, g);
            if (r != null) {
                return r.apply(getGstem());
            }
            return null;
        }
    }

}
