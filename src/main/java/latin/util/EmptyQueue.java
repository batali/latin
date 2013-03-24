
package latin.util;

import com.google.common.collect.Iterators;

import java.util.AbstractQueue;
import java.util.Iterator;

public class EmptyQueue<T> extends AbstractQueue<T> {

    public EmptyQueue() {
        super();
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.emptyIterator();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean offer(T t) {
        return false;
    }

    @Override
    public T poll() {
        return null;
    }

    @Override
    public T peek() {
        return null;
    }
}