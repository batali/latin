
package latin.forms;

import com.google.common.collect.Lists;

import org.junit.Test;

import latin.choices.CaseNumber;
import latin.choices.CollectAlts;
import latin.choices.Noun;
import latin.choices.NounForms;
import latin.choices.Values;

import java.util.List;
import java.util.Map;

public class NounRulesTest {

    public void showErules(NounForms.Erules rules) {
        System.out.println("Erules " + rules.name);
        for (CaseNumber key : CaseNumber.values()) {
            Rulef rule = rules.getRule(key);
            if (rule != null) {
                System.out.println(key.toString() + " " + rule.toString());
            }
        }
    }

    @Test
    public void testErules() throws Exception {
        for (NounForms.Erules rules : NounForms.getAllRules()) {
            showErules(rules);
        }
    }

    List<String> allForms(Noun.NE e, CaseNumber key) {
        CollectAlts collector = new CollectAlts();
        List<String> rlist = Lists.newArrayList();
        do {
            Token t = e.getForm(key, collector);
            if (t != null) {
                rlist.add(t.toString());
            }
        }
        while(collector.incrementPositions());
        return rlist;
    }

    Values<TokenRule> findRules(NounForms.Rules rules, CaseNumber key) {
        Values<TokenRule> rl = rules.getRules(key);
        if (rl != null) {
            return rl;
        }
        else {
            return ((key = Noun.renamed.get(key)) != null) ? findRules(rules, key) : null;
        }
    }

    public void showRules(NounForms.Rules rules) {
        System.out.println(rules.name);
        for (CaseNumber key : CaseNumber.values()) {
            Values<TokenRule> rl = findRules(rules, key);
            if (rl != null) {
                System.out.println(String.format("\t %s : %s %s",
                                                 key.toString(),
                                                 TokenRules.rulesToString(rl),
                                                 rl.getId()));
            }
        }
    }

    @Test
    public void testShowRules() throws Exception {
        for (Map.Entry<String,NounForms.Rules> e : NounForms.rulesMap.entrySet()) {
            showRules(e.getValue());
        }
    }

    public void showApplyRules(Noun.NE e) {
        for (CaseNumber key : CaseNumber.values()) {
            List<String> fl = allForms(e, key);
            System.out.println(String.format("\t %s : %s",
                                             key.toString(),
                                             fl.toString()));
        }
    }

    void showApply(String gst, String rn) {
        Noun.NE e = Noun.makeEntry(gst, rn);
        System.out.println(e.getSpec());
        showApplyRules(e);
    }

    @Test
    public void testApplyComplete() throws Exception {
        showApply("femin", "first.a");
        System.out.println("");
        showApply("fili", "second.ius");
        System.out.println("");
        showApply("rex", "reg", "third.c.mf");
        System.out.println("");
        showApply("urbs", "urb", "third.mi.n");
        System.out.println("");
        showApply("mare", "mar", "third.pi.n");
    }

    void showApply(String nsi, String gst, String rn) {
        Noun.NE e = Noun.makeEntry(nsi, gst, rn);
        System.out.println(e.getSpec());
        showApplyRules(e);
    }






}