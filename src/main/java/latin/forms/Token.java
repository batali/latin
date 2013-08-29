
package latin.forms;

public interface Token extends CharSequence {
    public boolean isEmpty();
    public Token subSequence(int s, int e);
    public StringBuilder addToBuilder(StringBuilder stringBuilder, int sp, int ep);
    public StringBuilder addToBuilder(StringBuilder stringBuilder);
    public Character endChar(int fromEnd);
    public int endOffset(int fromEnd);
}