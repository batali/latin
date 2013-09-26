package latin.choices;

import latin.forms.Token;
import latin.forms.Tokens;
import latin.util.PathId;

import java.util.List;

public enum Declension {

    First("ae"),
    Second("ī"),
    Third("is"),
    Fourth("ūs"),
    Fifth("eī,ēī");

    public final List<Token> gsiEndings;
    final PathId.Element path;

    Declension (String gst) {
        this.gsiEndings = Tokens.parseTokens(gst);
        this.path = new PathId.Root(this);
    }

    public PathId.Element getPath() {
        return this.path;
    }

}