package latin.choices;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatinNounTest {

    private static Logger logger = LoggerFactory.getLogger(LatinNounTest.class);

    LatinNoun.IEntry makeEntry(String id, String adds) {
        return LatinNoun.entryBuilder(id).add(adds).build();
    }

    LatinNoun.IEntry makeEntry(String nsi, String gst, String rs, String gs) {
        return LatinNoun.entryBuilder(nsi, gst, rs, gs).build();
    }

    void showForms(LatinNoun.IEntry entry) {
        logger.info(entry.getId());
        for (CaseNumber cn : CaseNumber.values()) {
            logger.info(cn.toString() + " " + entry.getForm(cn));
        }
    }


    @Test
    public void testFirst() {
        LatinNoun.IEntry entry = makeEntry("aqua", "aqu", "First.a", "f");
        showForms(entry);
    }

    @Test
    public void testSecondUm() {
        LatinNoun.IEntry entry = makeEntry("bellum", "gender=n rules=Second.um gstem=bell");
        showForms(entry);
    }

    @Test
    public void testSecondUs() {
        LatinNoun.IEntry entry = makeEntry("dominus", "gender=m rules=Second.us gstem=domin");
        showForms(entry);
    }

    @Test
    public void testSecondIus() {
        LatinNoun.IEntry entry = makeEntry("fīlius", "gender=m rules=Second.ius gstem=fīli");
        showForms(entry);
    }

    @Test
    public void testRex() {
        showForms(makeEntry("rex", "gender=m rules=Third.mf gstem=reg"));
    }

    @Test
    public void testMare() {
        showForms(makeEntry("mare", "mar", "Third.n.i", "n"));
    }
}