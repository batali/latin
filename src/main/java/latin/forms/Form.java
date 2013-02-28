
package latin.forms;

import latin.choices.Alts;

public class Form {

    private Form() {
    }

    public static interface Stored<AT> {
        public Formf getStored(AT a);
    }

    public static interface Rules<AT> {
        public Rulef getRule(AT a);
    }

    public static interface Mod {
        public boolean applyMod(IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static interface Forms<AT> {
        public boolean getForm(AT a, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static interface Mods<AT> {
        public boolean applyMod(AT a, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static interface EntryForm<ET> {
        public boolean getForm(ET e, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static interface FormFunction<ET> {
        public boolean apply(ET e, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static interface FormFunctions<ET,AT> {
        public boolean apply(ET e, AT a, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

}