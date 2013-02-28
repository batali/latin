
package latin.forms;

import com.google.common.base.Preconditions;
import latin.choices.Alts;
import latin.choices.Completeness;
import latin.choices.PersonNumber;
import latin.choices.Time;
import latin.choices.Voice;

import java.util.EnumMap;
import java.util.List;

public class English {

    private English() {
    }

    public static final FormRule S = FormRule.makeFormRule("s");
    public static final FormRule ES = FormRule.makeFormRule("es");
    public static final FormRule IES = FormRule.makeFormRule("-ies");
    public static final FormRule ED = FormRule.makeFormRule("ed");
    public static final FormRule D = FormRule.makeFormRule("d");
    public static final FormRule IED = FormRule.makeFormRule("-ied");
    public static final FormRule N = FormRule.makeFormRule("n");
    public static final FormRule EN = FormRule.makeFormRule("en");
    public static final FormRule ING = FormRule.makeFormRule("ing");
    public static final FormRule EING = FormRule.makeFormRule("-ing");
    public static final FormRule PED = FormRule.makeFormRule("+ed");
    public static final FormRule PING = FormRule.makeFormRule("+ing");
    public static final FormRule POSS = new FormRule.StringFormRule("'s");

    public static final Suffix.EndMatcher sibilintMatcher = Suffix.endMatcher("s,sh,ch,x");
    public static final Suffix.EndMatcher cyMatcher = Suffix.endMatcher("Cy");

    public static final Suffix.StringTest dupMatcher = new Suffix.StringTest() {
        private Suffix.EndMatcher pm = Suffix.endMatcher("CVC");
        private Suffix.EndMatcher nm = Suffix.endMatcher("w,y");
        @Override
        public boolean test(CharSequence charSequence) {
            return pm.test(charSequence) && !nm.test(charSequence);
        }
    };

    public static final Suffix.EndMatcher eMatcher = Suffix.endMatcher("Ce");

    public static FormRule pluralRule (CharSequence base) {
        if (sibilintMatcher.test(base)) {
            return ES;
        }
        if (cyMatcher.test(base)) {
            return IES;
        }
        else {
            return S;
        }
    }

    public static FormRule tpSiRule (CharSequence base) {
       return pluralRule(base);
    }

    public static FormRule pastRule (CharSequence base) {
        if (cyMatcher.test(base)) {
            return IED;
        }
        if (dupMatcher.test(base)) {
            return PED;
        }
        if (eMatcher.test(base)) {
            return D;
        }
        return ED;
    }

    public static FormRule progRule (CharSequence base) {
        if (dupMatcher.test(base)) {
            return PING;
        }
        if (eMatcher.test(base)) {
            return EING;
        }
        else {
            return ING;
        }
    }

    public static enum NounKey {
        Sing,
        Plur,
        Poss;
    }

    public static enum VerbKey {
        Base,
        TpSi,
        Past,
        Part,
        Prog;
    }

    public static EnumMap<NounKey,Rulef> defaultNounRules = new RuleMapBuilder<NounKey>(NounKey.class, "default")
            .add(NounKey.Sing, FormRule.noopRule)
            .add(NounKey.Plur, S)
            .add(NounKey.Poss, POSS)
            .enumMap;


    public static class NounForms implements Form.Forms<NounKey> {

        public final EnumMap<NounKey, Formf> stored;
        public final EnumMap<NounKey, Rulef> rules;

        public NounForms(EnumMap<NounKey,Formf> stored,
                         EnumMap<NounKey,Rulef> rules) {
            this.stored = stored;
            this.rules = rules;
        }

        public boolean applyRule(NounKey key, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf si = stored.get(NounKey.Sing);
            if (si == null || !si.apply(formBuilder, chooser)) {
                return false;
            }
            Rulef rule = rules.get(key);
            if (rule == null) {
                switch(key) {
                    case Sing: rule = FormRule.noopRule;
                        break;
                    case Plur: rule = pluralRule(formBuilder);
                        break;
                    case Poss: rule = POSS;
                        break;
                }
            }
            return rule.apply(formBuilder, chooser);
        }

        public boolean getForm(NounKey key, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf sf = stored.get(key);
            if (sf != null) {
                return sf.apply(formBuilder, chooser);
            }
            else if (key.equals(NounKey.Sing)) {
                return false;
            }
            else {
                return applyRule(key, formBuilder, chooser);
            }
        }

        public static class Builder {
            String id;
            EnumMap<NounKey,Formf> stored;
            EnumMap<NounKey,Rulef> rules;
            public Builder (String ss) {
                this.id = ss;
                this.stored = new EnumMap<NounKey, Formf>(NounKey.class);
                this.rules = new EnumMap<NounKey, Rulef>(NounKey.class);
                putForm(NounKey.Sing, ss);
            }
            public Builder putForm(NounKey key, String ss) {
                stored.put(key, Suffix.makeFormf(id, key, ss));
                return this;
            }
            public Builder putForm(String ks, String ss) {
                return putForm(NounKey.valueOf(ks), ss);
            }
            public Builder putRule(NounKey key, Rulef rule) {
                rules.put(key, rule);
                return this;
            }
            public NounForms make () {
                return new NounForms(stored, rules);
            }
        }
    }

    public static NounForms.Builder nounFormsBuilder (String si) {
        return new NounForms.Builder(si);
    }

    public static NounForms nounForms(String bs) {
        List<String> sl = Suffix.ssplit(bs);
        Preconditions.checkState(!sl.isEmpty());
        NounForms.Builder b = nounFormsBuilder(sl.get(0));
        if (sl.size() > 1) {
            b.putForm(NounKey.Plur, sl.get(1));
        }
        return b.make();
    }

    public interface TensedForms {
        public boolean getTensedForm(PersonNumber personNumber, Time time, IFormBuilder formBuilder, Alts.Chooser chooser);
    }

    public static interface TensedEntry extends TensedForms, Form.Forms<VerbKey> {
    }

    public static class VerbForms implements TensedEntry {
        EnumMap<VerbKey, Formf> stored;

        public VerbForms (EnumMap<VerbKey, Formf> stored) {
            this.stored = stored;
        }

        public boolean applyRule(VerbKey key, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf base = stored.get(VerbKey.Base);
            if (base == null || !base.apply(formBuilder, chooser)) {
                return false;
            }
            Rulef rule = FormRule.noopRule;
            switch(key) {
                case TpSi:
                    rule = tpSiRule(formBuilder);
                    break;
                case Past:
                    rule = pastRule(formBuilder);
                    break;
                case Prog:
                    rule = progRule(formBuilder);
                    break;
                default:
                    break;
            }
            return rule.apply(formBuilder, chooser);
        }

        @Override
        public boolean getForm(VerbKey key, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf formf = stored.get(key);
            if (formf != null) {
                return formf.apply(formBuilder, chooser);
            }
            else if (key.equals(VerbKey.Part)) {
                return getForm(VerbKey.Past, formBuilder, chooser);
            }
            else {
                return applyRule(key, formBuilder, chooser);
            }
        }

        public static class Builder {
            String id;
            EnumMap<VerbKey,Formf> stored;
            public Builder (String ss) {
                this.id = ss;
                this.stored = new EnumMap<VerbKey, Formf>(VerbKey.class);
                putForm(VerbKey.Base, ss);
            }
            public Builder putForm(VerbKey key, String ss) {
                stored.put(key, Suffix.makeFormf(id, key, ss));
                return this;
            }
            public Builder putForm(String ks, String ss) {
                return putForm(VerbKey.valueOf(ks), ss);
            }
            public VerbForms make () {
                return new VerbForms(stored);
            }
        }

        @Override
        public boolean getTensedForm(PersonNumber personNumber, Time time, IFormBuilder formBuilder, Alts.Chooser chooser) {
            if (time.isPast()) {
                return getForm(VerbKey.Past, formBuilder, chooser);
            }
            else if (time.isPresent()) {
                if (personNumber.equals(PersonNumber.TpSi)) {
                    return getForm(VerbKey.TpSi, formBuilder, chooser);
                }
                else {
                    return getForm(VerbKey.Base, formBuilder, chooser);
                }
            }
            else {
                return false;
            }
        }

    }

    public static VerbForms.Builder verbFormsBuilder(String bs) {
        return new VerbForms.Builder(bs);
    }

    public static VerbForms verbForms (String sss) {
        List<String> ssl = Suffix.ssplit(sss);
        int n = ssl.size();
        Preconditions.checkState(!ssl.isEmpty());
        VerbForms.Builder builder = verbFormsBuilder(ssl.get(0));
        int p = 1;
        int e = n-1;
        if (p <= e && ssl.get(e).endsWith("ing")) {
            builder.putForm(VerbKey.Prog, ssl.get(e));
            e--;
        }
        if (p <= e && ssl.get(e).endsWith("n")) {
            builder.putForm(VerbKey.Part, ssl.get(e));
            e--;
        }
        if (p <= e && ssl.get(p).endsWith("s")) {
            builder.putForm(VerbKey.TpSi, ssl.get(p));
            p++;
        }
        if (p <= e) {
            builder.putForm(VerbKey.Past, ssl.get(p));
            p++;
        }
        if (p <= e) {
            builder.putForm(VerbKey.Part, ssl.get(p));
            p++;
        }
        Preconditions.checkState(p > e);
        return builder.make();
    }

    public static TensedEntry beForms = new VerbForms(new EnumMap<VerbKey, Formf>(VerbKey.class)) {

        public final Formf Was = Suffix.makeFormf("be", "was", "was");
        public final Formf Were = Suffix.makeFormf("be", "were", "were");
        public final Formf Am = Suffix.makeFormf("be", "am", "am");
        public final Formf Is = Suffix.makeFormf("be", "is", "is");
        public final Formf Are = Suffix.makeFormf("be", "are", "are");
        public final Formf Be = Suffix.makeFormf("be", "be", "be");
        public final Formf Been = Suffix.makeFormf("be", "been", "been");
        public final Formf Being = Suffix.makeFormf("be", "been", "been");

        public Formf getTensedFormf(PersonNumber personNumber, Time time) {
            if (time.isPast()) {
                return (personNumber.numberKey.isPlural() || personNumber.personKey.isSecond()) ?
                       Were : Was;
            }
            else if (time.isPresent()) {
                if (personNumber.equals(PersonNumber.FpSi)) {
                    return Am;
                }
                else if (personNumber.equals(PersonNumber.TpSi)) {
                    return Is;
                }
                else {
                    return Are;
                }
            }
            else {
                return null;
            }
        }

        @Override
        public boolean getTensedForm(PersonNumber personNumber, Time time, IFormBuilder formBuilder, Alts.Chooser chooser) {
            Formf formf = getTensedFormf(personNumber, time);
            return formf != null && formf.apply(formBuilder, chooser);
        }

        public Formf getFormf(VerbKey key) {
            switch (key) {
                case Base:
                    return Be;
                case TpSi:
                    return Is;
                case Part:
                    return Been;
                case Past:
                    return Was;
                default:
                    return Be;
            }
        }

        @Override
        public boolean getForm(VerbKey key, IFormBuilder builder, Alts.Chooser chooser) {
            return getFormf(key).apply(builder, chooser);
        }
    };

    public static final VerbForms haveForms = verbForms("have has had");

    public static final Formf Will = Suffix.makeFormf("will", "will");

    public static boolean getVerbGroup(TensedEntry baseVerb,
                                PersonNumber personNumber, Time time, Completeness completeness, Voice voice,
                                IFormBuilder formBuilder, Alts.Chooser chooser) {
        VerbKey nextKey = null;
        if (time.isFuture()) {
            Will.apply(formBuilder, chooser);
            formBuilder.add(' ');
            nextKey = VerbKey.Base;
        }
        if (completeness.isComplete()) {
            if (nextKey == null) {
                haveForms.getTensedForm(personNumber, time, formBuilder, chooser);
            }
            else {
                haveForms.getForm(nextKey, formBuilder, chooser);
            }
            formBuilder.add(' ');
            nextKey = VerbKey.Part;
        }
        if (voice.isPassive()) {
            if (nextKey == null) {
                beForms.getTensedForm(personNumber, time, formBuilder, chooser);
            }
            else {
                beForms.getForm(nextKey, formBuilder, chooser);
            }
            formBuilder.add(' ');
            nextKey = VerbKey.Part;
        }
        if (nextKey == null){
            baseVerb.getTensedForm(personNumber, time, formBuilder, chooser);
        }
        else {
            baseVerb.getForm(nextKey, formBuilder, chooser);
        }
        return true;
    }
}



