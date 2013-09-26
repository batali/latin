
package latin.forms;

import com.google.common.base.Function;

import java.io.IOException;

import javax.annotation.Nullable;

public class StringToken extends AbstractToken implements TokenRule {

    public final String string;

    public StringToken (String string) {
        this.string = string;
    }

    public StringToken (char c) {
        this(Character.toString(c));
    }

    @Override
    public int length() {
        return string.length();
    }

    @Override
    public char charAt(int i) {
        return string.charAt(i);
    }

    @Override
    public void appendTo(Appendable appendable, int s, int e) {
        try {
            appendable.append(string, s, e);
        }
        catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public Token apply(@Nullable Token token) {
        return (token == null || token.isEmpty()) ? this : new PairToken(token, this);
    }

    @Override
    public String getSpec() {
        return string;
    }

    public static final Function<String,Token> toStringToken = new Function<String, Token>() {
        @Override
        public Token apply(@Nullable String s) {
            return (s == null) ? null : new StringToken(s);
        }
    };

}