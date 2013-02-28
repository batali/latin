
package latin.forms;

import latin.choices.CaseNumber;
import latin.choices.NounForms;
import org.junit.Test;

public class NounRulesTest {

    public void showErules(NounForms.Erules rules) {
        System.out.println("Erules " + rules.name);
        for (CaseNumber key : CaseNumber.values()) {
            String kstring = key.toString();
            String vstring = "";
            Rulef rule = null;
            CaseNumber rk = key;
            while(true) {
                rule = rules.getRule(rk);
                if (rule != null) {
                    if (rk != key) {
                        vstring = rk.toString();
                    }
                    break;
                }
                CaseNumber nrk = NounForms.renamed.get(rk);
                if (nrk == null) {
                    if (rk != key) {
                        vstring = rk.toString();
                    }
                    break;
                }
                rk = nrk;
            }
            if (rule != null && vstring.isEmpty()) {
                vstring = rule.toString();
                Rulef frule = rule.firstRule();
                if (frule != rule) {
                    vstring += " : " + frule.toString();
                }
                if (frule.endingString() != null) {
                    vstring += " '" + frule.endingString() + "'";
                }
            }
            System.out.println(String.format("\t %s : %s", kstring, vstring));
        }
    }

    @Test
    public void testErules() throws Exception {
        for (NounForms.Erules rules : NounForms.getAllRules()) {
            showErules(rules);
        }
    }

}