package latin.choices;

import latin.forms.Form;
import latin.forms.ModRule;

public class LatinNounForms {

    public static CaseNumber getRenamed(CaseNumber caseNumber, Gender gender) {
        switch (caseNumber) {
            case VocSi:
            case VocPl:
                return caseNumber.toCase(Case.Nom);
            case AccSi:
            case AccPl:
                return gender.isNeuter() ? caseNumber.toCase(Case.Nom) : null;
            case LocSi:
                return CaseNumber.AblSi;
            case AblPl:
            case LocPl:
                return CaseNumber.DatPl;
            default:
                return null;
        }
    }

    public interface KeyFormEntry {

        Form getKeyForm(CaseNumber cn, Gender g);

        default Form getNounForm(CaseNumber cn, Gender g) {
            Form form = getKeyForm(cn, g);
            if (form != null) {
                return form;
            }
            CaseNumber rcn = getRenamed(cn, g);
            if (rcn != null) {
                return getNounForm(rcn, g);
            }
            return null;
        }
    }

    public interface RegularAndIrregularFormEntry extends KeyFormEntry {

        default Form getIrregularForm(CaseNumber cn, Gender g) {
            return null;
        }

        Form getRegularForm(CaseNumber cn, Gender g);

        default Form getKeyForm(CaseNumber cn, Gender g) {
            Form irregular = getIrregularForm(cn, g);
            if (irregular != null) {
                return irregular;
            } else {
                return getRegularForm(cn, g);
            }
        }
    }

    public interface GstemRulesFormEntry extends RegularAndIrregularFormEntry {

        Form getGstem();

        ModRule getGstemRule(CaseNumber cn, Gender g);

        default Form getRegularForm(CaseNumber cn, Gender g) {
            ModRule rule = getGstemRule(cn, g);
            return rule != null ? rule.apply(getGstem()) : null;
        }
    }
}