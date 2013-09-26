
package latin.forms;

public interface Token extends CharSequence {
    public boolean isEmpty();
    public Token subSequence(int s, int e);
    public Token butLast(int n);
    public Token butFirst(int n);
    public void appendTo(Appendable appendable);
    public void appendTo(Appendable appendable, int s, int e);
    public char endChar(int fromEnd);
    public int endOffset(int fromEnd);
}