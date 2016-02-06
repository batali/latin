package latin.choices;

import com.google.common.base.Preconditions;

import latin.forms.Mod;
import latin.forms.Suffix;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.List;

public class English {

    public static final PathId pathId = PathId.makeRoot("English");

    interface NounEntry {
        String getSingular();
        String getPlural();
    }

    public static final Mod sMod = Mod.ending("s");
    public static final Mod esMod = Mod.ending("es");
    public static final Mod cyMod = Mod.parseMod("-ies");

    public static final Suffix.EndMatcher sibilintMatcher = Suffix.endMatcher("s,sh,ch,x");
    public static final Suffix.EndMatcher cyMatcher = Suffix.endMatcher("Cy");

    public static String regularPlural(String base) {
        if (sibilintMatcher.test(base)) {
            return esMod.apply(base);
        }
        else if (cyMatcher.test(base)) {
            return cyMod.apply(base);
        }
        else {
            return sMod.apply(base);
        }
    }

    static abstract class AbstractNounEntry implements NounEntry {
        public final String singular;
        public AbstractNounEntry(String singular) {
            this.singular = singular;
        }
        @Override
        public String getSingular() {
            return singular;
        }
    }

    public static class RegularNounEntry extends AbstractNounEntry {
        public RegularNounEntry(String singular) {
            super(singular);
        }
        @Override
        public String getPlural() {
            return regularPlural(singular);
        }
    }

    public static class UnchangedNounEntry extends AbstractNounEntry {
        public UnchangedNounEntry(String singular) {
            super(singular);
        }
        @Override
        public String getPlural() {
            return singular;
        }
    }

    public static class IrregularNounEntry extends AbstractNounEntry {
        public final String plural;
        public IrregularNounEntry(String singular, String plural) {
            super(singular);
            this.plural = plural;
        }
        @Override
        public String getPlural() {
            return plural;
        }
    }

    public static NounEntry makeNounEntry(String singular, String plural) {
        if (plural == null || plural.equals(regularPlural(singular))) {
            return new RegularNounEntry(singular);
        }
        else if (singular.equals(plural)) {
            return new UnchangedNounEntry(singular);
        }
        else {
            return new IrregularNounEntry(singular, plural);
        }
    }

    public static NounEntry parseNounEntry(String ss) {
        List<String> sl = Splitters.csplit(ss);
        int l = sl.size();
        Preconditions.checkArgument(l > 0 && l <= 2);
        if (l == 1) {
            return makeNounEntry(sl.get(0), null);
        } else {
            return makeNounEntry(sl.get(0), sl.get(1));
        }
    }







}