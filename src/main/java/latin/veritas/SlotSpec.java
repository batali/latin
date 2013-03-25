
package latin.veritas;

public interface SlotSpec {
    String getPathString();
    int getChoiceCount();
    boolean isBoolean();
    String getChoiceName(int p);
    int getChoiceIndex(String choiceName);
}