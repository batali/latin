
package latin.forms;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import latin.choices.Alts;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.List;

public abstract class FormRule implements Rulef {

    public @Nullable String endingString() {
        return null;
    }

    public FormRule firstRule() {
        return this;
    }

    public FormRule() {
    }

    public String getSpec() {
        return toString();
    }

    public static class StringFormRule extends FormRule {
        public final String form;
        public StringFormRule(String form) {
            this.form = form;
        }
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            formBuilder.add(form);
            return true;
        }
        @Override
        public String toString() {
            return form;
        }
        @Override
        public String endingString() {
            return form;
        }
    }

    public static FormRule noopRule = new FormRule() {
        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            return true;
        }
        @Override
        public String endingString() {
            return "";
        }
        @Override
        public String toString() {
            return ":";
        }
    };

    public static FormRule falseFormf = new FormRule() {
        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            return false;
        }
        @Override
        public String toString() {
            return "False";
        }
    };

    public static final FormRule shortenLast = new FormRule() {
        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            int s = formBuilder.length();
            if (s == 0) {
                return false;
            }
            else {
                formBuilder.setCharAt(s-1, Suffix.unaccented(formBuilder.charAt(s-1)));
                return true;
            }

        }
        @Override
        public String toString() {
            return ">";
        }
    };

    public static final FormRule lengthenLast = new FormRule() {
        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            int s = formBuilder.length();
            if (s == 0) {
                return false;
            }
            else {
                formBuilder.setCharAt(s-1, Suffix.accented(formBuilder.charAt(s-1)));
                return true;
            }
        }
        @Override
        public String toString() {
            return "<";
        }
    };

    public static final FormRule duplicateLast = new FormRule() {
        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            if (formBuilder.isEmpty()) {
                return false;
            }
            else {
                formBuilder.add(formBuilder.charAt(formBuilder.length()-1));
                return true;
            }
        }
        @Override
        public String toString() {
            return "+";
        }
    };

    static class RemoveEndingChars extends FormRule {
        public final int removed;
        public RemoveEndingChars(int removed) {
            super();
            this.removed = removed;
            Preconditions.checkArgument(removed > 0);
        }
        @Override
        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            int s = formBuilder.length();
            if (s < removed) {
                return false;
            }
            else {
                formBuilder.removeLast(removed);
                return true;
            }
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < removed; i++) {
                sb.append('-');
            }
            return sb.toString();
        }
    }

    public static final RemoveEndingChars removeLast = new RemoveEndingChars(1);

    public static class SequenceRule extends FormRule {

        public final ImmutableList<? extends Rulef> sequence;

        public SequenceRule(ImmutableList<? extends Rulef> sequence) {
            this.sequence = sequence;
        }

        public SequenceRule(List<? extends Rulef> rules) {
            this(ImmutableList.copyOf(rules));
        }

        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            for (Rulef rulef : sequence) {
                if (!rulef.apply(formBuilder, chooser)) {
                    return false;
                }
            }
            return true;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Rulef fr : sequence) {
                sb.append(fr.toString());
            }
            return sb.toString();
        }
    }

    private static List<FormRule> parseFormfSequence(String ffs) {
        List<FormRule> formfList = Lists.newArrayList();
        int sp = 0;
        int ep = ffs.length();
        while(sp < ep) {
            char nc = ffs.charAt(sp);
            if (Character.isLetter(nc)) {
                int np = sp + 1;
                while(np < ep && Character.isLetter(ffs.charAt(np))) {
                    np++;
                }
                if (np < ep && Character.isWhitespace(ffs.charAt(np))) {
                    np++;
                }
                formfList.add(new StringFormRule(ffs.substring(sp, np)));
                while(np < ep && Character.isWhitespace(ffs.charAt(np))) {
                    np++;
                }
                sp = np;
            }
            else if (nc == '-') {
                int np = sp+1;
                while (np < ep && ffs.charAt(np)=='-') {
                    np++;
                }
                if (np == sp+1) {
                    formfList.add(removeLast);
                }
                else {
                    formfList.add(new RemoveEndingChars(np-sp));
                }
                sp = np;
            }
            else if (nc == '<') {
                formfList.add(lengthenLast);
                sp++;
            }
            else if (nc == '>') {
                formfList.add(shortenLast);
                sp++;
            }
            else if (nc == '+') {
                formfList.add(duplicateLast);
                sp++;
            }
            else if (nc == ':') {
                if (formfList.isEmpty()) {
                    formfList.add(noopRule);
                }
                sp++;
            }
            else {
                throw new IllegalArgumentException("bad mod string '" + ffs + "'");
            }
        }
        return formfList;
    }

    public static FormRule sequenceFormf(List<? extends FormRule> rules) {
        if (rules.isEmpty()) {
            return noopRule;
        }
        else if (rules.size() == 1) {
            return rules.get(0);
        }
        else {
            return new SequenceRule(rules);
        }
    }

    public static FormRule makeFormRule(Object x) {
        if (x instanceof FormRule) {
            return (FormRule) x;
        }
        else {
            return sequenceFormf(parseFormfSequence(Suffix.makeFormString(x.toString())));
        }
    }

    public static Function<Object,FormRule> toFormRule = new Function<Object, FormRule>() {
        @Override
        public FormRule apply(@Nullable Object o) {
            Preconditions.checkNotNull(o);
            return makeFormRule(o);
        }
    };

    static class AltsRule extends AbstractList<FormRule> implements Rulef {

        public final String id;
        public final ImmutableList<FormRule> alts;

        public AltsRule(String id, List<?> objects) {
            this.id = id;
            this.alts = ImmutableList.copyOf(Lists.transform(objects, toFormRule));
        }

        public int size() {
            return alts.size();
        }

        public FormRule get(int p) {
            return alts.get(p);
        }

        public boolean apply(IFormBuilder formBuilder, Alts.Chooser chooser) {
            return Alts.chooseElement(this, chooser).apply(formBuilder, chooser);
        }

        public String toString() {
            return alts.toString();
        }

        @Override
        public FormRule firstRule() {
            return alts.get(0).firstRule();
        }

        @Override
        public String endingString() {
            return alts.get(0).endingString();
        }
    }

    public static Rulef makeRule(String prefix, Object key, List<?> objects) {
        return new AltsRule(prefix + "." + key.toString(), objects);
    }

    public static Rulef parseRule(String prefix, Object key, String afs) {
        return makeRule(prefix, key, Suffix.csplit(afs));
    }

}





