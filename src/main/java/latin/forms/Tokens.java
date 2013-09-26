
package latin.forms;

import com.google.common.base.Preconditions;

import java.util.List;

public class Tokens {

    public static List<Token> parseTokens(String ts) {
        return Suffix.csplit(ts, StringToken.toStringToken);
    }

    public static final Token emptyToken = new AbstractToken() {
        @Override
        public Token subSequence(int s, int e) {
            Preconditions.checkArgument(s == 0);
            Preconditions.checkArgument(e == 0);
            return this;
        }
        @Override
        public void appendTo(Appendable appendable, int s, int e) {
            Preconditions.checkArgument(s == 0);
            Preconditions.checkArgument(e == 0);
        }
        @Override
        public int length() {
            return 0;
        }
        @Override
        public char charAt(int i) {
            Preconditions.checkElementIndex(i, 0);
            return 0;
        }
    };

    public static Token token(Object o) {
        if (o == null) {
            return emptyToken;
        }
        else if (o instanceof Token) {
            return (Token)o;
        }
        else {
            String s = o.toString();
            return s.isEmpty() ? emptyToken : new StringToken(s);
        }
    }

    public static Token pair(Object ho, Object to) {
        Token h = token(ho);
        Token t = token(to);
        if (h.isEmpty()) {
            return t;
        }
        else if (t.isEmpty()) {
            return h;
        }
        else {
            return new PairToken(h, t);
        }
    }

}

