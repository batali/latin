package latin.forms;

public interface IFormBuilder extends IForm {
    public boolean isEmpty();
    public IFormBuilder removeLast(int n);
    public IFormBuilder add(char c);
    public IFormBuilder add(String s);
    public IFormBuilder setCharAt(int p, char c);
    public String getForm();
}


