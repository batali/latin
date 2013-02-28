
package latin.choices;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

public class AltsTest {

   @Test
   public void testCollectAlts() {
       List<String> list1 = Lists.newArrayList("a", "b", "c");
       List<Integer> list2 = Lists.newArrayList(1, 2);
       CollectAlts collector = new CollectAlts();
       List<String> rlist = Lists.newArrayList();
       do {
           rlist.add(Alts.chooseElement(list1, collector) + "." + Alts.chooseElement(list2, collector).toString());
       }
       while (collector.incrementPositions());
       System.out.println("rlist " + rlist.toString());
       System.out.println("ids " + collector.getIds().toString());
   }

}