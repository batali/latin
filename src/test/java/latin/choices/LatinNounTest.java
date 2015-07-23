package latin.choices;

import org.junit.Test;

import junit.framework.Assert;
import latin.forms.Form;
import latin.forms.StringForm;
import latin.util.DomElement;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.Map;
import java.util.function.BiConsumer;

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

    void showForms(LatinNoun.Entry e) {
        for (CaseNumber cn : CaseNumber.values()) {
            Form f = e.getForm(cn);
            System.out.println(cn.toString() + " : " + f.toString());
        }
    }

    LatinNoun.EntryBuilder builder(String id) {
        return LatinNoun.builder(PathId.makeRoot(id));
    }

    @Test
    public void testNounEntry() {
        LatinNoun.Entry e1 = builder("e1")
            .setGender("f")
            .setRules("First.a")
            .setGstem("femin")
            .build();
        showForms(e1);

        /*
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
        */
    }

    void checkForm(String ts, Form f) {
        Assert.assertEquals(StringForm.split(ts), f.asList());
    }

    void checkEntryForms(LatinNoun.Entry e, String... formStrings) {
        BiConsumer<String, String> checker = new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                CaseNumber key = CaseNumber.fromString(s);
                checkForm(s2,  e.getForm(key));
            }
        };
        for (String fs : formStrings) {
            Splitters.esplit(fs, checker);
        }
    }

    @Test
    public void testFromDom() throws Exception {
        DomElement domElement = DomElement.fromFile(DomElement.getResourceFile(LatinNounTest.class, "LatinNounTest.xml"));
        LatinNoun.Entry e = LatinNoun.fromDomElement("f", domElement);
        checkEntryForms(e, "NomSi=aqua", "DatSi=poop");
    }

    void checkClassified(String cs, String rn, String... formStrings) {
        LatinNoun.NounEntry e = builder(cs).classify(cs).build();
        LatinNoun.Rules rules = LatinNoun.getRules(rn);
        Assert.assertEquals(rules, e.getRules());
        checkEntryForms(e, formStrings);
    }

    @Test
    public void testClassify() throws Exception {
        checkClassified("aqua,aquae,f", "First.a",
                        "NomSi=aqua", "DatSi=aquae");
        checkClassified("animal,animālis,n,i", "Third.n.i",
                        "NomPl=animālia", "AccSi=animal");
        checkClassified("bellum,bellī,n", "Second.um",
                        "AccSi=bellum");
        checkClassified("dominus,dominī,m", "Second.us",
                        "VocSi=domine");
        checkClassified("fīlius,fīliī,m", "Second.ius",
                        "GenSi=fīliī,fīlī", "VocSi=fīlī");
        checkClassified("diēs,diēī,m", "Fifth.v",
                        "DatSi=diēī");
        checkClassified("ager,agrī,m", "Second.r",
                        "AccSi=agrum");
        checkClassified("puer,puerī,m", "Second.er",
                        "AccSi=puerum");

    }

    @Test
    public void testAdj() throws Exception {
        Form stem = new StringForm(PathId.makeRoot("ff"), "long");
        Adjective.Entry e = new Adjective.CmpEntry(stem);
        checkForm("longior", e.getForm(CaseNumber.NomSi, Gender.m));
        checkForm("longiōrī", e.getForm(CaseNumber.DatSi, Gender.f));
        checkForm("longius", e.getForm(CaseNumber.AccSi, Gender.n));
    }

    /*
    @Test
    public void testThirdEntry() {
        LatinNoun.NounEntry e1 = new LatinNoun.NounEntry("e1");
        e1.setGender("m");
        e1.setRules("Third.mf");
        e1.stored("NomSi=rex");
        e1.setGstem("reg");
        showForms(e1);
    }
    */

}