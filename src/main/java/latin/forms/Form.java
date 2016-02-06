package latin.forms;

import com.google.common.base.Joiner;

import latin.choices.Chooser;
import latin.choices.RecordAlts;
import latin.util.ImmutableIterable;

public interface Form extends ImmutableIterable<String>, RecordAlts, Chooser.Choose<String> {

    default String cjoin() {
        return Joiner.on(',').join(this);
    }

    default String pjoin() {
        return "[" + cjoin() + "]";
    }

}
