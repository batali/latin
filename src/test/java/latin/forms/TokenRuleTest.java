
package latin.forms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokenRuleTest {

    void checkRuleResult(String ts, TokenRule r, String as) {
        Token rs = r.apply(new StringToken(as));
        assertEquals(ts, rs.toString());
    }

    @Test
    public void testSingleRules() throws Exception {
        checkRuleResult("cat", TokenRules.noopRule, "cat");
        checkRuleResult("hats", new StringToken("s"), "hat");
        checkRuleResult("animal", TokenRules.unaccenterRule, "animāl");
        checkRuleResult("amō", TokenRules.accenterRule, "amo");
        checkRuleResult("he", TokenRules.removeLastRule, "hep");
        checkRuleResult("padd", TokenRules.duplicateLast, "pad");
    }

    @Test
    public void testParseRules() throws Exception {
        checkRuleResult("foe", TokenRules.parseRule("-e"), "foo");
        checkRuleResult("fie", TokenRules.parseRule("oo:ie"), "foo");
        checkRuleResult("aba", TokenRules.parseRule(":ba"), "a");
        checkRuleResult("pooed", TokenRules.parseRule("-+ed"), "pol");
    }


}