
package latin.slots;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import javax.annotation.Nullable;

public class StringParser {

    String charSequence;
    int position;

    public StringParser(String string) {
        this.charSequence = string;
        this.position = 0;
    }

    public int size() {
        return charSequence.length();
    }

    public int getPosition() {
        return position;
    }

    private char charAt(int p) {
        return charSequence.charAt(p);
    }

    public char charAt() {
        return charAt(position);
    }

    public StringParser useChar(char c) {
        Preconditions.checkState(c == charAt());
        position += 1;
        return this;
    }

    public boolean startsWith(String prefix) {
        return charSequence.startsWith(prefix, position);
    }

    public boolean usePrefix(String start) {
        if (startsWith(start)) {
            position += start.length();
            return true;
        }
        else {
            return false;
        }
    }

    public void signalError(String msg) {
        System.err.println("*** " + msg);
        System.err.println("*** " + charSequence);
        System.err.println("*** " + Strings.padStart("^", position+1, ' '));
        throw new IllegalStateException(msg);
    }

    public boolean atEnd(boolean requireParen) {
        if (position == size()) {
            if (requireParen) {
                signalError("Missing close parenthesis");
            }
            return true;
        }
        else if (charAt() == ')') {
            if (requireParen) {
                useChar(')');
            }
            else {
                signalError("Extra close parenthesis");
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void requireEnd(boolean parenp) {
        if (!atEnd(parenp)) {
            signalError("Expression must end");
        }
    }

    public boolean apply (Predicate<Character> cpred) {
        return cpred.apply(charAt());
    }

    public int findEnd(Predicate<Character> pred) {
        int p = position + 1;
        int e = size();
        while (p < e && pred.apply(charAt(p))) {
            p += 1;
        }
        return p;
    }

    public int findStart(Predicate<Character> cpred) {
        int p = position;
        int e = size();
        while (p < e && !cpred.apply(charAt(p))) {
            p += 1;
        }
        return p;
    }

    public String getToken(Predicate<Character> cpred) {
        int ts = position;
        int te = findEnd(cpred);
        position = te;
        return charSequence.substring(ts, te);
    }

    public static final Predicate<Character> isSpacePredicate = CharMatcher.WHITESPACE;

    public static final Predicate<Character> nonSpacePredicate = new Predicate<Character>() {
        @Override
        public boolean apply(@Nullable Character character) {
            return !isSpacePredicate.apply(character);
        }
    };

    public StringParser ignoreSpaces() {
        position = findStart(nonSpacePredicate);
        return this;
    }

    public String nonWhitespaceToken() {
        return getToken(nonSpacePredicate);
    }

}
