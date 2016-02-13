package latin.choices;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.junit.Assert;
import org.junit.Test;

import latin.forms.StringForm;

import java.util.Map;

public class LatinCountNounTest {

    static Declension.Rules firstmf = Declension.getRules("First.mf");
    static Declension.Rules firsta = Declension.getRules("First.a");
    static Declension.Rules secondn = Declension.getRules("Second.n");
    static Declension.Rules secondum = Declension.getRules("Second.um");

    static StringForm gstem = new StringForm("b","b");

    static CaseNumber NOMSI = CaseNumber.NomSi;
    static CaseNumber ACCSI = CaseNumber.AccSi;

    @Test
    public void testCompleteRules() {
        StringForm target = new StringForm("ba","ba");
        LatinCountNoun.EntryImpl entry =
            new LatinCountNoun.EntryImpl(gstem, firsta, Gender.f,
                                         LatinCountNoun.EmptyStoredFormFunction);
        Assert.assertTrue(Iterables.elementsEqual(target, entry.getForm(NOMSI)));
    }

    @Test
    public void testCompleteRulesRenamed() {
        StringForm target = new StringForm("bum","bum");
        LatinCountNoun.EntryImpl entry =
            new LatinCountNoun.EntryImpl(gstem, secondum, Gender.n,
                                         LatinCountNoun.EmptyStoredFormFunction);
        Assert.assertTrue(Iterables.elementsEqual(target, entry.getForm(ACCSI)));
    }

    @Test
    public void testIncompleteRules() {
        StringForm nsi = new StringForm("poof", "poof");
        Map<CaseNumber,StringForm> m = Maps.newHashMap();
        m.put(NOMSI, nsi);
        LatinCountNoun.EntryImpl entry =
            new LatinCountNoun.EntryImpl(gstem, firstmf, Gender.f,
                                         m::get);
        Assert.assertTrue(Iterables.elementsEqual(nsi, entry.getForm(NOMSI)));
    }

    @Test
    public void testIncompleteRulesRenamed() {
        StringForm nsi = new StringForm("poof", "poof");
        Map<CaseNumber,StringForm> m = Maps.newHashMap();
        m.put(NOMSI, nsi);
        LatinCountNoun.EntryImpl entry =
            new LatinCountNoun.EntryImpl(gstem, secondn, Gender.n,
                                         m::get);
        Assert.assertTrue(Iterables.elementsEqual(nsi, entry.getForm(ACCSI)));
    }

}