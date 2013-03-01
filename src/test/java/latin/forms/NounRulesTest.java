
package latin.forms;

import latin.choices.CaseNumber;
import latin.choices.NounForms;
import org.junit.Test;

public class NounRulesTest {

    public void showErules(NounForms.Erules rules) {
        System.out.println("Erules " + rules.name);
        for (CaseNumber key : CaseNumber.values()) {
            Rulef rule = rules.getRule(key);
            if (rule != null) {
                System.out.println(key.toString() + " " + rule.toString());
            }
        }
    }

    @Test
    public void testErules() throws Exception {
        for (NounForms.Erules rules : NounForms.getAllRules()) {
            showErules(rules);
        }
    }

}