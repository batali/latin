
package latin.forms;

import latin.choices.Alts;

import java.util.List;

public class Forms {

    private Forms () {
    }

    public static <ET> IFormBuilder applyStemf (Stemf<ET> stemf, ET e, Alts.Chooser chooser) {
        if (stemf == null) {
            return null;
        }
        else {
            return stemf.apply(e, chooser);
        }
    }

    public static IFormBuilder applyRule(Rulef rulef, IFormBuilder formBuilder, Alts.Chooser chooser) {
        if (formBuilder == null || rulef == null) {
            return null;
        }
        if (rulef.apply(formBuilder, chooser)) {
            return formBuilder;
        }
        else {
            return null;
        }
    }

    public static IFormBuilder applyFormf(Formf formf, Alts.Chooser chooser) {
        if (formf == null) {
            return null;
        }
        else {
            return formf.apply(chooser);
        }
    }

    public static String formString(IForm iForm) {
        return (iForm != null) ? iForm.toString() : null;
    }

    public static void addForm(IForm iform, List<String> strings) {
        if (iform != null) {
            strings.add(iform.toString());
        }
    }
}