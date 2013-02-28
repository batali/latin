
package latin.forms;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import latin.choices.CollectAlts;
import org.junit.Test;

import java.util.List;

public class FormRuleTest {

    public List<String> getForms(Rulef formf) {
        List<String> formList = Lists.newArrayList();
        CollectAlts collectAlts = new CollectAlts();
        do {
            FormBuilder formBuilder = new FormBuilder();
            if (formf.apply(formBuilder, collectAlts)) {
                formList.add(formBuilder.toString());
            }
        }
        while(collectAlts.incrementPositions());
        return formList;
    }

    public List<String> getForms(List<Rulef> rules) {
        return getForms(new FormRule.SequenceRule(rules));
    }

    public List<String> getForms(Rulef formf1, Rulef formf2) {
        return getForms(Lists.newArrayList(formf1, formf2));
    }

    @Test
    public void testStringFormf() {
        Rulef ff1 = FormRule.parseRule("", "ff1", "foo");
        Assert.assertEquals("[foo]", getForms(ff1).toString());
        Rulef ff2 = FormRule.parseRule("", "ff2", "bar");
        Assert.assertEquals("[foobar]", getForms(ff1,ff2).toString());
        Rulef ff3 = FormRule.parseRule("", "ff3", "eepers__jeepers");
        Assert.assertEquals("[eepers jeepers]", getForms(ff3).toString());
    }

    @Test
    public void testModFormf() {
        Rulef ff1 = FormRule.parseRule("", "ff1", "boa");
        Assert.assertEquals("[boā]", getForms(ff1, FormRule.lengthenLast).toString());
        Assert.assertEquals("[boa]", getForms(ff1, FormRule.shortenLast).toString());
        Assert.assertEquals("[bo]", getForms(ff1, FormRule.removeLast).toString());
        Rulef ff2 = FormRule.parseRule("", "ff2", "ibē");
        Assert.assertEquals("[ibē]", getForms(ff2, FormRule.lengthenLast).toString());
        Assert.assertEquals("[ibe]", getForms(ff2, FormRule.shortenLast).toString());
        Assert.assertEquals("[balf]", getForms(ff2, FormRule.parseRule("", "ff2d", "---balf")).toString());
    }

    @Test
    public void testAltFormf() {
        Rulef ff1 = FormRule.parseRule("", "ff1", "fe,fi,fo");
        Assert.assertEquals("[fe, fi, fo]", getForms(ff1).toString());
        Rulef ff2 = FormRule.parseRule("", "ff2", "foo,bar");
        Assert.assertEquals("[fefoo, fifoo, fofoo, febar, fibar, fobar]", getForms(ff1, ff2).toString());
    }

    @Test
    public void testNoops() {
        Rulef r1 = FormRule.parseRule("", "r1", ":");
        System.out.println("r1 " + r1.toString());
    }




}