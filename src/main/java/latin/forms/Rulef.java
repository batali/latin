
package latin.forms;

import latin.choices.Alts;

public interface Rulef {
    public boolean apply(IFormBuilder iFormBuilder, Alts.Chooser chooser);
}