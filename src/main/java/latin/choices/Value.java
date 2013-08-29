package latin.choices;

public interface Value<T> {
    T get(Alts.Chooser chooser);
}