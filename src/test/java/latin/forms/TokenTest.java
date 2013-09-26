package latin.forms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TokenTest {

    void checkCharSequence(String ts, CharSequence cs) {
        int n = ts.length();
        assertEquals(n, cs.length());
        for (int i = 0; i < n; i++) {
            assertEquals(ts.charAt(i), cs.charAt(i));
        }
    }

    void checkToken(String ts, Token tk) {
        assertEquals(ts, tk.toString());
        checkCharSequence(ts, tk);
    }

    static final Token token1 = new StringToken("abcde");
    static final Token token2 = new StringToken("wxyz");

    @Test
    public void testMethods() {
        assertEquals('b', token1.charAt(1));
        assertEquals('y', token2.endChar(2));
        assertEquals(5, token1.length());
        assertEquals(1, token2.endOffset(3));
    }

    @Test
    public void testStringToken() {
        checkToken("abcde", token1);
        checkToken("foo", new StringToken("foo"));
        checkToken("a", new StringToken('a'));
        checkToken("bar", Tokens.token("bar"));
        assertSame(token2, Tokens.token(token2));
        checkToken("b", Tokens.token('b'));
    }

    @Test
    public void testSub() {
        checkToken("ab", token1.butLast(3));
        checkToken("de", token1.butFirst(3));
        checkToken("cd", token1.subSequence(2, 4));
        checkToken("wx", token2.subSequence(0, 2));
        assertSame(Tokens.emptyToken, token1.subSequence(0, 0));
    }

    @Test
    public void testPair() {
        Token p1 = Tokens.pair(token1, token2);
        checkToken("abcdewxyz", p1);
        checkToken("dewx", new PairToken(token1.butFirst(3), token2.butLast(2)));
        checkToken("dewx", p1.subSequence(3, p1.endOffset(2)));
        checkToken("abc", p1.butLast(token2.length() + 2));
        Token p2 = Tokens.pair(token1, token2.subSequence(0, 0));
        checkToken(token1.toString(), p2);
        assertTrue(p1 instanceof PairToken);
        assertTrue(p2 instanceof StringToken);
        Token p3 = Tokens.pair("foo", "bar");
        checkToken("foobar", p3);
        Token p4 = Tokens.pair(p3, 's');
        checkToken("foobars", p4);
    }

}