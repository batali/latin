
package latin.util;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.annotation.Nullable;

public class ImmutableSortedMap<K,V> extends AbstractMap<K,V> implements SortedMap<K,V> {

    public static class EntrySet<K,V> extends AbstractSet<Entry<K,V>> {
        final ImmutableList<Entry<K,V>> entries;
        final int startPosition;
        final int endPosition;
        public EntrySet(ImmutableList<Entry<K, V>> entries, int startPosition, int endPosition) {
            this.entries = entries;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
        public EntrySet(ImmutableList<Entry<K, V>> entries) {
            this(entries, 0, entries.size());
        }
        public int size() {
            return endPosition - startPosition;
        }
        public Entry<K,V> getEntry(int p) {
            return entries.get(p);
        }
        class PositionIterator implements Iterator<Entry<K,V>> {
            int position;
            public PositionIterator(int position) {
                this.position = position;
            }
            public boolean hasNext() {
                return position < endPosition;
            }
            public Entry<K,V> next() {
                return entries.get(position++);
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
        @Override
        public PositionIterator iterator() {
            return new PositionIterator(startPosition);
        }
        public EntrySet<K,V> makeEntrySet(int sp, int ep) {
            return new EntrySet<K,V>(entries, sp, ep);
        }
        Entry<K,V> getKeyEntry(K key, Comparator<K> keyComparator) {
            int p = keySearch(entries, key, keyComparator, startPosition, endPosition);
            return (p >= 0) ? entries.get(p) : null;
        }
        int startOffset(int d) {
            return startPosition + d;
        }
        int endOffset(int d) {
            return endPosition + d;
        }
    }

    EntrySet<K,V> entrySet;
    Comparator<K> keyComparator;
    public ImmutableSortedMap (EntrySet<K,V> entrySet,
                               Comparator<K> keyComparator) {
        this.entrySet = entrySet;
        this.keyComparator = keyComparator;
    }

    static <K,V> EntrySet<K,V> makeEntryList(Iterable<Entry<K,V>> ei, Ordering<K> keyOrdering) {
        Ordering<Entry<K,V>> eo = keyOrdering.onResultOf(new Function<Entry<K, V>, K>() {
            @Override
            public K apply(@Nullable Entry<K, V> e) {
                return e == null ? null : e.getKey();
            }
        });
        return new EntrySet<K,V>(eo.immutableSortedCopy(ei));
    }

    public ImmutableSortedMap (Iterable<Entry<K,V>> ei, Ordering<K> keyOrdering) {
        this(makeEntryList(ei, keyOrdering), (Comparator<K>) keyOrdering);
    }

    public ImmutableSortedMap (Map<K,V> m, Ordering<K> keyOrdering) {
        this(m.entrySet(), keyOrdering);
    }

    public Set<Entry<K,V>> entrySet() {
        return entrySet;
    }

    static <K,V> int keySearch (List<Entry<K,V>> entryList,
                                K key,
                                Comparator<K> keyComparator,
                                int lp,
                                int up) {
        while (lp < up) {
            int mp = (lp + up)/2;
            int d = keyComparator.compare(entryList.get(mp).getKey(), key);
            if (d < 0) {
                lp = mp + 1;
            }
            else if (d > 0) {
                up = mp;
            }
            else {
                return mp;
            }
        }
        return (-(lp) - 1);
    }

    int entrySearch(K key, int sp, int ep) {
        return keySearch(entrySet.entries, key, keyComparator, sp, ep);
    }

    int entrySearch(K key) {
        return entrySearch(key, entrySet.startPosition, entrySet.endPosition);
    }

    static int toPosition(int p) {
        return (p >= 0) ? p : (-(p) - 1);
    }

    public ImmutableSortedMap<K,V> headMap(K key) {
        return new ImmutableSortedMap<K,V>(entrySet.makeEntrySet(entrySet.startOffset(0),
                                                                 toPosition(entrySearch(key))),
                                           keyComparator);
    }

    public ImmutableSortedMap<K,V> tailMap(K key) {
        return new ImmutableSortedMap<K,V>(entrySet.makeEntrySet(toPosition(entrySearch(key)),
                                                                 entrySet.endOffset(0)),
                                           keyComparator);
    }

    public ImmutableSortedMap<K,V> subMap(K fk, K tk) {
        int ep = toPosition(entrySearch(tk));
        int sp = toPosition(entrySearch(fk, entrySet.startOffset(0), ep));
        return new ImmutableSortedMap<K,V>(entrySet.makeEntrySet(sp, ep), keyComparator);
    }

    @Override
    public Comparator<? super K> comparator() {
        return keyComparator;
    }

    @Override
    public K firstKey() {
        return entrySet.entries.get(entrySet.startPosition).getKey();
    }

    @Override
    public K lastKey() {
        return entrySet.entries.get(entrySet.endPosition-1).getKey();
    }

    public Entry<K,V> getEntry(K key) {
        int p = entrySearch(key);
        return (p >= 0) ? entrySet.getEntry(p) : null;
    }

    public V getValue(K key) {
        Entry<K,V> e = getEntry(key);
        return (e != null) ? e.getValue() : null;
    }

}






