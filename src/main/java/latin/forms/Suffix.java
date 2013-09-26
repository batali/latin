
package latin.forms;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;

import latin.choices.Alts;
import latin.choices.CollectAlts;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class Suffix {

    private Suffix() {
    }

    public static final String lowerUnaccentedVowels = "aeiou";
    public static final String lowerAccentedVowels = "āēīōū";

    public interface Transformer {
        public char transform(char c);
    }

    public static boolean isVowel(char c) {
        char lc = Character.toLowerCase(c);
        return lowerUnaccentedVowels.indexOf(lc) >= 0 || lowerAccentedVowels.indexOf(lc) >= 0;
    }

    public static boolean isConsonant(char c) {
        return Character.isLetter(c) && !isVowel(c);
    }

    public static boolean isAccented(char c) {
        return lowerAccentedVowels.indexOf(Character.toLowerCase(c)) >= 0;
    }

    public static char accented(char c) {
        boolean lp = Character.isLowerCase(c);
        char lc = lp ? c : Character.toLowerCase(c);
        int p = lowerUnaccentedVowels.indexOf(lc);
        if (p < 0) {
            return c;
        }
        else {
            char lac = lowerAccentedVowels.charAt(p);
            return lp ? lac : Character.toUpperCase(lac);
        }
    }

    public static char unaccented(char c) {
        boolean lp = Character.isLowerCase(c);
        char lc = lp ? c : Character.toLowerCase(c);
        int p = lowerAccentedVowels.indexOf(lc);
        if (p < 0) {
            return c;
        }
        else {
            char lac = lowerUnaccentedVowels.charAt(p);
            return lp ? lac : Character.toUpperCase(lac);
        }
    }

    public static String unaccentString(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(charSequence);
        int s = sb.length();
        for (int i = 0; i < s; i++) {
            char c = sb.charAt(i);
            if (c == ' ') {
                sb.setCharAt(i, '_');
            }
            else if (isAccented(c)) {
                sb.setCharAt(i, unaccented(c));
            }
        }
        return sb.toString();
    }

    public static Iterable<String> ssplitter(String str) {
        return Splitter.on(CharMatcher.BREAKING_WHITESPACE)
                .omitEmptyStrings()
                .split(str);
    }

    public static List<String> ssplit(String str) {
        return Lists.newArrayList(ssplitter(str));
    }

    public static String makeFormString(String fstr) {
        return CharMatcher.is('_').collapseFrom(fstr.trim(), ' ');
        //return fstr.trim().replace('_', ' ');
    }

    public static final Function<String,String> toSameString = Functions.identity();

    public static final Function<String,String> toFormString = new Function<String, String>() {
        @Override
        public String apply(@Nullable String s) {
            Preconditions.checkNotNull(s);
            return makeFormString(s);
        }
    };

    public static <T> Iterable<T> csplitter(String str, Function<? super String,T> tfunc) {
        str = str.trim();
        if (str.isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return Iterables.transform(Splitter.on(',').trimResults().split(str), tfunc);
        }
    }

    public static <T> List<T> csplit(String cstr, Function<? super String,T> tfunc) {
        return ImmutableList.copyOf(csplitter(cstr, tfunc));
    }

    public static List<String> csplit(String cstr) {
        return csplit(cstr, toFormString);
    }

    public static boolean charMatch(char cc, char mc) {
        if (Character.toLowerCase(cc) == mc) {
            return true;
        }
        else if (mc == '*') {
            return true;
        }
        else if (mc == 'V') {
            return isVowel(cc);
        }
        else if (mc == 'C') {
            return isConsonant(cc);
        }
        else {
            return false;
        }
    }

    public static boolean seqMatches(CharSequence cs, String ms, int sp) {
        if (cs == null) {
            return false;
        }
        int mn = ms.length();
        int cn = cs.length();
        if (sp < 0 || sp + mn > cn) {
            return false;
        }
        for (int i = 0; i < mn; i++) {
            if (!charMatch(cs.charAt(sp + i), ms.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean startMatches(CharSequence cs, String ms) {
        return seqMatches(cs, ms, 0);
    }

    public static boolean endMatches(CharSequence cs, String ms) {
        return seqMatches(cs, ms, cs.length() - ms.length());
    }

    public static String selectEndMatcher(CharSequence cs, List<String> msl) {
        for (String ms : msl) {
            if (endMatches(cs, ms)) {
                return ms;
            }
        }
        return null;
    }

    public static interface StringTest {
        public boolean test(CharSequence charSequence);
    }

    public static class EndMatcher implements StringTest {
        public final ImmutableList<String> matchStrings;
        public EndMatcher(ImmutableList<String> matchStrings) {
            this.matchStrings = matchStrings;
        }

        public boolean test(CharSequence charSequence) {
            for (String ms : matchStrings) {
                if (endMatches(charSequence, ms)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static EndMatcher endMatcher (String mss) {
        return new EndMatcher(ImmutableList.copyOf(csplitter(mss, toSameString)));
    }

    public static String removeEnding(String s, String e) {
        return s.endsWith(e) ? s.substring(0, s.length() - e.length()) : null;
    }

    public static boolean checkFormString(String fs) {
        int s = fs.length();
        int lc = -1;
        for (int i = 0; i < s; i++) {
            char c = fs.charAt(i);
            if (Character.isLetter(c)) {
                lc = i;
            }
            else if (Character.isWhitespace(c)) {
                if (lc < 0 || i - lc > 1) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return lc+1 == s;
    }

    public static List<String> checkFormStrings(List<String> strings) {
        for (String fs : strings) {
            if (!checkFormString(fs)) {
                throw new IllegalArgumentException("bad form string " + fs);
            }
        }
        return strings;
    }

    static class AltsForm implements Formf {

        public final Object spec;
        public final ImmutableList<String> alts;

        public AltsForm(Object spec, List<String> strings) {
            this.spec = spec;
            this.alts = ImmutableList.copyOf(checkFormStrings(strings));
        }

        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            String s = Alts.chooseElement(alts, this, chooser);
            if (s != null) {
                formBuilder.add(s);
                return true;
            }
            else {
                return false;
            }
        }

        public IFormBuilder apply(Alts.Chooser chooser) {
            String s = Alts.chooseElement(alts, this, chooser);
            if (s != null) {
                FormBuilder formBuilder = new FormBuilder();
                formBuilder.add(s);
                return formBuilder;
            }
            else {
                return null;
            }
        }

        public String toString() {
            return spec.toString() + ":" + alts.toString();
        }

    }

    public static Formf makeFormf(Object id, List<String> strings) {
        return new AltsForm(id, strings);
    }

    public static Formf makeFormf(String prefix, Object key, List<String> strings) {
        return makeFormf(prefix + "." + key.toString(), strings);
    }

    public static Formf makeFormf(Object id, String fs) {
        return makeFormf(id, Suffix.csplit(fs));
    }

    public static Formf makeFormf(String prefix, Object key, String fs) {
        return makeFormf(prefix, key, Suffix.csplit(fs));
    }

    public static List<String> getStrings(Formf formf) {
        List<String> strings = Lists.newArrayList();
        CollectAlts collector = new CollectAlts();
        do {
            Forms.addForm(formf.apply(collector), strings);
        }
        while (collector.incrementPositions());
        return strings;
    }

    public static Pair<String,String> esplitItem(String s) {
        int p = s.indexOf('=');
        if (p >= 0) {
            return Pair.of(s.substring(0, p), s.substring(p+1, s.length()));
        }
        else {
            return Pair.of(s,"");
        }
    }

    public static final Function<String,Pair<String,String>> toEsplitItem = new Function<String, Pair<String, String>>() {
        @Override
        public Pair<String, String> apply(@Nullable String s) {
            Preconditions.checkNotNull(s);
            return esplitItem(s);
        }
    };

    public static Iterable<Pair<String,String>> esplitter(String s) {
        return Iterables.transform(ssplitter(s), toEsplitItem);
    }
}
