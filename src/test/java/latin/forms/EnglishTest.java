
package latin.forms;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import latin.choices.Alts;
import latin.choices.Completeness;
import latin.choices.PersonNumber;
import latin.choices.Time;
import latin.choices.Voice;
import org.junit.Test;

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

    public void showVerbGroups(English.VerbForms fv, PersonNumber pn, Time tm, Completeness cm, Voice v) {
        List<String> strings = English.getVerbGroup(fv, pn, tm, cm, v, Alts.firstAlt);
        System.out.println(String.format("%s %s %s %s : %s",
                tm.toString(),
                cm.toString(),
                pn.toString(),
                v.toString(),
                strings.toString()));
    }

    public void showVerbGroups(English.VerbForms fv) {
        for (Time tm : Time.values()) {
            for (Completeness cm : Completeness.values()) {
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