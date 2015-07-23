package latin.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathIdTest {

    @Test
    public void testEquality() {
        PathId p1 = PathId.makePath("foo", "bar", "baz");
        System.out.println("p1 " + p1.toString());
        PathId p2 = PathId.makePath("foo", "bar", "baz");
        assertEquals(p1,p2);
    }
}