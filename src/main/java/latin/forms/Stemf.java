
package latin.forms;

import latin.choices.Alts;

public interface Stemf<ET> {
    public boolean test(ET e);
    public IFormBuilder apply(ET e, Alts.AltChooser altChooser);
}