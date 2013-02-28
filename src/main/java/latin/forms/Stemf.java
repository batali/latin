
package latin.forms;

import latin.choices.Alts;

public interface Stemf<ET> {
    public boolean test(ET e);
    public boolean apply(ET e, IFormBuilder formBuilder, Alts.Chooser chooser);
}