
package  latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import latin.forms.FieldStemf;
import latin.forms.Form;
import latin.forms.FormMap;
import latin.forms.Formf;
import latin.forms.IFormBuilder;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.Suffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Adjective {

    public interface Key extends latin.choices.CaseNumber.Key, latin.choices.Gender.Key {
    }

    public interface Stored extends Form.Stored<Key> {
        public Formf getStored(latin.choices.CaseNumber caseNumber, latin.choices.Gender gender);
        public Form.Stored<latin.choices.CaseNumber> toCaseNumber(latin.choices.Gender gender);
    }

    public interface Rules extends Form.Rules<Key> {
        public Rulef getRule(latin.choices.CaseNumber caseNumber, latin.choices.Gender gender);
        public Form.Rules<latin.choices.CaseNumber> toCaseNumber(latin.choices.Gender gender);
    }

    public static Stored emptyForms = new Stored () {

        @Override
        public Formf getStored(latin.choices.CaseNumber caseNumber, latin.choices.Gender gender) {
            return null;
        }

        @Override
        public Form.Stored<latin.choices.CaseNumber> toCaseNumber(latin.choices.Gender gender) {
            return latin.choices.Noun.emptyForms;
        }

        @Override
        public Formf getStored(Key key) {
            return null;
        }
    };

    public static Form.Stored<CaseNumber> makeNounForms(final Stored forms, final Gender gender) {
        if (forms == null) {
            return null;
        }
        Form.Stored<CaseNumber> nounForms = forms.toCaseNumber(gender);
        if (nounForms != null) {
            return nounForms;
        }
        return new Form.Stored<CaseNumber> () {
            @Override
            public Formf getStored(CaseNumber caseNumber) {
                return forms.getStored(caseNumber, gender);
            }
        };
    }

    public static Form.Rules<CaseNumber> makeNounRules(final Rules rules, final Gender gender) {
        Form.Rules<CaseNumber> nounRules = rules.toCaseNumber(gender);
        if (nounRules != null) {
            return nounRules;
        }
        return new Form.Rules<CaseNumber> () {
            @Override
            public Rulef getRule(CaseNumber caseNumber) {
                return rules.getRule(caseNumber, gender);
            }
        };
    }

    public static <ET> boolean getAdjForm(CaseNumber caseNumber,
                                          Gender gender,
                                          Stored forms,
                                          Stemf<ET> stemf,
                                          ET stemEntry,
                                          Rules rules,
                                          IFormBuilder fb,
                                          Alts.Chooser chooser) {
        return NounForms.getForm(
                caseNumber,
                makeNounForms(forms, gender),
                stemf,
                stemEntry,
                makeNounRules(rules, gender),
                fb,
                chooser);
    }

    public static <ET> boolean getAdjForm(Key key,
                                          Stored forms,
                                          Stemf<ET> stemf,
                                          ET stemEntry,
                                          Rules rules,
                                          IFormBuilder fb,
                                          Alts.Chooser chooser) {
        return getAdjForm(key.getCaseNumber(), key.getGender(), forms, stemf, stemEntry, rules, fb, chooser);
    }

    public static class ListStoredForms implements Stored, Form.Stored<Key> {
        public final String id;
        private List<FormMap<CaseNumber>> storedFormsList;
        public ListStoredForms(String id, int n) {
            Preconditions.checkArgument(n >= 0 && n <= 3);
            this.id = id;
            this.storedFormsList = Lists.newArrayList();
            for (int i = 0; i < n; i++) {
                storedFormsList.add(new FormMap<CaseNumber>(CaseNumber.class));
            }
        }

        @Override
        public Formf getStored(CaseNumber caseNumber, Gender gender) {
            return toCaseNumber(gender).getStored(caseNumber);
        }

        @Override
        public FormMap<CaseNumber> toCaseNumber(Gender gender) {
            return gender.select(storedFormsList);
        }

        public void putForm(Gender g, CaseNumber key, List<String> strings) {
            Preconditions.checkState(!storedFormsList.isEmpty());
            toCaseNumber(g).putForm(id + "." + g.toString(), key, strings);
        }

        public void putForm(Gender g, CaseNumber key, String afs) {
            putForm(g, key, Suffix.csplit(afs));
        }

        public FormMap<CaseNumber> getStoredForms(int p) {
            return storedFormsList.get(p);
        }

        public Formf getStored(Key key) {
            return getStored(key.getCaseNumber(), key.getGender());
        }
    }

    public static class ListRules implements Rules, Form.Rules<Key> {
        public final String name;
        private List<NounForms.Erules> rulesList;
        public ListRules(String name, List<NounForms.Erules> rulesList) {
            this.name = name;
            this.rulesList = rulesList;
        }
        public NounForms.Erules toCaseNumber(Gender gender) {
            return gender.select(rulesList);
        }
        public Rulef getRule(CaseNumber caseNumber, Gender gender) {
            return toCaseNumber(gender).getRule(caseNumber);
        }
        public Rulef getRule(Key key) {
            return getRule(key.getCaseNumber(), key.getGender());
        }
        public boolean allComplete() {
            for (NounForms.Erules erules : rulesList) {
                if (!erules.haveNomSiRule()) {
                    return false;
                }
            }
            return true;
        }
    }

    private static Map<String,ListRules> rulesMap = Maps.newHashMap();

    private static void makeRules(String name, String... ruleNames) {
        List<NounForms.Erules> rulesList = Lists.newArrayList();
        for (int i = 0; i < ruleNames.length; i++) {
            rulesList.add(NounForms.getErules(ruleNames[i]));
        }
        ListRules listRules = new ListRules(name, rulesList);
        rulesMap.put(name, listRules);
    }

    static {
        makeRules("first.second", "Second.mf", "First.mf", "Second.n");
        makeRules("us.a.um", "Second.us", "First.a", "Second.um");
        makeRules("r.a.um", "Second.r", "First.a", "Second.um");
        makeRules("er.a.um", "Second.er", "First.a", "Second.um");
        makeRules("third", "Third.adj.mf", "Third.adj.n");
    }

    public static ListRules getRules(String name, boolean errorp) {
        ListRules rules = rulesMap.get(name);
        if (rules == null) {
            if (errorp) {
                throw new IllegalArgumentException("Unknown rules " + name);
            }
        }
        return rules;
    }

    public static ListRules getRules(String name) {
        return getRules(name, true);
    }

    public static class FormEntry {
        public final String id;
        public Formf gstemf;
        public ListRules rules;
        public ListStoredForms storedForms;
        public FormEntry(String id, String gst, String rn, List<String> nsilist) {
            this.id = id;
            this.gstemf =  Suffix.makeFormf(id, "gstem", gst);
            this.rules = getRules(rn);
            int ns = nsilist.size();
            this.storedForms = rules.allComplete() ? null : new ListStoredForms(id, ns);
            if (storedForms != null) {
                for (int i = 0; i < ns; i++) {
                    storedForms.getStoredForms(i).putForm(id + "." + i, CaseNumber.NomSi, nsilist.get(i));
                }
            }
        }
        public FormEntry(String id, String gst, String rn) {
            this(id, gst, rn, new ArrayList<String>());
        }
        public boolean getForm(CaseNumber caseNumber, Gender gender,
                               IFormBuilder formBuilder,
                               Alts.Chooser chooser) {
            return getAdjForm(caseNumber, gender, storedForms, adjStemf, this, rules, formBuilder, chooser);
        }
    }

    public static Stemf<FormEntry> adjStemf =
            new FieldStemf<FormEntry>(FormEntry.class, "gstemf");

}


