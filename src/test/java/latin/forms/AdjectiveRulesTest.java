
package latin.forms;

import com.google.common.collect.Lists;
import latin.choices.Adjective;
import latin.choices.CaseNumber;
import latin.choices.CollectAlts;
import latin.choices.Gender;
import org.junit.Test;

import java.util.List;

public class AdjectiveRulesTest {

    public static List<Gender> glist = Gender.getIdList(3);

    public static List<String> getForms(Adjective.FormEntry entry, CaseNumber key, Gender gender) {
        List<String> formList = Lists.newArrayList();
        CollectAlts collectAlts = new CollectAlts();
        do {
            FormBuilder formBuilder = new FormBuilder();
            if (entry.getForm(key, gender, formBuilder, collectAlts)) {
                formList.add(formBuilder.toString());
            }
        }
        while(collectAlts.incrementPositions());
        return formList;
    }

    public static boolean notEqualToLast(List<String> ls, String ns) {
        int s = ls.size();
        return s == 0 || !ns.equals(ls.get(s-1));
    }

    public void showAdjectiveForms(Adjective.FormEntry entry) {
        System.out.println(entry.id + " " + entry.rules.name);
        for (CaseNumber caseNumber : CaseNumber.values()) {
            List<String> dlist = Lists.newArrayList();
            for (Gender gender : glist) {
                List<String> strings = getForms(entry, caseNumber, gender);
                String fs = strings.toString();
                if (notEqualToLast(dlist, fs)) {
                    dlist.add(fs);
                }
            }
            System.out.println(String.format("%s %s",
                    caseNumber.toString(),
                    dlist.toString()));
        }
    }

    @Test
    public void testAdjectiveForms() throws Exception {
        Adjective.FormEntry fe = new Adjective.FormEntry(
                "altus", "alt", "us.a.um");
        showAdjectiveForms(fe);
        Adjective.FormEntry ge = new Adjective.FormEntry(
                "sacer", "sacr", "er.a.um");
        showAdjectiveForms(ge);
        Adjective.FormEntry he = new Adjective.FormEntry(
                "miser", "miser", "r.a.um");
        showAdjectiveForms(he);
        List<String> xelist = Lists.newArrayList();
        xelist.add("atrox");
        Adjective.FormEntry xe = new Adjective.FormEntry(
                "atrox", "atroc", "third", xelist);
        showAdjectiveForms(xe);
        List<String> aelist = Lists.newArrayList();
        aelist.add("agilis");
        aelist.add("agile");
        Adjective.FormEntry ae = new Adjective.FormEntry(
                "agilis", "agil", "third", aelist);
        showAdjectiveForms(ae);

    }


}