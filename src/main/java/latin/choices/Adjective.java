
package  latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.Rule;
import latin.util.PathId;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Adjective {

    static CaseNumber nsiKey = CaseNumber.NomSi;
    static Gender mKey = Gender.m;

    public static <T> T getNsiValue(List<T> tList, CaseNumber cn, Gender g) {
        if (cn.equals(nsiKey)) {
            return g.select(tList);
        } else {
            return null;
        }
    }

    public interface Entry {

        Form getForm(CaseNumber cn, Gender g);
    }

    interface Rules {

        Rule getRule(CaseNumber cn, Gender g);
    }

    static class RulesList implements Rules {

        List<LatinNoun.Rules> rulesList;

        public RulesList(List<LatinNoun.Rules> rulesList) {
            Preconditions.checkArgument(!rulesList.isEmpty());
            this.rulesList = rulesList;
        }

        public Rule getRule(CaseNumber cn, Gender g) {
            return g.select(rulesList).get(cn);
        }
    }

    static RulesList makeRulesList(String... ruleNames) {
        List<LatinNoun.Rules> ruleses = Lists.newArrayList();
        for (String rn : ruleNames) {
            ruleses.add(LatinNoun.getRules(rn));
        }
        return new RulesList(ruleses);
    }

    static class NsiList<V> implements BiFunction<CaseNumber, Gender, V> {

        List<V> items;

        public NsiList(List<V> items) {
            this.items = items;
        }

        public V apply(CaseNumber cn, Gender g) {
            if (cn.equals(nsiKey)) {
                return g.select(items);
            } else {
                return null;
            }
        }
    }

    static class NsiRules implements Rules {

        List<Rule> rules;

        NsiRules(List<Rule> rules) {
            this.rules = rules;
        }

        public Rule getRule(CaseNumber cn, Gender g) {
            if (cn.equals(nsiKey)) {
                return g.select(rules);
            } else {
                return null;
            }
        }
    }

    public static RulesList firstSecond = makeRulesList("Second.mf", "First.mf", "Second.n");
    public static RulesList us_a_um = makeRulesList("Second.us", "First.a", "Second.um");
    public static RulesList third = makeRulesList("Third.mf.pi", "Third.n.i");
    public static RulesList cmpRules = makeRulesList("Third.mf", "Third.n");

    static abstract class AbstractEntry implements LatinNounForms.GstemFormFunction, Entry {

        Form stem;

        public AbstractEntry(Form stem) {
            this.stem = stem;
        }

        public Form getGstem() {
            return stem;
        }

        public Form getForm(CaseNumber cn, Gender g) {
            return LatinNounForms.getForm(this, cn, g);
        }
    }

    static class PosEntry extends AbstractEntry {

        Rules rules;
        BiFunction<CaseNumber, Gender, Form> irregularForms;

        public PosEntry(Form stem, Rules rules, BiFunction<CaseNumber, Gender, Form> irregularForms) {

            super(stem);
            this.rules = rules;
            this.irregularForms = irregularForms;
        }

        @Override
        public Rule getGstemRule(CaseNumber cn, Gender g) {
            return rules.getRule(cn, g);
        }

        @Override
        public Form getIrregularForm(CaseNumber cn, Gender g) {
            return irregularForms.apply(cn, g);
        }
    }

    static class SupEntry extends AbstractEntry {

        public SupEntry(Form stem) {
            super(stem);
        }

        @Override
        public Rule getGstemRule(CaseNumber cn, Gender g) {
            return us_a_um.getRule(cn, g);
        }
    }

    static abstract class IrregularRulesEntry extends AbstractEntry {

        Rule getIrregularRule(CaseNumber cn, Gender g) {
            return null;
        }

        public IrregularRulesEntry(Form stem) {
            super(stem);
        }

        @Override
        public Form getIrregularForm(CaseNumber cn, Gender g) {
            Rule r = getIrregularRule(cn, g);
            if (r != null) {
                return r.apply(stem);
            } else {
                return null;
            }
        }
    }

    public static abstract class IrregularNsiHandler<E> implements LatinNounForms.Handler<E> {

        abstract List<Rule> getNsiRules(E e);

        abstract Form getStem(E e);

        abstract Rule getGstemMod(E e);

        @Override
        public Form getIrregularForm(E e, CaseNumber cn, Gender g) {
            if (cn.equals(nsiKey)) {
                Rule r = g.select(getNsiRules(e));
                if (r != null) {
                    return r.apply(getStem(e));
                }
            }
            return null;
        }

        @Override
        public Form getGstem(E e) {
            return getGstemMod(e).apply(getStem(e));
        }
    }

    public static class CmpEntry extends IrregularRulesEntry {

        static PathId pathId = PathId.makeRoot("CmpEntry");
        static Rule gstemMod = new ModRule(pathId.makeChild("gstemMod"), "iōr");
        static PathId nsiPathId = pathId.makeChild("NomSi");

        static Rule makeNsiRule(Gender g, String ms) {
            return new ModRule(nsiPathId.makeChild(g), ms);
        }

        static NsiList<Rule> nsiRules = new NsiList<>(Lists.newArrayList(makeNsiRule(Gender.mf, "ior"), makeNsiRule(Gender.n, "ius")));
        static List<Rule> nsiRulesList =
            Lists.newArrayList(makeNsiRule(Gender.mf, "ior"), makeNsiRule(Gender.n, "ius"));

        public CmpEntry(Form stem) {
            super(stem);
        }

        @Override
        public Form getGstem() {
            return gstemMod.apply(stem);
        }

        @Override
        public Rule getGstemRule(CaseNumber cn, Gender g) {
            return cmpRules.getRule(cn, g);
        }

        @Override
        public Rule getIrregularRule(CaseNumber cn, Gender g) {
            return nsiRules.apply(cn, g);
        }
    }


    /*

    static {
        makeRules("first.second", "Second.mf", "First.mf", "Second.n");
        makeRules("us.a.um", "Second.us", "First.a", "Second.um");
        makeRules("r.a.um", "Second.r", "First.a", "Second.um");
        makeRules("er.a.um", "Second.er", "First.a", "Second.um");
        makeRules("third", "Third.adj.mf", "Third.adj.n");
    }

    */

    static abstract class AdjectiveEntry {

        Form stem;
        List<Form> posNsiForms;
        Rules posRules;

        public Form getHandlerForm(LatinNounForms.Handler<AdjectiveEntry> h, CaseNumber cn, Gender g) {
            return h.getForm(this, cn, g);
        }

        public Form getPosForm(CaseNumber cn, Gender g) {
            return getHandlerForm(posHandler, cn, g);
        }

        public Form getCmpStem() {
            return stem;
        }

        abstract public Form getSupStem();

        public Form getCmpForm(CaseNumber cn, Gender g) {
            return getHandlerForm(cmpHandler, cn, g);
        }

        public Form getSupForm(CaseNumber cn, Gender g) {
            return getHandlerForm(supHandler, cn, g);
        }
    }

    static Rule regSupStemMod = new ModRule(PathId.makePath("regularSup", "stemMod"), "issum");

    public class RegularAdjectiveEntry extends AdjectiveEntry {

        public Form getSupStem() {
            return regSupStemMod.apply(stem);
        }
    }

    static Rule rstemSupStemMod = new ModRule(PathId.makePath("rstemSup", "stemMod"), "rim");

    static class RstemAdjectiveEntry extends AdjectiveEntry {

        public Form getSupStem() {
            Form stem = posHandler.getKeyForm(this, nsiKey, mKey);
            return rstemSupStemMod.apply(stem);
        }
    }

    static class IrregularAdjectiveEntry extends AdjectiveEntry {

        Form cmpStem;
        Form supStem;

        public Form getCmpStem() {
            return cmpStem;
        }

        public Form getSupStem() {
            return supStem;
        }
    }

    static LatinNounForms.KeyFormHandler<AdjectiveEntry> posIrregularHandler =
        new LatinNounForms.KeyFormHandler<AdjectiveEntry>() {
            @Override
            public Form getKeyForm(AdjectiveEntry e, CaseNumber cn, Gender g) {
                return cn.equals(nsiKey) ? g.select(e.posNsiForms) : null;
            }
        };

    static LatinNounForms.RuleHandler<AdjectiveEntry> posRuleHandler =
        new LatinNounForms.RuleHandler<AdjectiveEntry>() {
            @Override
            public Form getStem(AdjectiveEntry e) {
                return e.stem;
            }

            @Override
            public Rule getRule(AdjectiveEntry e, CaseNumber cn, Gender g) {
                return e.posRules.getRule(cn, g);
            }
        };

    static LatinNounForms.Handler<AdjectiveEntry> posHandler =
        new LatinNounForms.PairedHandler<>(posIrregularHandler, posRuleHandler);

    static LatinNounForms.Handler<AdjectiveEntry> cmpHandler =
        new LatinNounForms.Handler<AdjectiveEntry>() {
            LatinNounForms.StringRuleHandler nsiHandler =
                new LatinNounForms.StringRuleHandler() {
                    List<Rule> nsiRules = Lists
                        .newArrayList(new ModRule(PathId.makePath("mf"), "ior"),
                                      new ModRule(PathId.makePath("n"), "ius"));

                    @Override
                    public Rule getRule(Form e, CaseNumber cn, Gender g) {
                        return cn.equals(nsiKey) ? g.select(nsiRules) : null;
                    }
                };
            LatinNounForms.StringRuleHandler gstemHandler =
                new LatinNounForms.StringRuleHandler() {
                    Rule gstemMod = new ModRule(PathId.makePath("CmpHandler", "gstemMod"), "iōr");
                    @Override
                    public Form getStem(Form e) {
                        return gstemMod.apply(e);
                    }
                    @Override
                    public Rule getRule(Form e, CaseNumber cn, Gender g) {
                        return cmpRules.getRule(cn, g);
                    }
                };
            LatinNounForms.PairedHandler<Form> formHandler =
                new LatinNounForms.PairedHandler<Form>(nsiHandler, gstemHandler);
            @Override
            public Form getKeyForm(AdjectiveEntry adjectiveEntry, CaseNumber cn, Gender g) {
                return formHandler.getKeyForm(adjectiveEntry.getCmpStem(), cn, g);
            }
        };

    static LatinNounForms.Handler<AdjectiveEntry> supHandler =
        new LatinNounForms.Handler<AdjectiveEntry>() {
            LatinNounForms.StringRuleHandler formHandler =
                new LatinNounForms.StringRuleHandler() {
                    @Override
                    public Rule getRule(Form form, CaseNumber cn, Gender g) {
                        return us_a_um.getRule(cn, g);
                    }
                };
            @Override
            public Form getKeyForm(AdjectiveEntry e, CaseNumber cn, Gender g) {
                return formHandler.getKeyForm(e.getSupStem(), cn, g);
            }
        };
    };



}

