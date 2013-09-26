
package latin.forms;

public class PairToken extends AbstractToken {

    final Token head;
    final Token tail;

    public PairToken(Token head, Token tail) {
        this.head = head;
        this.tail = tail;
    }

    public int length() {
        return head.length() + tail.length();
    }

    public char charAt(int p) {
        int hl = head.length();
        if (p < hl) {
            return head.charAt(p);
        }
        else {
            return tail.charAt(p - hl);
        }
    }

    Token subToken(int start, int end) {
        int hl = head.length();
        if (end <= hl) {
            return head.subSequence(start, end);
        }
        else if (hl <= start) {
            return tail.subSequence(start - hl, end - hl);
        }
        else {
            return new SubToken(this, start, end);
        }
    }

    public void appendTo(Appendable appendable, int s, int e) {
        int hl = head.length();
        if (s < hl) {
            head.appendTo(appendable, s, Math.min(hl, e));
        }
        if (e > hl) {
            tail.appendTo(appendable, Math.max(s - hl, 0), e - hl);
        }
    }
}
