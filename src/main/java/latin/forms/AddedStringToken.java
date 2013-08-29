
package latin.forms;

public class AddedStringToken extends AbstractToken {

    final Token head;
    final String tail;

    public AddedStringToken(Token head, String tail) {
        this.head = head;
        this.tail = tail;
    }

    public int length() {
        return head.length() + tail.length();
    }

    @Override
    public StringBuilder addToBuilder(StringBuilder stringBuilder, int sp, int ep) {
        int hl = head.length();
        if (sp < hl) {
            head.addToBuilder(stringBuilder, sp, Math.min(hl, ep));
        }
        if (hl < ep) {
            stringBuilder.append(tail, Math.max(0, sp - hl), ep - hl);
        }
        return stringBuilder;
    }

    @Override
    public char charAt(int i) {
        int hl = head.length();
        if (i < hl) {
            return head.charAt(i);
        }
        else {
            return tail.charAt(i - hl);
        }
    }

}