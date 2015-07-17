package latin.forms;

import latin.choices.Chooser;
import latin.choices.RecordAlts;
import latin.util.ImmutableIterable;

public interface Form extends ImmutableIterable<String>, RecordAlts, Chooser.Choose<String> {
}
