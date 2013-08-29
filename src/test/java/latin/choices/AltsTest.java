
package latin.choices;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AltsTest {

   @Test
   public void testCollectAlts() {
       List<String> list1 = Lists.newArrayList("a", "b", "c");
       List<Integer> list2 = Lists.newArrayList(1, 2);
       CollectAlts collector = new CollectAlts();
       List<String> rlist = Lists.newArrayList();
       do {
           rlist.add(Alts.chooseElement(list1, list1, collector) + "." + Alts.chooseElement(list2, list2, collector).toString());
       }
       while (collector.incrementPositions());
       System.out.println("rlist " + rlist.toString());
       System.out.println("ids " + collector.getIds().toString());
   }

    @Test
    public void testCollectValues() {
        IdValuesList<String> vl1 = new IdValuesList<String>("vl1", Arrays.asList("a", "b", "c"));
        IdValuesList<String> vl2 = new IdValuesList<String>("vl2", Arrays.asList("1", "2"));
        CollectAlts collector = new CollectAlts();
        List<String> rlist = Lists.newArrayList();
        do {
            rlist.add(Alts.chooseElement(vl1, collector) + "." + Alts.chooseElement(vl2, collector));
        }
        while (collector.incrementPositions());
        System.out.println("rlist " + rlist.toString());
        System.out.println("ids " + collector.getIds().toString());
    }

}