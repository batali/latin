
package latin.choices;

import java.util.AbstractList;
import java.util.List;

public abstract class ValuesList<T> extends AbstractList<T> implements Values<T>, Value<T> {

    List<T> values;

    public ValuesList(List<T> values) {
        this.values = values;
    }

    @Override
    public T get(int i) {
        return values.get(i);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public T choose(Alts.Chooser chooser) {
        return Alts.chooseElement(this, chooser);
    }

    @Override
    public boolean add(T t) {
        return values.add(t);
    }

}