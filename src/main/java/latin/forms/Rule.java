package latin.forms;

import latin.util.PathId;

import java.util.function.Function;

public interface Rule extends Function<Form,Form>, PathId.Identified {

    public static final Rule noopRule = new Rule() {
        PathId pathId = PathId.makeRoot("noop");
        public Form apply(Form stem) {
            return stem;
        }
        @Override
        public PathId getPathId() {
            return pathId;
        }
    };

}