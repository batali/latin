package latin.forms;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import latin.choices.AltsList;
import latin.util.Splitters;

public class StringForm extends AltsList<String> implements Form {

    public StringForm(Object id, ImmutableList<String> values) {
        super(id, values);
    }

    public StringForm(Object id, String cs) {
        this(id, split(cs));
    }

    @Override
    public UnmodifiableIterator<String> iterator() {
        return values.iterator();
    }

    public static String makeFormString(String fstr) {
        return CharMatcher.is('_').collapseFrom(fstr.trim(), ' ');
    }

    public static ImmutableList<String> split(String cs) {
        return Splitters.csplitter(cs).transform(StringForm::makeFormString).toList();
    }

    @Override
    public String toString() {
        return pjoin();
    }

}