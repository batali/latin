package latin.forms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;

import latin.choices.AltsList;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.function.BiConsumer;

public class ModRule extends AltsList<Mod> implements Rule {

    public ModRule(PathId id, ImmutableList<Mod> mods) {
        super(id, mods);
    }

    public ModRule(PathId id, String cs) {
        this(id, split(cs));
    }

    public ModRule getRule() {
        return this;
    }

    public class SingleModIterator extends UnmodifiableIterator<String> {
        private UnmodifiableIterator<String> sit;
        public SingleModIterator(UnmodifiableIterator<String> sit) {
            this.sit = sit;
        }
        @Override
        public boolean hasNext() {
            return sit.hasNext();
        }
        @Override
        public String next() {
            return values.get(0).apply(sit.next());
        }
    }

    public class MultiModIterator extends UnmodifiableIterator<String> {
        private UnmodifiableIterator<String> sit;
        private String stemString;
        private int pos;

        public MultiModIterator(UnmodifiableIterator<String> sit) {
            super();
            this.sit = sit;
            setNextStem();
        }

        public boolean setNextStem() {
            pos = 0;
            if (sit.hasNext()) {
                stemString = sit.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean hasNext() {
            if (pos < values.size()) {
                return true;
            } else {
                return setNextStem();
            }
        }

        @Override
        public String next() {
            return values.get(pos++).apply(stemString);
        }
    }

    class Applied implements Form {
        final Form stem;
        public Applied(Form stem) {
            this.stem = stem;
        }

        @Override
        public UnmodifiableIterator<String> iterator() {
            if (size() == 1) {
                return new SingleModIterator(stem.iterator());
            } else {
                return new MultiModIterator(stem.iterator());
            }
        }

        @Override
        public void recordAlts(BiConsumer<Object, Integer> bic) {
            stem.recordAlts(bic);
            getRule().recordAlts(bic);
        }

        @Override
        public String choose(latin.choices.Chooser chooser) {
            return getRule().choose(chooser).apply(stem.choose(chooser));
        }

        @Override
        public String toString() {
            return Iterables.toString(this);
        }
    }

    @Override
    public Applied apply(Form stem) {
        return new Applied(stem);
    }

    public static ImmutableList<Mod> split(String cs) {
        ImmutableList.Builder<Mod> builder = ImmutableList.builder();
        for (String ms : Splitters.csplitter(cs)) {
            builder.add(Mod.parseMod(ms));
        }
        return builder.build();
    }

    public String toString() {
        return values.toString() + " (" + getId() + ")";
    }

    public static final ModRule noop = new ModRule(PathId.makeRoot(":"),
                                                    ImmutableList.of(Mod.noop));
    public static final ModRule unaccentLast = new ModRule(PathId.makeRoot(">"),
                                                       ImmutableList.of(Mod.unaccentLast));
    public static final ModRule accentLast = new ModRule(PathId.makeRoot("<"),
                                                         ImmutableList.of(Mod.accentLast));


}