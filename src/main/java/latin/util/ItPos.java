
package latin.util;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class ItPos<T> implements Iterator<T> {
    private Iterator<T> iterator;
    private AtomicInteger counter;
    private boolean firstp;
    public ItPos (Iterator<T>iterator, AtomicInteger counter) {
        this.iterator = iterator;
        this.counter = counter;
        this.firstp = true;
    }
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    @Override
    public T next() {
        if (firstp) {
            firstp = false;
        }
        else {
            counter.incrementAndGet();
        }
        return iterator.next();
    }

    public void remove() {
    }

    public static class Wrapper<T> implements Iterable<T> {
        private Iterable<T> wrapped;
        private AtomicInteger counter;
        public Wrapper(Iterable<T> wrapped, AtomicInteger counter) {
            this.wrapped = wrapped;
            this.counter = counter;
        }
        public Iterator<T> iterator() {
            return new ItPos<T>(wrapped.iterator(), counter);
        }
    }

    public static <T> Wrapper<T> wrap(Iterable<T> w, AtomicInteger counter) {
        return new Wrapper<T>(w, counter);
    }
}