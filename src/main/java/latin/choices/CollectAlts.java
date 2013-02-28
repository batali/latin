
package latin.choices;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class CollectAlts implements Alts.Chooser {

    static class Entry {
        public final Object id;
        public final int size;
        private int position;
        public Entry(Object id, int size) {
            this.id = id;
            this.size = size;
            this.position = 0;
        }
        public int getPosition() {
            return position;
        }
        public boolean incrementPosition() {
            if (++position < size) {
                return true;
            }
            else {
                position = 0;
                return false;
            }
        }
        public String toString() {
            return String.format("%s:%d/%d", id.toString(), position, size);
        }
    }

    private List<Entry> entries;

    public CollectAlts() {
        this.entries = Lists.newArrayList();
    }

    private Entry getEntry(Object id, int n) {
        for (Entry entry : entries) {
            if (entry.id.equals(id)) {
                Preconditions.checkState(n == entry.size);
                return entry;
            }
        }
        Entry newEntry = new Entry(id, n);
        entries.add(newEntry);
        return newEntry;
    }

    @Override
    public int getAltsIndex(Object id, int n) {
        return getEntry(id, n).getPosition();
    }

    public boolean incrementPositions() {
        for (Entry entry : entries) {
            if (entry.incrementPosition()) {
                return true;
            }
        }
        return false;
    }

    public List<Object> getIds() {
        return Lists.transform(entries, new Function<Entry, Object>() {
            @Override
            public Object apply(@Nullable Entry e) {
                Preconditions.checkNotNull(e);
                return e.id;
            }
        });
    }

}
