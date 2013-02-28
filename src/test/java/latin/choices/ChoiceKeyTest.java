
package latin.choices;

import org.junit.Assert;
import org.junit.Test;

public class ChoiceKeyTest {

    @Test
    public void testCaseKeys() throws Exception {
        for (Case key : Case.values()) {
            String kstr = key.toString();
            boolean isAbl = key.isAblative();
            System.out.println(String.format("k %s isAbl %s", kstr, isAbl));
            Assert.assertEquals((key==Case.Abl), isAbl);
            Case fk1 = Case.fromString(kstr);
            Assert.assertEquals(key, fk1);
            Case  fk2 = Case.fromString(kstr.toUpperCase());
            Assert.assertEquals(key, fk2);
        }
    }

    @Test
    public void testCaseNumberKeys() throws Exception {
        CaseNumber fnsi = CaseNumber.fromString("NomSi");
        Assert.assertEquals(CaseNumber.NomSi, fnsi);
        CaseNumber fnpl = CaseNumber.fromString("NOMPL");
        Assert.assertEquals(CaseNumber.NomPl, fnpl);
        CaseNumber fdsi = CaseNumber.fromCaseNumber(Case.Dat, Number.Si);
        Assert.assertEquals(CaseNumber.DatSi, fdsi);
        CaseNumber flpl = CaseNumber.fromCaseNumber(Case.Loc, Number.Pl);
        Assert.assertEquals(CaseNumber.LocPl, flpl);
    }

    @Test
    public void testPersonNumber() throws Exception {
        PersonNumber pn1 = PersonNumber.fromString("FpSi");
        Assert.assertEquals(PersonNumber.FpSi, pn1);
        PersonNumber pn2 = PersonNumber.fromPersonNumber(Person.Sp, Number.Pl);
        Assert.assertEquals(PersonNumber.SpPl, pn2);
    }

}

