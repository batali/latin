package latin.choices;

import org.junit.Test;

import latin.forms.Form;

import java.util.Map;

public class LatinNounTest {

    void showRulesMap(Map<String,LatinNoun.Rules> rulesMap) {
        for (LatinNoun.Rules rules : rulesMap.values()) {
            LatinNoun.printRules(rules);
        }
    }

    @Test
    public void testFirst() {
        showRulesMap(Declension.First.rulesMap);

    }

    @Test
    public void testSecondRules() {
        showRulesMap(Declension.Second.rulesMap);
    }

    @Test
    public void testThirdRules() {
        showRulesMap(Declension.Third.rulesMap);
    }

    void showForms(LatinNoun.NounEntry e) {
        for (CaseNumber cn : CaseNumber.values()) {
            Form f = e.getForm(cn);
            System.out.println(cn.toString() + " : " + f.toString());
        }
    }

    @Test
    public void testNounEntry() {
        LatinNoun.NounEntry e1 = new LatinNoun.NounEntry("e1");
        e1.setGender("f");
        e1.setRules("First.a");
        e1.setGstem("femin");
        showForms(e1);
        LatinNoun.NounEntry e2 = new LatinNoun.NounEntry("e2");
        e2.setGender("m");
        e2.setRules("Second.us");
        e2.setGstem("domin");
        showForms(e2);
        LatinNoun.NounEntry e3 = new LatinNoun.NounEntry("e3");
        e3.setGender("m");
        e3.setRules("Second.ius");
        e3.setGstem("fili");
        showForms(e3);
    }

    @Test
    public void testThirdEntry() {
        LatinNoun.NounEntry e1 = new LatinNoun.NounEntry("e1");
        e1.setGender("m");
        e1.setRules("Third.mf");
        e1.stored("NomSi=rex");
        e1.setGstem("reg");
        showForms(e1);
    }

}