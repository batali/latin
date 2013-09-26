
package latin.forms;

public class SubToken extends AbstractToken {

    final Token token;
    final int start;
    final int end;

    public SubToken(Token token, int start, int end) {
        this.token = token;
        this.start = start;
        this.end = end;
    }

    public int length() {
        return end - start;
    }

    public char charAt(int p) {
        return token.charAt(start + p);
    }

    public Token subToken(int s, int e) {
        return token.subSequence(start + s, start + e);
    }

    public void appendTo(Appendable appendable, int s, int e) {
        token.appendTo(appendable, start + s, start + e);
    }
}
