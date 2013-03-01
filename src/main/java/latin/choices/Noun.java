
package latin.choices;

import latin.forms.Formf;
import latin.forms.Forms;
import latin.forms.IForm;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.StoredForms;

import java.util.EnumMap;

public class Noun {

    public static StoredForms<CaseNumber> emptyForms = new StoredForms<CaseNumber> () {
        @Override
        public Formf getStored(CaseNumber key) {
            return null;
        }
    };

    public interface Rules {
        public Rulef getRule(CaseNumber caseNumber);
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

    public static <ET> IForm getForm(CaseNumber key,
                                     StoredForms<CaseNumber> storedForms,
                                     Stemf<ET> stemf,
                                     ET stemEntry,
                                     Rules rules,
                                     Alts.Chooser chooser) {
        if (storedForms != null) {
            Formf storedFormf = storedForms.getStored(key);
            if (storedFormf != null) {
                return storedFormf.apply(chooser);
            }
        }
        Rulef ruleFormf = rules.getRule(key);
        if (ruleFormf != null) {
            return Forms.applyRule(ruleFormf, Forms.applyStemf(stemf, stemEntry, chooser), chooser);
        }
        CaseNumber rkey = renamed.get(key);
        if (rkey != null) {
            return getForm(rkey, storedForms, stemf, stemEntry, rules, chooser);
        }
        else {
            return null;
        }
    }

}