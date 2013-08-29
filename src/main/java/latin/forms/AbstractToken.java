
package latin.forms;

import com.google.common.base.Preconditions;

public abstract class AbstractToken implements Token {

    public static final Token emptyToken = new AbstractToken() {
        @Override
        public Token subSequence(int s, int e) {
            Preconditions.checkArgument(s == 0);
            Preconditions.checkArgument(e == 0);
            return this;
        }
        @Override
        public StringBuilder addToBuilder(StringBuilder stringBuilder, int sp, int ep) {
            Preconditions.checkArgument(ep == 0);
            return stringBuilder;
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

    public Token subSequence(int sp, int ep) {
        Preconditions.checkArgument(ep <= length());
        Preconditions.checkArgument(sp <= ep);
        if (sp == 0 && ep == length()) {
            return this;
        }
        else if (sp == ep) {
            return emptyToken;
        }
        else {
            return new SubToken(this, sp, ep);
        }
    }

    public StringBuilder addToBuilder(StringBuilder sb) {
        return addToBuilder(sb, 0, length());
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public int endOffset(int fromEnd) {
        return length() - fromEnd;
    }

    public Token butFirst(int n) {
        if (n == 0) {
            return this;
        }
        else {
            return subSequence(n, length());
        }
    }

    public Token butLast(int n) {
        if (n == 0) {
            return this;
        }
        else {
            return subSequence(0, length() - n);
        }
    }

    public Character endChar(int fromEnd) {
        return charAt(endOffset(fromEnd));
    }

    public String toString() {
        return addToBuilder(new StringBuilder()).toString();
    }

}