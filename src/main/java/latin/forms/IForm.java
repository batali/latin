package latin.forms;

public interface IForm extends CharSequence {
    public boolean spaceBefore();
    public boolean spaceAfter();
    public String sequenceString(IForm nxt);

}