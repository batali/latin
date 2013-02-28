
package latin.forms;

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

    public static <K extends Enum<K>> List<String> getKeyForms(Form.Forms<K> forms, Class<K> kclass) throws Exception {
        List<String> fstrings = Lists.newArrayList();
        for (K k : kclass.getEnumConstants()) {
            FormBuilder fb = new FormBuilder();
            if (forms.getForm(k, fb, Alts.firstAlt)) {
                fstrings.add(fb.getForm());
            }
        }
        return fstrings;
    }

    public static <K extends Enum<K>> void testKeyForms(Form.Forms<K> forms, Class<K> kclass, List<String> tstrings) throws Exception {
        List<String> fstrings = getKeyForms(forms, kclass);
        Assert.assertEquals(tstrings, fstrings);
    }

    public void testNounForms(String bs, String ts) throws Exception {
        testKeyForms(English.nounForms(bs), English.NounKey.class, Suffix.ssplit(ts));
    }

    @Test
    public void testNoun() throws Exception {
        testNounForms("car", "car cars car's");
        testNounForms("try", "try tries try's");
        testNounForms("man men", "man men man's");
        testNounForms("house", "house houses house's");
    }

    public void testVerbForms(String bs, String ts) throws Exception {
        testKeyForms(English.verbForms(bs), English.VerbKey.class, Suffix.ssplit(ts));
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
        FormBuilder fb = new FormBuilder();
        English.getVerbGroup(fv, pn, tm, cm, v, fb, Alts.firstAlt);
        System.out.println(String.format("%s %s %s %s : %s",
                tm.toString(),
                cm.toString(),
                pn.toString(),
                v.toString(),
                fb.getForm()));
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