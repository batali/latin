package latin.choices;

import latin.util.ImmutableIterable;

public interface Value<T> extends ImmutableIterable<T>, RecordAlts {
    T choose (Chooser chooser);
}