
package latin.forms;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.junit.Test;

import junit.framework.Assert;
import latin.choices.Alts;
import latin.choices.Aspect;
import latin.choices.PersonNumber;
import latin.choices.Time;
import latin.choices.Voice;
import latin.util.PathId;

import java.util.List;

public class EnglishTest {

    public void testNounForms(String bs, String ts) throws Exception {
        English.NounForms nf = English.nounForms(bs);
        List<String> cforms = Lists.newArrayList();
        for (English.NounKey key : English.NounKey.values()) {
            cforms.add(Strings.nullToEmpty(Forms.formString(nf.getForm(key, Alts.firstAlt))));
        }
        List<String> tforms = Suffix.ssplit(ts);
        Assert.assertEquals(tforms, cforms);
    }

    @Test
    public void testNoun() throws Exception {
        testNounForms("car", "car cars car's");
        testNounForms("try", "try tries try's");
        testNounForms("man men", "man men man's");
        testNounForms("house", "house houses house's");
    }

    void checkPluralize(String ns, String ts) {
        PathId root = PathId.makeRoot("c");
        Form stem = new StringForm(root.makeChild("s"), ns);
        Form target = new StringForm(root.makeChild("t"), ts);
        Form pl = English.addS(stem);
        Assert.assertEquals(target.asList(), pl.asList());
    }

    static Form makeForm(String fs) {
        return new StringForm(PathId.makeRoot(fs), fs);
    }

    @Test
    public void testPluralize() throws Exception {
        checkPluralize("dog", "dogs");
        checkPluralize("try", "tries");
        checkPluralize("pass", "passes");
    }

    void checkD (String ss, String ts) {
        Form stem = makeForm(ss);
        Form target = makeForm(ts);
        Form dform = English.addD(stem);
        Assert.assertEquals(target.asList(), dform.asList());
    }

    @Test
    public void testD() {
        checkD("row", "rowed");
        checkD("hop", "hopped");
        checkD("chase", "chased");
        checkD("dry", "dries");
    }

    public void testVerbForms(String bs, String ts) throws Exception {
        English.VerbForms vf = English.verbForms(bs);
        List<String> cforms = Lists.newArrayList();
        for (English.VerbKey key : English.VerbKey.values()) {
            cforms.add(Strings.nullToEmpty(Forms.formString(vf.getKeyForm(key, Alts.firstAlt))));
        }
        List<String> tforms = Suffix.ssplit(ts);
        Assert.assertEquals(tforms, cforms);
    }

    @Test
    public void testVerb() throws Exception {
        testVerbForms("stop", "stop stops stopped stopped stopping");
        testVerbForms("eat ate eaten", "eat eats ate eaten eating");
        testVerbForms("show shown", "show shows showed shown showing");
        testVerbForms("have has had", "have has had had having");
        testVerbForms("put put", "put puts put put putting");
        testVerbForms("flee fled", "flee flees fled fled fleeing");
        testVerbForms("row", "row rows rowed rowed rowing");
        testVerbForms("say said", "say says said said saying");
        testVerbForms("dry", "dry dries dried dried drying");
    }


    public void showVerbGroups(English.VerbForms fv, PersonNumber pn, Time tm, Aspect cm, Voice v) {
        IPhrase vbp = English.getVerbGroup(fv, pn, tm, cm, v, Alts.firstAlt);
        System.out.println(Joiner.on('.').join(tm, cm, v, pn) + " : " + Forms.printPhrase(vbp).toString());
    }

    public void showVerbGroups(English.VerbForms fv) {
        for (Time tm : Time.values()) {
            for (Aspect cm : Aspect.values()) {
                for (Voice v : Voice.values()) {
                    for (PersonNumber pn : PersonNumber.values()) {
                        showVerbGroups(fv, pn, tm, cm, v);
                    }
                }
            }
        }
    }

    @Test
    public void testVerbGroup() throws Exception {
    //    English.VerbForms vf = English.verbForms("eat ate eaten");
        English.VerbForms vf = English.verbForms("see saw seen");
        showVerbGroups(vf);
    }

}