
package latin.forms;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public abstract class AbstractToken implements Token {

    Token subToken (int s, int e) {
        return new SubToken(this, s, e);
    }

    @Override
    public void appendTo(Appendable appendable) {
        appendTo(appendable, 0, length());
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public int endOffset(int fromEnd) {
        return length() - fromEnd;
    }

    @Override
    public Token butFirst(int n) {
        return subSequence(n, length());
    }

    @Override
    public Token butLast(int n) {
        return subSequence(0, length() - n);
    }

    @Override
    public char endChar(int fromEnd) {
        return charAt(endOffset(fromEnd));
    }

    @Override
    @Nonnull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb, 0, length());
        return sb.toString();
    }

    @Override
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