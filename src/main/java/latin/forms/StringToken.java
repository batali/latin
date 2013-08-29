
package latin.forms;

import com.google.common.base.Function;

import javax.annotation.Nullable;

public class StringToken extends AbstractToken implements TokenRule {

    final String string;

    public StringToken (String string) {
        this.string = string;
    }

    @Override
    public StringBuilder addToBuilder(StringBuilder stringBuilder, int sp, int ep) {
        if (ep > sp) {
            stringBuilder.append(string, sp, ep);
        }
        return stringBuilder;
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
    public Token apply(@Nullable Token token) {
        return (token == null || token.isEmpty()) ? this : new AddedStringToken(token, string);
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