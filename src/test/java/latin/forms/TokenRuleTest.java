
package latin.forms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TokenRuleTest {

    void checkRuleResult(String ts, TokenRule r, String as) {
        Token rs = r.apply(new StringToken(as));
        assertEquals(ts, rs.toString());
    }

    @Test
    public void testParseRule() {
        assertSame(TokenRules.noopRule, TokenRules.parseRule(":"));
        assertSame(TokenRules.removeLastRule, TokenRules.parseRule("-"));
        assertSame(TokenRules.removeLastRule, TokenRules.parseRule("f:"));
        assertSame(TokenRules.accenterRule, TokenRules.parseRule("<"));
        assertSame(TokenRules.unaccenterRule, TokenRules.parseRule(">"));
        assertSame(TokenRules.duplicateLast, TokenRules.parseRule("+"));
        assertTrue(TokenRules.parseRule(":a") instanceof TokenRules.AddedStringRule);
    }

    @Test
    public void testSingleRules() throws Exception {
        checkRuleResult("cat", TokenRules.noopRule, "cat");
        checkRuleResult("animal", TokenRules.unaccenterRule, "animāl");
        checkRuleResult("amō", TokenRules.accenterRule, "amo");
        checkRuleResult("he", TokenRules.removeLastRule, "hep");
        checkRuleResult("padd", TokenRules.duplicateLast, "pad");
    }

    @Test
    public void testParsedRules() throws Exception {
        checkRuleResult("hats", TokenRules.parseRule("s"), "hat");
        checkRuleResult("foe", TokenRules.parseRule("-e"), "foo");
        checkRuleResult("fie", TokenRules.parseRule("oo:ie"), "foo");
        checkRuleResult("aba", TokenRules.parseRule(":ba"), "a");
        checkRuleResult("pooed", TokenRules.parseRule("-+ed"), "pol");
    }


}