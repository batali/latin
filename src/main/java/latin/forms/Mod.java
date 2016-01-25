package latin.forms;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import latin.choices.Chooser;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Mod implements java.util.function.Function<String,String>,
                                     com.google.common.base.Function<String,String> {

    public abstract String getSpec();

    @Override
    public String toString() {
        return getSpec();
    }

    public static Mod noop = new Mod() {
        @Override
        public String getSpec() {
            return ":";
        }

        public String apply(String s) {
            return s;
        }
    };

    public static class Ending extends Mod {

        public final String ending;

        public Ending(String ending) {
            this.ending = ending;
        }

        @Override
        public String apply(String s) {
            return s + ending;
        }

        @Override
        public String getSpec() {
            return ending;
        }
    }

    public static String butLastString(String s, int n){
        return s.substring(0, s.length() - n);
    }

    public static class ButLast extends Mod {

        public final int n;

        public ButLast(int n) {
            this.n = n;
        }

        @Override
        public String getSpec() {
            return Strings.padEnd("", n, '-');
        }

        @Override
        public String apply(String s) {
            return butLastString(s, n);
        }
    }

    public static ButLast butOne = new ButLast(1);

    public static Mod accentLast = new Mod() {
        @Override
        public String apply(String s) {
            int n = s.length();
            char uc = s.charAt(n - 1);
            char ac = Suffix.accented(uc);
            if (uc == ac) {
                return s;
            } else {
                return s.substring(0, n - 1) + ac;
            }
        }

        @Override
        public String getSpec() {
            return "<";
        }
    };

    public static Mod unaccentLast = new Mod() {
        @Override
        public String apply(String s) {
            int n = s.length();
            char ac = s.charAt(n - 1);
            char uc = Suffix.unaccented(ac);
            if (ac == uc) {
                return s;
            } else {
                return s.substring(0, n - 1) + uc;
            }
        }

        @Override
        public String getSpec() {
            return ">";
        }
    };

    public static Mod duplicateLast = new Mod() {
        @Override
        public String apply(String s) {
            int n = s.length();
            return s + s.charAt(n-1);
        }

        @Override
        public String getSpec() { return "+"; }
    };

    public static class ModSeq extends Mod {

        public final ImmutableList<Mod> mods;

        public ModSeq(List<Mod> mods) {
            this.mods = ImmutableList.copyOf(mods);
        }

        @Override
        public String apply(String s) {
            for (Mod m : mods) {
                s = m.apply(s);
            }
            return s;
        }

        @Override
        public String getSpec() {
            return mods.stream().map(Mod::getSpec).collect(Collectors.joining());
        }
    }

    public static Mod parseMod(String ms) {
        List<Mod> mods = Lists.newArrayList();
        int p = 0;
        int e = ms.length();
        while (p < e) {
            char c = ms.charAt(p);
            if (c == '>') {
                mods.add(unaccentLast);
                p += 1;
            } else if (c == '<') {
                mods.add(accentLast);
                p += 1;
            } else if (c == '-') {
                int n = 1;
                while (p + n < e && ms.charAt(p + n) == '-') {
                    n += 1;
                }
                if (n == 1) {
                    mods.add(butOne);
                } else {
                    mods.add(new ButLast(n));
                }
                p += n;
            } else if (c == ':') {
                mods.add(noop);
                p += 1;
            } else if (c == '+') {
                mods.add(duplicateLast);
                p += 1;
            } else {
                mods.add(new Ending(ms.substring(p)));
                p = e;
            }
        }
        if (mods.isEmpty()) {
            return noop;
        } else if (mods.size() == 1) {
            return mods.get(0);
        } else {
            return new ModSeq(mods);
        }
    }

    public static class AppliedIterator extends UnmodifiableIterator<String> {
        final Iterator<String> sit;
        final Function<String,String> mod;
        public AppliedIterator (Iterator<String> sit, Function<String,String> mod) {
            this.sit = sit;
            this.mod = mod;
        }
        @Override
        public boolean hasNext() {
            return sit.hasNext();
        }
        @Override
        public String next() {
            return mod.apply(sit.next());
        }
    }

    public static class Applied implements Form {
        final Form stem;
        final Function<String,String> mod;
        public Applied(Form stem, Function<String,String> mod) {
            this.stem = stem;
            this.mod = mod;
        }

        @Override
        public String choose(Chooser chooser) {
            return mod.apply(stem.choose(chooser));
        }

        @Override
        public UnmodifiableIterator<String> iterator() {
            return new AppliedIterator(stem.iterator(), mod);
        }

        @Override
        public void recordAlts(BiConsumer<Object, Integer> bic) {
            stem.recordAlts(bic);
        }
    }

}

