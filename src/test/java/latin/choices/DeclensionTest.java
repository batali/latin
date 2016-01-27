package latin.choices;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.StringForm;

import java.util.Map;

public class DeclensionTest {

    private static Logger logger = LoggerFactory.getLogger(DeclensionTest.class);

    @Test
    public void testRules () {
        for (Declension declension : Declension.values()) {
            for (Map.Entry<String, KeyRules<CaseNumber>> entry : declension.rulesMap.entrySet()) {
                KeyRules<CaseNumber> keyRules = entry.getValue();
                logger.info(keyRules.getPathId().toString());
                for (Map.Entry<CaseNumber, ModRule> ruleEntry : keyRules.entrySet()) {
                    logger.info("   " + ruleEntry.getKey() + ": " + ruleEntry.getValue());
                }
            }
        }
    }

    @Test
    public void testSecondIus() {
        StringForm stem = new StringForm("fili", "fili");
        KeyRules<CaseNumber> keyRules = Declension.Second.getRules("ius", true);
        for (Map.Entry<CaseNumber, ModRule> ruleEntry : keyRules.entrySet()) {
            CaseNumber key = ruleEntry.getKey();
            ModRule r = ruleEntry.getValue();
            Form form = r.apply(stem);
            logger.info(key.toString() + " " + form.toString());
        }
    }


}