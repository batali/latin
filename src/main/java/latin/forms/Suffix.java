
package latin.forms;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;

import latin.util.Splitters;

import java.util.List;

public class Suffix {

    private Suffix() {
    }

    public static final String lowerUnaccentedVowels = "aeiou";
    public static final String lowerAccentedVowels = "āēīōū";

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
        return new EndMatcher(Splitters.csplit(mss));
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

}
