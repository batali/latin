
package latin.choices;

import latin.forms.Form;
import latin.forms.FormRule;
import latin.forms.ModRule;
import latin.forms.Rule;
import latin.forms.Rulef;
import latin.forms.Suffix;
import latin.util.PathId;

public enum Conjugation {

    first ("ā", "-", ">",   ":",  "r",  ":", "-ē"),
    second("ē", ">", ">",   ":",  "r",  ":", ">ā"),
    thirdc("i", "-", "-u", "-e",  "-", "-ē", "-ā"),
    thirdi("i", ">", ">u", "-e",  "-", ">ē", ">ā"),
    fourth("ī", ">", ">u",  ":",  "r", ">ē", ">ā");

    public final String stemEnding;
    PathId pathId;
    public final Rulef fsi;
    public final Rulef tpl;
    public final Rulef ai;
    public final Rulef pi;
    public final Rulef strong;
    public final Rulef subj;
    public final Rule fsiRule;
    public final Rule tplRule;
    public final Rule aiRule;
    public final Rule piRule;
    public final Rule strongRule;
    public final Rule subjRule;

    Conjugation(String stemEnding,
                String fsiString,
                String tplString,
                String aiString,
                String piString,
                String strongString,
                String subjString) {
        this.pathId = PathId.makeRoot(this);
        this.stemEnding = stemEnding;
        this.fsi = FormRule.parseRule(name(), "fsi", fsiString);
        this.fsiRule = new ModRule(pathId.makeChild("fsi"), fsiString);
        this.tpl = FormRule.parseRule(name(), "tpl", tplString);
        this.tplRule = new ModRule(pathId.makeChild("tpl"), tplString);
        this.ai = FormRule.parseRule(name(), "ai", aiString);
        this.aiRule = new ModRule(pathId.makeChild("ai"), aiString);
        this.pi = FormRule.parseRule(name(), "pi", piString);
        this.piRule = new ModRule(pathId.makeChild("pi"), piString);
        this.strong = FormRule.parseRule(name(), "strong", strongString);
        this.strongRule = new ModRule(pathId.makeChild("strong"), strongString);
        this.subj = FormRule.parseRule(name(), "subj", subjString);
        this.subjRule = new ModRule(pathId.makeChild("subj"), subjString);
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

    public Rule getModRule(String name) {
        if (name.equals("strong")) {
            return strongRule;
        }
        else if (name.equals("subj")) {
            return subjRule;
        }
        else if (name.equals("pi")) {
            return piRule;
        }
        else if (name.endsWith("ai")) {
            return aiRule;
        }
        else {
            throw new IllegalArgumentException("Unknown Mod rule " + name);
        }
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

    public Rule getIndPreRule(PersonNumber personNumber, Voice voice) {
        switch(personNumber) {
            case FpSi:
                return fsiRule;
            case TpSi:
                return voice.isActive() ? ModRule.unaccentLast : ModRule.noop;
            case TpPl:
                return tplRule;
            case SpSi:
                return voice.isPassive() ? aiRule : ModRule.noop;
            default:
                return ModRule.noop;
        }
    }

    public Form applyIndPreRule(Form stem, PersonNumber personNumber, Voice voice) {
        Rule rule = getIndPreRule(personNumber, voice);
        return (rule == null) ? stem : rule.apply(stem);
    }


}


