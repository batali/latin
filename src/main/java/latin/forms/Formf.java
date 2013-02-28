
package latin.forms;

import latin.choices.Alts;

public interface Formf {
    boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser);
}