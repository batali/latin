
package latin.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.TreeSet;

public class ShufflerTest {

    public void testChooseList(int n, int k, List<Integer> cl) {
        Preconditions.checkArgument(k > 0);
        Assert.assertEquals(k, cl.size());
        TreeSet<Integer> iset = Sets.newTreeSet(cl);
        Assert.assertEquals(k, iset.size());
        Assert.assertTrue(iset.first() >= 0);
        Assert.assertTrue(iset.last() < n);
    }

    @Test
    public void testKnuth() throws Exception {
        for (int i = 0; i < 10; i++) {
            List<Integer> li = Shuffler.knuthChoose(10, 5);
            testChooseList(10, 5, li);
        }
    }

    @Test
    public void testFloyd() throws Exception {
        for (int i = 0; i < 10; i++) {
            List<Integer> li = Shuffler.floydChoose(10, 5);
            testChooseList(10, 5, li);
        }
    }

}