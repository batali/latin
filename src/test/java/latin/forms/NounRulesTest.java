
package latin.forms;

import com.google.common.collect.Lists;

import org.junit.Test;

import latin.choices.CaseNumber;
import latin.choices.CollectAlts;
import latin.choices.Noun;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NounRulesTest {

    @Test
    public void testEntry() {
        Noun.LatinEntryImpl e1 = Noun.makeEntry("femina", "femin", "First.mf");
        assertTrue(e1.hasStored("NomSi"));
        Noun.LatinEntryImpl e2 = Noun.makeEntry("femina", "femin", "First.a");
        assertFalse(e2.hasStored("NomSi"));
    }

    public List<String> getForms(Noun.LatinEntry e, CaseNumber key) {
        CollectAlts collectAlts = new CollectAlts();
        List<String> formList = Lists.newArrayList();
        do {
            Token t = e.getForm(key, collectAlts);
            if (t != null) {
                formList.add(t.toString());
            }
        }
        while (collectAlts.incrementPositions());
        return formList;
    }

    void showForms(Noun.LatinEntry e) {
        for (CaseNumber key : CaseNumber.values()) {
            List<String> fl = getForms(e, key);
            System.out.println("\t" + key.toString() + " : " + fl.toString());
        }
    }

    @Test
    public void testForms() throws Exception {
        Noun.LatinEntry e1 = Noun.makeEntry("femina", "femin", "First.a");
        showForms(e1);
    }




}