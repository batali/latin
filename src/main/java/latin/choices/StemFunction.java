
package latin.choices;

import latin.forms.Token;

public interface StemFunction<ET> {
    public Token apply(ET e, Alts.Chooser chooser);
}