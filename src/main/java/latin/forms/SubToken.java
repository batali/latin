
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
    public StringBuilder addToBuilder(StringBuilder sb, int sp, int ep) {
        if (ep > sp) {
            token.addToBuilder(sb, start + sp, start + ep);
        }
        return sb;
    }
}
