package latin.forms;

import java.util.function.Function;

public interface Rule extends Function<Form,Form> {

    public static final Rule noopRule = new Rule() {
        public Form apply(Form stem) {
            return stem;
        }
    };

}