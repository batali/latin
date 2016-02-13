package latin.choices;

import com.google.common.collect.Iterables;

import org.junit.Test;

import junit.framework.Assert;
import latin.forms.Form;
import latin.forms.StringForm;

import java.util.function.BiFunction;

public class LatinNounFormsTest {

    static CaseNumber NOMSI = CaseNumber.NomSi;
    static CaseNumber NOMPL = CaseNumber.NomPl;
    static CaseNumber VOCSI = CaseNumber.VocSi;
    static CaseNumber ACCSI = CaseNumber.AccSi;
    static CaseNumber ACCPL = CaseNumber.AccPl;
    static CaseNumber GENSI = CaseNumber.GenSi;
    static CaseNumber DATPL = CaseNumber.DatPl;
    static CaseNumber ABLPL = CaseNumber.AblPl;
    static Gender M = Gender.m;
    static Gender F = Gender.f;
    static Gender N = Gender.n;

    static boolean formsEqual(Form f1, Form f2) {
        if (f1 == f2) {
            return true;
        }
        if (f1 == null || f2 == null) {
            return false;
        }
        return Iterables.elementsEqual(f1, f2);
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
        Assert.assertEquals(DATPL, LatinNounForms.getRenamed(ABLPL, M));
    }

    @Test
    public void testGetForm() {
        final Form form1 = new StringForm("ant", "ant");
        BiFunction<CaseNumber, Gender, Form> keyFormFunction =
            new BiFunction<CaseNumber, Gender, Form>() {
                @Override
                public Form apply(CaseNumber cn, Gender g) {
                    return cn.equals(NOMSI) ? form1 : null;
                }
            };
        checkFormsEqual(form1, LatinNounForms.getForm(keyFormFunction, VOCSI, M));
        checkFormsEqual(form1, LatinNounForms.getForm(keyFormFunction, ACCSI, N));
        checkFormsEqual(null, LatinNounForms.getForm(keyFormFunction, ACCSI, F));
    }

}