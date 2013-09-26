
package latin.forms;

import com.google.common.base.Preconditions;

public abstract class AbstractToken implements Token {

    Token subToken (int s, int e) {
        return new SubToken(this, s, e);
    }

    public void appendTo(Appendable appendable) {
        appendTo(appendable, 0, length());
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public int endOffset(int fromEnd) {
        return length() - fromEnd;
    }

    public Token butFirst(int n) {
        return subSequence(n, length());
    }

    public Token butLast(int n) {
        return subSequence(0, length() - n);
    }

    public char endChar(int fromEnd) {
        return charAt(endOffset(fromEnd));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb);
        return sb.toString();
    }

    public Token subSequence(int sp, int ep) {
        Preconditions.checkArgument(ep <= length());
        Preconditions.checkArgument(sp <= ep);
        if (sp == 0 && ep == length()) {
            return this;
        }
        else if (sp == ep) {
            return Tokens.emptyToken;
        }
        else {
            return subToken(sp, ep);
        }
    }

}