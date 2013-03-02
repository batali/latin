
package latin.forms;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public class ListPhrase implements IPhrase {

    private List<Iterable<IForm>> iterables;

    public void addObject(Object o) {
        Forms.addToPhrases(iterables, o);
    }

    public ListPhrase(Object... objects) {
        this.iterables = Lists.newArrayList();
        for (Object o : objects) {
            addObject(o);
        }
    }

    public ListPhrase add(Object... objects) {
        for (Object o : objects) {
            addObject(o);
        }
        return this;
    }

    public ListPhrase clear() {
        this.iterables.clear();
        return this;
    }

    @Override
    public Iterator<IForm> iterator() {
        return new Forms.PhraseIterator(iterables);
    }
}