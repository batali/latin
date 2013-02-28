
package latin.choices;

import latin.forms.FormRule;
import latin.forms.RuleMapBuilder;
import latin.forms.Rulef;
import latin.forms.Suffix;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;

public enum Conjugation {

    first ("ā", "-", ">",   ":",  "r",  ":", "-ē"),
    second("ē", ">", ">",   ":",  "r",  ":", ">ā"),
    thirdc("i", "-", "-u", "-e",  "-", "-ē", "-ā"),
    thirdi("i", ">", ">u", "-e",  "-", ">ē", ">ā"),
    fourth("ī", ">", ">u",  ":",  "r", ">ē", ">ā");

    public final String stemEnding;
    public final Rulef fsi;
    public final Rulef tpl;
    public final Rulef ai;
    public final Rulef pi;
    public final Rulef strong;
    public final Rulef subj;

    Conjugation(String stemEnding,
                String fsiString,
                String tplString,
                String aiString,
                String piString,
                String strongString,
                String subjString) {
        this.stemEnding = stemEnding;
        this.fsi = FormRule.parseRule(name(), "fsi", fsiString);
        this.tpl = FormRule.parseRule(name(), "tpl", tplString);
        this.ai = FormRule.parseRule(name(), "ai", aiString);
        this.pi = FormRule.parseRule(name(), "pi", piString);
        this.strong = FormRule.parseRule(name(), "strong", strongString);
        this.subj = FormRule.parseRule(name(), "subj", subjString);
    }

    public boolean longStem() {
        return Suffix.isAccented(stemEnding.charAt(0));
    }

    public boolean hasi() {
        return Suffix.unaccented(stemEnding.charAt(0))=='i';
    }

    public boolean shorti() {
        return stemEnding.charAt(0)=='i';
    }

    public Rulef ipmod(PersonNumber personNumber, Voice voice) {
        switch (personNumber) {
            case FpSi:
                return fsi;
            case TpSi:
                return voice.isActive() ? FormRule.shortenLast : null;
            case TpPl:
                return tpl;
            case SpSi:
                return voice.isPassive() ? ai : null;
            default:
                return null;
        }
    }

    public static Pair<EnumMap<PersonNumber,Rulef>,EnumMap<PersonNumber,Rulef>> pendingsPair =
            Pair.of(
                    new RuleMapBuilder<PersonNumber>(PersonNumber.class, "act.pe")
                            .add("SpSi", "s")
                            .add("TpSi", "t")
                            .add("FpPl", "mus")
                            .add("SpPl", "tis")
                            .add("TpPl", "nt").enumMap,
                    new RuleMapBuilder<PersonNumber>(PersonNumber.class, "pas.pe")
                            .add("SpSi", "ris")
                            .add("TpSi", "tur")
                            .add("FpPl", "mur")
                            .add("SpPl", "minī")
                            .add("TpPl", "ntur").enumMap);

    public static Pair<Rulef,Rulef> piFpSiPair =
            Pair.of(FormRule.parseRule("pi.act", PersonNumber.FpSi, "ō"),
                    FormRule.parseRule("pi.pas", PersonNumber.FpSi, "or"));

    public static Pair<Rulef,Rulef> regFpSiPair =
            Pair.of(FormRule.parseRule("reg.act", PersonNumber.FpSi, "m"),
                    FormRule.parseRule("reg.pas", PersonNumber.FpSi, "r"));

    public static Rulef endingRule(PersonNumber personNumber, Voice voice,
                                   Pair<Rulef,Rulef> fpSiPair) {
        if (personNumber.equals(PersonNumber.FpSi)) {
            return voice.select(fpSiPair);
        }
        else {
            return voice.select(pendingsPair).get(personNumber);
        }
    }

}


