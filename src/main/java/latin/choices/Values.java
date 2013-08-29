package latin.choices;

import java.util.List;

public interface Values<T> extends List<T>, Value<T> {
    Object getId();
}