package latin.choices;

public interface Chooser {
    Integer get(Object key);

    interface Choose<T> {
        T choose(Chooser chooser);
    }
}