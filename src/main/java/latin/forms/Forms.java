
package latin.forms;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import latin.choices.Alts;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Forms {

    private Forms () {
    }

    public static <ET> IFormBuilder applyStemf (Stemf<ET> stemf, ET e, Alts.AltChooser altChooser) {
        if (stemf == null) {
            return null;
        }
        else {
            return stemf.apply(e, altChooser);
        }
    }

    public static IFormBuilder applyRule(Rulef rulef, IFormBuilder formBuilder, Alts.AltChooser altChooser) {
        if (formBuilder == null || rulef == null) {
            return null;
        }
        if (rulef.apply(formBuilder, altChooser)) {
            return formBuilder;
        }
        else {
            return null;
        }
    }

    public static IFormBuilder applyFormf(Formf formf, Alts.AltChooser altChooser) {
        if (formf == null) {
            return null;
        }
        else {
            return formf.apply(altChooser);
        }
    }

    public static String formString(IForm iForm) {
        return (iForm != null) ? iForm.toString() : null;
    }

    public static void addForm(IForm iform, List<String> strings) {
        if (iform != null) {
            strings.add(iform.toString());
        }
    }

    public static File wordsDirectory = new File("words");

    public static File wordsFile(String filename) {
        return new File(wordsDirectory, filename + ".xml");
    }

    public static class StringIForm implements IForm, IPhrase {
        public final String string;

        public StringIForm(String string) {
            this.string = string;
        }

        @Override
        public boolean spaceBefore() {
            return true;
        }

        @Override
        public boolean spaceAfter() {
            return true;
        }

        @Override
        public String sequenceString(IForm nxt) {
            return string;
        }

        @Override
        public int length() {
            return string.length();
        }

        @Override
        public char charAt(int i) {
            return string.charAt(i);
        }

        @Override
        public CharSequence subSequence(int s, int e) {
            return string.subSequence(s, e);
        }

        @Override
        public Iterator<IForm> iterator () {
            return Iterators.singletonIterator((IForm)this);
        }
    }

    public static StringIForm stringIForm (String s) {
        return new StringIForm(Suffix.makeFormString(s));
    }

    public static final StringIForm period = new StringIForm(".") {
        @Override
        public boolean spaceBefore() {
            return false;
        }
    };

    public static final StringIForm comma = new StringIForm(",") {
        @Override
        public boolean spaceBefore() {
            return false;
        }
    };

    public static final StringIForm openParen = new StringIForm("(") {
        @Override
        public boolean spaceAfter() {
            return false;
        }
    };

    public static final StringIForm closeParen = new StringIForm(")") {
        @Override
        public boolean spaceBefore() {
            return false;
        }
    };

    public static final Function<String,IForm> toIform = new Function<String, IForm>() {
        @Override
        public IForm apply(@Nullable String s) {
            Preconditions.checkNotNull(s);
            return stringIForm(s);
        }
    };

    public static void addToPhrases(List<Iterable<IForm>> iterables, Object o) {
        if (o instanceof IPhrase) {
            iterables.add((IPhrase)o);
        }
        else if (o instanceof IForm) {
            iterables.add(Lists.newArrayList((IForm) o));
        }
        else {
            List<IForm> subforms = Lists.newArrayList();
            Iterables.addAll(subforms, Iterables.transform(Suffix.ssplitter(o.toString()), Forms.toIform));
            iterables.add(subforms);
        }
    }

    public static StringBuilder printPhrase(IPhrase iPhrase, StringBuilder sb) {
        IForm prev = null;
        PeekingIterator<IForm> pi = Iterators.peekingIterator (iPhrase.iterator());
        while (pi.hasNext()) {
            IForm curr = pi.next();
            IForm next = pi.hasNext() ? pi.peek() : null;
            if (prev != null && prev.spaceAfter() && curr.spaceBefore()) {
                sb.append(' ');
            }
            sb.append(curr.sequenceString(next));
            prev = curr;
        }
        return sb;
    }

    public static StringBuilder printPhrase(IPhrase iPhrase) {
        return printPhrase(iPhrase, new StringBuilder());
    }

    public static class PhraseIterator extends AbstractIterator<IForm> {
        private Iterator<? extends Iterable<IForm>> pit;
        private Iterator<IForm> fit;
        public PhraseIterator(Iterator<? extends Iterable<IForm>> pit) {
            this.pit = pit;
            this.fit = Iterators.emptyIterator();
        }
        public PhraseIterator(Iterable<? extends Iterable<IForm>> pi) {
            this(pi.iterator());
        }
        @Override
        protected IForm computeNext() {
            while(true) {
                if (fit.hasNext()) {
                    return fit.next();
                }
                else if (pit.hasNext()) {
                    fit = pit.next().iterator();
                }
                else {
                    break;
                }
            }
            return endOfData();
        }
    }
}