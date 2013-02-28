
package latin.choices;

import latin.forms.Form;
import latin.forms.Formf;

public class Noun {

    public static Form.Stored<CaseNumber> emptyForms = new Form.Stored<CaseNumber> () {
        @Override
        public Formf getStored(CaseNumber key) {
            return null;
        }
    };

}