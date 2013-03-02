
package latin.forms;

public class FormBuilder implements IFormBuilder {

    private StringBuilder stringBuilder;

    public FormBuilder() {
        this.stringBuilder = new StringBuilder();
    }

    public FormBuilder(CharSequence cs) {
        this.stringBuilder = new StringBuilder(cs);
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public FormBuilder removeLast(int n) {
        stringBuilder.setLength(stringBuilder.length()-n);
        return this;
    }

    @Override
    public FormBuilder add(char c) {
        stringBuilder.append(c);
        return this;
    }

    @Override
    public FormBuilder add(String s) {
        stringBuilder.append(s);
        return this;
    }

    @Override
    public int length() {
        return stringBuilder.length();
    }

    @Override
    public char charAt(int i) {
        return stringBuilder.charAt(i);
    }

    @Override
    public CharSequence subSequence(int s, int e) {
        return stringBuilder.subSequence(s, e);
    }

    @Override
    public FormBuilder setCharAt(int p, char c) {
        stringBuilder.setCharAt(p, c);
        return this;
    }

    @Override
    public String getForm() {
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return getForm();
    }

    @Override
    public boolean spaceBefore() {
        return true;
    }

    @Override
    public boolean spaceAfter() {
        return true;
    }

    @Override
    public String sequenceString(IForm nxt) {
        return stringBuilder.toString();
    }
}