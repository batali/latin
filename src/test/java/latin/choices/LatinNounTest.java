package latin.choices;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import latin.forms.Form;
import latin.forms.StringForm;

import java.io.File;
import java.util.Map;

public class LatinNounTest {

    private static Logger logger = LoggerFactory.getLogger(LatinNounTest.class);

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
        LatinNoun.EntryImpl entry =
            new LatinNoun.EntryImpl(gstem, firsta, Gender.f,
                                         LatinNoun.EmptyStoredFormFunction);
        Assert.assertTrue(Iterables.elementsEqual(target, entry.getForm(NOMSI)));
    }

    @Test
    public void testCompleteRulesRenamed() {
        StringForm target = new StringForm("bum","bum");
        LatinNoun.EntryImpl entry =
            new LatinNoun.EntryImpl(gstem, secondum, Gender.n,
                                         LatinNoun.EmptyStoredFormFunction);
        Assert.assertTrue(Iterables.elementsEqual(target, entry.getForm(ACCSI)));
    }

    @Test
    public void testIncompleteRules() {
        StringForm nsi = new StringForm("poof", "poof");
        Map<CaseNumber,StringForm> m = Maps.newHashMap();
        m.put(NOMSI, nsi);
        LatinNoun.EntryImpl entry =
            new LatinNoun.EntryImpl(gstem, firstmf, Gender.f,
                                         m::get);
        Assert.assertTrue(Iterables.elementsEqual(nsi, entry.getForm(NOMSI)));
    }

    @Test
    public void testIncompleteRulesRenamed() {
        StringForm nsi = new StringForm("poof", "poof");
        Map<CaseNumber,StringForm> m = Maps.newHashMap();
        m.put(NOMSI, nsi);
        LatinNoun.EntryImpl entry =
            new LatinNoun.EntryImpl(gstem, secondn, Gender.n,
                                         m::get);
        Assert.assertTrue(Iterables.elementsEqual(nsi, entry.getForm(ACCSI)));
    }

    void showSimpleNoun(SimpleNoun simpleNoun) {
        logger.info(simpleNoun.pathId.toString() + " " + simpleNoun.gender.toString() + " " + simpleNoun.rules.pathId.toString());
        for (CaseNumber cn : CaseNumber.values()) {
            Form form = simpleNoun.latinEntry.getForm(cn);
            if (form == null) {
                logger.info(cn.toString() + " null");
            }
            else {
                logger.info(cn.toString() + " " + form.toString());
            }
        }
        logger.info("");
    }

    @Test
    public void testLoad() throws Exception {
        File[] files = new File(SimpleNoun.WordsDirectory, "nouns").listFiles();
        for (File file : files) {
            showSimpleNoun(SimpleNoun.fromXML(file));
        }
        /*
        showSimpleNoun(SimpleNoun.fromXML("aqua.xml"));
        showSimpleNoun(SimpleNoun.fromXML("fīlius.xml"));
        showSimpleNoun(SimpleNoun.fromXML("mare.xml"));
        showSimpleNoun(SimpleNoun.fromXML("bellum.xml"));
        showSimpleNoun(SimpleNoun.fromXML("dominus.xml"));
        showSimpleNoun(SimpleNoun.fromXML("rex.xml"));
        */
    }

}