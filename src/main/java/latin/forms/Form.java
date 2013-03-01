
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

    public static interface Forms<AT> {
        public boolean getForm(AT a, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

}