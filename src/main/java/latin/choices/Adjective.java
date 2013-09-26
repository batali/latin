
package  latin.choices;

import latin.forms.Token;
import latin.forms.TokenRule;

import java.util.List;

public class Adjective {

    public interface Stored {
        public KeyValues<CaseNumber,Token> select(Gender gender);
    }

    public static class ListStored implements Stored {
        final List<KeyValues<CaseNumber,Token>> storedList;
        public ListStored(List<KeyValues<CaseNumber,Token>> storedList) {
            this.storedList = storedList;
        }
        public KeyValues<CaseNumber,Token> select(Gender gender) {
            return gender.select(storedList);
        }
    }

    public static final Stored emptyStored = new Stored() {
        @Override
        public KeyValues<CaseNumber,Token> select(Gender gender) {
            return NounForms.emptyStored;
        }
    };

    public static class Rules {
        final String name;
        final List<KeyValues<CaseNumber,TokenRule>> rulesList;
        public Rules(String name, List<KeyValues<CaseNumber,TokenRule>> rulesList) {
            this.name = name;
            this.rulesList = rulesList;
        }
        public KeyValues<CaseNumber,TokenRule> select(Gender gender) {
            return gender.select(rulesList);
        }
    }

    /*

    static {
        makeRules("first.second", "Second.mf", "First.mf", "Second.n");
        makeRules("us.a.um", "Second.us", "First.a", "Second.um");
        makeRules("r.a.um", "Second.r", "First.a", "Second.um");
        makeRules("er.a.um", "Second.er", "First.a", "Second.um");
        makeRules("third", "Third.adj.mf", "Third.adj.n");
    }

    */


}


