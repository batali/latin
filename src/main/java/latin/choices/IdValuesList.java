
package latin.choices;

import java.util.List;

public class IdValuesList<T> extends ValuesList<T> {

    final Object id;
    public IdValuesList(Object id, List<T> values) {
        super(values);
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }
}