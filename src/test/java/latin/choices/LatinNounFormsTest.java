package latin.choices;

import com.google.common.collect.Iterables;

import org.junit.Test;

import junit.framework.Assert;
import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.StringForm;

public class LatinNounFormsTest {

    static CaseNumber NOMSI = CaseNumber.NomSi;
    static CaseNumber NOMPL = CaseNumber.NomPl;
    static CaseNumber VOCSI = CaseNumber.VocSi;
    static CaseNumber ACCSI = CaseNumber.AccSi;
    static CaseNumber ACCPL = CaseNumber.AccPl;
    static CaseNumber GENSI = CaseNumber.GenSi;
    static Gender M = Gender.m;
    static Gender F = Gender.f;
    static Gender N = Gender.n;

    static boolean formsEqual(Form f1, Form f2) {
        return f1 != null && f2 != null && Iterables.elementsEqual(f1, f2);
    }

    static void checkFormsEqual(Form f1, Form f2) {
        Assert.assertTrue(formsEqual(f1, f2));
    }

    @Test
    public void testRenaming() {
        Assert.assertEquals(NOMSI, LatinNounForms.getRenamed(ACCSI, N));
        Assert.assertNull(LatinNounForms.getRenamed(ACCSI, M));
        Assert.assertNull(LatinNounForms.getRenamed(GENSI, N));
        Assert.assertEquals(NOMPL, LatinNounForms.getRenamed(ACCPL, N));
        Assert.assertNull(LatinNounForms.getRenamed(ACCPL, F));
    }

    @Test
    public void testKeyFormEntry() {
        final Form nsi = new StringForm("a","a");
        LatinNounForms.KeyFormEntry entry = new LatinNounForms.KeyFormEntry() {
            @Override
            public Form getKeyForm(CaseNumber cn, Gender g) {
                return cn.equals(NOMSI) ? nsi : null;
            }
        };
        checkFormsEqual(nsi, entry.getNounForm(VOCSI, M));
        checkFormsEqual(nsi, entry.getNounForm(ACCSI, N));
        Assert.assertNull(entry.getNounForm(ACCSI, M));
    }

    @Test
    public void testRegularAndIrregularFormEntry () {
        final Form ie = new StringForm("a","a");
        final Form re = new StringForm("b","b");
        LatinNounForms.RegularAndIrregularFormEntry entry =
            new LatinNounForms.RegularAndIrregularFormEntry() {
                @Override
                public Form getIrregularForm(CaseNumber cn, Gender g) {
                    return cn.equals(NOMSI) ? ie : null;
                }
                @Override
                public Form getRegularForm(CaseNumber cn, Gender g) {
                    return cn.equals(GENSI) ? re : null;
                }
            };
        checkFormsEqual(ie, entry.getKeyForm(NOMSI, M));
        checkFormsEqual(re, entry.getKeyForm(GENSI, M));
        checkFormsEqual(ie, entry.getNounForm(VOCSI, M));
    }

    @Test
    public void testGstemRulesFormEntry () {
        final Form stem = new StringForm("a", "a");
        final ModRule rule = new ModRule("b", "b");
        final Form t1 = new StringForm("ab", "ab");
        LatinNounForms.GstemRulesFormEntry entry =
            new LatinNounForms.GstemRulesFormEntry() {
                @Override
                public Form getGstem() {
                    return stem;
                }
                @Override
                public ModRule getGstemRule(CaseNumber cn, Gender g) {
                    return cn.equals(NOMSI) ? rule : null;
                }
            };
        checkFormsEqual(t1, entry.getRegularForm(NOMSI, M));
        checkFormsEqual(t1, entry.getNounForm(VOCSI, M));
    }

}