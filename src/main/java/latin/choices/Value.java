package latin.choices;

public interface Value<T> {
    T choose(Alts.Chooser chooser);
}