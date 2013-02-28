
package latin.setter;

public interface UseSlotHandler<T> {
    public T useBooleanSlot(BooleanSlot booleanSlot);
    public T useBinarySlot(BinarySlot binarySlot);
    public T useValueSlot(ValueSlot valueSlot);
}