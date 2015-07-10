package latin.forms;

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

    public static ImmutableList<String> split(String cs) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String fs : Splitters.csplitter(cs)) {
            builder.add(fs);
        }
        return builder.build();
    }

}