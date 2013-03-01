
package  latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import latin.forms.FieldStemf;
import latin.forms.FormMap;
import latin.forms.Formf;
import latin.forms.IForm;
import latin.forms.Rulef;
import latin.forms.Stemf;
import latin.forms.StoredForms;
import latin.forms.Suffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Adjective {

    public interface Stored {
        public Formf getStored(CaseNumber caseNumber, Gender gender);
        public StoredForms<CaseNumber> toNounForms(Gender gender);
    }

    public interface Rules {
        public Rulef getRule(CaseNumber caseNumber, Gender gender);
        public Noun.Rules toNounRules (Gender gender);
    }

    public static Stored emptyForms = new Stored () {

        @Override
        public Formf getStored(CaseNumber caseNumber, Gender gender) {
            return null;
        }

        @Override
        public StoredForms<CaseNumber> toNounForms(Gender gender) {
            return latin.choices.Noun.emptyForms;
        }

    };

    public static StoredForms<CaseNumber> makeNounForms(final Stored forms, final Gender gender) {
        if (forms == null) {
            return null;
        }
        StoredForms<CaseNumber> nounForms = forms.toNounForms(gender);
        if (nounForms != null) {
            return nounForms;
        }
        return new StoredForms<CaseNumber>() {
            @Override
            public Formf getStored(CaseNumber caseNumber) {
                return forms.getStored(caseNumber, gender);
            }
        };
    }

    public static Noun.Rules makeNounRules(final Rules rules, final Gender gender) {
        Noun.Rules nounRules = rules.toNounRules(gender);
        if (nounRules != null) {
            return nounRules;
        }
        return new Noun.Rules () {
            @Override
            public Rulef getRule(CaseNumber caseNumber) {
                return rules.getRule(caseNumber, gender);
            }
        };
    }

    public static <ET> IForm getAdjForm(CaseNumber caseNumber,
                                               Gender gender,
                                               Stored forms,
                                               Stemf<ET> stemf,
                                               ET stemEntry,
                                               Rules rules,
                                               Alts.Chooser chooser) {
        return Noun.getForm(
                caseNumber,
                makeNounForms(forms, gender),
                stemf,
                stemEntry,
                makeNounRules(rules, gender),
                chooser);
    }


    public static class ListStoredForms implements Stored {
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

        public FormMap<CaseNumber> selectFormMap(Gender gender) {
            return gender.select(storedFormsList);
        }

        @Override
        public Formf getStored(CaseNumber caseNumber, Gender gender) {
            return toNounForms(gender).getStored(caseNumber);
        }

        @Override
        public FormMap<CaseNumber> toNounForms(Gender gender) {
            return gender.select(storedFormsList);
        }

        public void putForm(Gender g, CaseNumber key, List<String> strings) {
            Preconditions.checkState(!storedFormsList.isEmpty());
            toNounForms(g).putForm(id + "." + g.toString(), key, strings);
        }

        public void putForm(Gender g, CaseNumber key, String afs) {
            putForm(g, key, Suffix.csplit(afs));
        }

        public FormMap<CaseNumber> getStoredForms(int p) {
            return storedFormsList.get(p);
        }
    }

    public static class ListRules implements Rules {
        public final String name;
        private List<NounForms.Erules> rulesList;
        public ListRules(String name, List<NounForms.Erules> rulesList) {
            this.name = name;
            this.rulesList = rulesList;
        }
        public Noun.Rules toNounRules(Gender gender) {
            return gender.select(rulesList);
        }
        public Rulef getRule(CaseNumber caseNumber, Gender gender) {
            return toNounRules(gender).getRule(caseNumber);
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
        public IForm getForm(CaseNumber caseNumber, Gender gender, Alts.Chooser chooser) {
            return getAdjForm(caseNumber, gender, storedForms, adjStemf, this, rules, chooser);
        }
    }

    public static Stemf<FormEntry> adjStemf =
            new FieldStemf<FormEntry>(FormEntry.class, "gstemf");

}


