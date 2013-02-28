
package latin.forms;

import latin.choices.Alts;

import javax.annotation.Nullable;

public interface Rulef {
    public @Nullable String endingString();
    public Rulef firstRule();
    public boolean apply(IFormBuilder iFormBuilder, Alts.Chooser chooser);
}