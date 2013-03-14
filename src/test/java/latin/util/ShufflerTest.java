
package latin.util;

import org.junit.Test;

import java.util.List;

public class ShufflerTest {

    @Test
    public void testKnuth() throws Exception {
        List<Integer> li = Shuffler.knuthChoose(10, 5);
        System.out.println(li);
    }

    @Test
    public void testFloyd() throws Exception {
        List<Integer> li = Shuffler.floydChoose(10, 5);
        System.out.println(li);
    }

}