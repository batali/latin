
package latin.forms;

import latin.choices.CaseNumber;
import latin.choices.NounForms;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

public class ErulesMapTest {

    public void showMap(EnumMap<CaseNumber,NounForms.Erule> em) {
        for (Map.Entry<CaseNumber,NounForms.Erule> e : em.entrySet()) {
            System.out.println(e.getKey().toString() + " " + e.getValue().toString());
        }
    }

    NounForms.ErulesEntriesMap am;

    @Before
    public void setup() {
        am = new NounForms.ErulesEntriesMap("first");
        am.add("NomSi.a", "a");
        am.add("GenSi", "ae");
        am.add("DatSi", "ae");
    }

    public void testMap(String ts, NounForms.ErulesEntriesMap m) {
        EnumMap<CaseNumber,NounForms.Erule> m1 = m.selectRules(ts);
        System.out.println(ts);
        showMap(m1);
    }

    @Test
    public void testMaps() {
        testMap("", am);
        testMap("a", am);
    }

}
