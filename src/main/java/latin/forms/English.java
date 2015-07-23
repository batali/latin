
package latin.forms;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import latin.choices.Alts;
import latin.choices.Aspect;
import latin.choices.PersonNumber;
import latin.choices.Time;
import latin.choices.VerbChoices;
import latin.choices.Voice;
import latin.choices.Number;

import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nullable;

public class English {

    private English() {
    }

    public static final TokenRule addS = TokenRules.parseRule("s");
    public static final TokenRule addES = TokenRules.parseRule("es");
    public static final TokenRule addIES = TokenRules.parseRule("-ies");

    public static TokenRule pluralTokenRule = new TokenRule() {
        @Override
        public String getSpec() {
            return "plural";
        }

        @Override
        public Token apply(@Nullable Token token) {
            if (token == null) {
                return token;
            }
            else if (sibilintMatcher.test(token)) {
                return addES.apply(token);
            }
            else if (cyMatcher.test(token)) {
                return addIES.apply(token);
            }
            else {
                return addS.apply(token);
            }
        }
    };


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

    public static class CountNoun {
        public final ImmutableList<Formf> formfList;
        public CountNoun(ImmutableList<Formf> formfList) {
            Preconditions.checkArgument(!formfList.isEmpty());
            this.formfList = formfList;
        }
        public IForm getForm(Number number, Alts.AltChooser altChooser) {
            if (number.isSingular()) {
                return Forms.applyFormf(formfList.get(0), altChooser);
            }
            else if (formfList.size() >= 2) {
                return Forms.applyFormf(formfList.get(1), altChooser);
            }
            else {
                IFormBuilder formBuilder = Forms.applyFormf(formfList.get(0), altChooser);
                return Forms.applyRule(pluralRule(formBuilder), formBuilder, altChooser);
            }
        }
    }

    public static class NounForms {

        public final EnumMap<NounKey, Formf> stored;
        public final EnumMap<NounKey, Rulef> rules;

        public NounForms(EnumMap<NounKey,Formf> stored,
                         EnumMap<NounKey,Rulef> rules) {
            this.stored = stored;
            this.rules = rules;
        }

        public IFormBuilder applyRule(NounKey key, Alts.AltChooser altChooser) {
            Formf si = stored.get(NounKey.Sing);
            IFormBuilder formBuilder = Forms.applyFormf(si, altChooser);
            if (formBuilder == null){
                return null;
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
            return Forms.applyRule(rule, formBuilder, altChooser);
        }

        public IFormBuilder getForm(NounKey key, Alts.AltChooser altChooser) {
            Formf sf = stored.get(key);
            if (sf != null) {
                return sf.apply(altChooser);
            }
            else if (key.equals(NounKey.Sing)) {
                return null;
            }
            else {
                return applyRule(key, altChooser);
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
        public IForm getTensedForm(PersonNumber personNumber, Time time, Alts.AltChooser altChooser);
    }

    public static interface TensedEntry extends TensedForms {
        public IForm getKeyForm(VerbKey verbKey, Alts.AltChooser altChooser);
    }

    public static class VerbForms implements TensedEntry {
        EnumMap<VerbKey, Formf> stored;

        public VerbForms (EnumMap<VerbKey, Formf> stored) {
            this.stored = stored;
        }

        public IFormBuilder applyRule(VerbKey key, Alts.AltChooser altChooser) {
            Formf base = stored.get(VerbKey.Base);
            IFormBuilder formBuilder = Forms.applyFormf(base, altChooser);
            if (formBuilder == null) {
                return null;
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
            return Forms.applyRule(rule, formBuilder, altChooser);
        }

        @Override
        public IFormBuilder getKeyForm(VerbKey key, Alts.AltChooser altChooser) {
            Formf formf = stored.get(key);
            if (formf != null) {
                return formf.apply(altChooser);
            }
            else if (key.equals(VerbKey.Part)) {
                return getKeyForm(VerbKey.Past, altChooser);
            }
            else {
                return applyRule(key, altChooser);
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
        public IForm getTensedForm(PersonNumber personNumber, Time time, Alts.AltChooser altChooser) {
            if (time.isPast()) {
                return getKeyForm(VerbKey.Past, altChooser);
            }
            else if (time.isPresent()) {
                if (personNumber.equals(PersonNumber.TpSi)) {
                    return getKeyForm(VerbKey.TpSi, altChooser);
                }
                else {
                    return getKeyForm(VerbKey.Base, altChooser);
                }
            }
            else {
                return null;
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

    public static VerbForms beForms = new VerbForms(new EnumMap<VerbKey, Formf>(VerbKey.class)) {

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
        public IFormBuilder getTensedForm(PersonNumber personNumber, Time time, Alts.AltChooser altChooser) {
            Formf formf = getTensedFormf(personNumber, time);
            return Forms.applyFormf(formf, altChooser);
        }

        public Formf getKeyFormf(VerbKey key) {
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
        public IFormBuilder getKeyForm(VerbKey key, Alts.AltChooser altChooser) {
            return getKeyFormf(key).apply(altChooser);
        }
    };

    public static final VerbForms haveForms = verbForms("have has had");

    public static final Formf Will = Suffix.makeFormf("will", "will");

    public static ListPhrase getVerbGroup(TensedEntry baseVerb,
                                          PersonNumber personNumber, Time time, Aspect aspect, Voice voice,
                                          Alts.AltChooser altChooser) {
        VerbKey nextKey = null;
        ListPhrase iForms = new ListPhrase();
        if (time.isFuture()) {
            iForms.add(Will.apply(altChooser));
            nextKey = VerbKey.Base;
        }
        if (aspect.isComplete()) {
            if (nextKey == null) {
                iForms.add(haveForms.getTensedForm(personNumber, time, altChooser));
            }
            else {
                iForms.add(haveForms.getKeyForm(nextKey, altChooser));
            }
            nextKey = VerbKey.Part;
        }
        if (voice.isPassive()) {
            if (nextKey == null) {
                iForms.add(beForms.getTensedForm(personNumber, time, altChooser));
            }
            else {
                iForms.add(beForms.getKeyForm(nextKey, altChooser));
            }
            nextKey = VerbKey.Part;
        }
        if (nextKey == null) {
            iForms.add(baseVerb.getTensedForm(personNumber, time, altChooser));
        }
        else {
            iForms.add(baseVerb.getKeyForm(nextKey, altChooser));
        }
        return iForms;
    }

    public static ListPhrase getVerbGroup(TensedEntry baseVerb,
                                          VerbChoices verbChoices,
                                          PersonNumber personNumber,
                                          Alts.AltChooser altChooser) {
        return getVerbGroup(baseVerb, personNumber, verbChoices.time, verbChoices.aspect, verbChoices.voice,

                            altChooser);
    }

    public static final Forms.StringIForm A = new Forms.StringIForm("a") {
        @Override
        public String sequenceString(IForm next) {
            if (next != null && Suffix.startMatches(next, "V")) {
                return "an";
            }
            else {
                return "a";
            }
        }
    };
}



