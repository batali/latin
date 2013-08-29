
package latin.forms;

import com.google.common.base.Preconditions;

public class ReplacedCharToken extends AbstractToken {
    final Token token;
    final int position;
    final char replacementChar;
    public ReplacedCharToken(Token token,  int position, char replacementChar) {
        Preconditions.checkElementIndex(position, token.length());
        this.token = token;
        this.position = position;
        this.replacementChar = replacementChar;
    }

    @Override
    public int length() {
        return token.length();
    }

    @Override
    public StringBuilder addToBuilder(StringBuilder stringBuilder, int sp, int ep) {
        int bep = Math.min(ep, position);
        if (sp < bep) {
            token.addToBuilder(stringBuilder, sp, bep);
        }
        if (position >= sp && position < ep) {
            stringBuilder.append(replacementChar);
        }
        int asp = Math.max(sp, position+1);
        if (asp < ep) {
            token.addToBuilder(stringBuilder, asp, ep);
        }
        return stringBuilder;
    }

    @Override
    public char charAt(int i) {
        if (i == position) {
            return replacementChar;
        }
        else {
            return token.charAt(i);
        }
    }
}

