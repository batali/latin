package latin.choices;

import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;

import latin.util.PathId;

public class AltsList<T> implements Alts<T>, PathId.Identified {

    protected final PathId id;
    protected final ImmutableList<T> values;

    public AltsList(PathId id, ImmutableList<T> values) {
        this.id = id;
        this.values = values;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public T get(int index) {
        return values.get(IntMath.mod(index, values.size()));
    }

    @Override
    public PathId getId() {
        return id;
    }

    @Override
    public PathId getPathId() { return id; }

    @Override
    public String toString() {
        return values.toString();
    }

}
