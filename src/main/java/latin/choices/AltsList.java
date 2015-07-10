package latin.choices;

import com.google.common.collect.ImmutableList;

public class AltsList<T> implements Alts<T> {

    protected final Object id;
    protected final ImmutableList<T> values;

    public AltsList(Object id, ImmutableList<T> values) {
        this.id = id;
        this.values = values;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public T get(int index) {
        return values.get(index);
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public String toString() {
        return values.toString();
    }

}
