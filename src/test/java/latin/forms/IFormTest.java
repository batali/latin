
package latin.forms;

import junit.framework.Assert;
import org.junit.Test;

public class IFormTest {

    public void checkPhrase(IPhrase phrase, String ts) {
        String ps = Forms.printPhrase(phrase).toString();
        System.out.println(ps);
        Assert.assertEquals(ts, ps);
    }

    @Test
    public void testIformList () {
        checkPhrase(new ListPhrase(English.A, "duck"), "a duck");
        checkPhrase(new ListPhrase(English.A, "apple"), "an apple");
        checkPhrase(new ListPhrase("the man", Forms.period), "the man.");
        checkPhrase(new ListPhrase("I said", Forms.openParen, "quite nicely",Forms.closeParen, "shut up"),
                "I said (quite nicely) shut up");
    }

    @Test
    public void testListPhrase () {
        IPhrase np = new ListPhrase(English.A, "duck");
        ListPhrase vp = new ListPhrase();
        vp.add("walked");
        ListPhrase pp = new ListPhrase();
        vp.add(pp);
        pp.add("to the store");
        ComplexPhrase sp = new ComplexPhrase(np, vp);
        sp.add(Forms.period);
        checkPhrase(sp, "a duck walked to the store.");
    }
}
