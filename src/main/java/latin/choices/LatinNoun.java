package latin.choices;

import com.google.common.collect.Maps;

import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.Rule;
import latin.forms.StringForm;
import latin.util.Splitters;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

class LatinNoun {

    static CaseNumber getRenamed(CaseNumber key, Gender gender) {
        if (key.equals(CaseNumber.AblPl)) {
            return CaseNumber.DatPl;
        }
        if (key.caseKey.isVocative()) {
            return key.toCase(Case.Nom);
        }
        if (key.caseKey.isLocative()) {
            return key.toCase(Case.Abl);
        }
        if (gender.isNeuter()) {
            if (key.caseKey.isAccusative()) {
                return key.toCase(Case.Nom);
            }
        }
        return null;
    }

    interface FormEntry {

        Form getIrregularForm(CaseNumber caseNumber, Gender gender);

        Form getGstem();

        Rule getGstemRule(CaseNumber caseNumber, Gender gender);
    }

    public static class NounEntry implements FormEntry {
        Object id;
        Rules rules;
        Form gstem;
        Gender gender;
        Map<CaseNumber,Form> stored;
        public NounEntry (Object id) {
            this.id = id;
            this.stored = Maps.newHashMap();
        }
        public NounEntry() {
            this.id = this;
            this.stored = Maps.newHashMap();
        }
        public NounEntry setRules(Rules rules) {
            this.rules = rules;
            return this;
        }
        public NounEntry setRules(String dn, String subname) {
            Declension declension = Declension.valueOf(dn);
            return setRules(declension.getRules(subname));
        }
        public NounEntry setRules(String rs) {
            int p = rs.indexOf('.');
            setRules(rs.substring(0,p), rs.substring(p+1));
            return this;
        }
        public Object makeFormId(Object key) {
            return this.id + "." + key.toString();
        }
        public NounEntry setGstem(String cs) {
            this.gstem = new StringForm(makeFormId("gstem"), cs);
            return this;
        }
        public NounEntry setGender(String gs) {
            this.gender = Gender.fromString(gs);
            return this;
        }
        public NounEntry storeForm(String ks, String vs) {
            CaseNumber key = CaseNumber.fromString(ks);
            stored.put(key, new StringForm(makeFormId(key), vs));
            return this;
        }
        public NounEntry stored(String string) {
            for (String ss : Splitters.ssplitter(string)) {
                Splitters.esplit(ss, this::storeForm);
            }
            return this;
        }

        public NounEntry setAttribute(String ks, String vs) {
            if (ks.equals("id")) {
                this.id = vs;
            }
            else if (ks.equals("rules")) {
                setRules(vs);
            }
            else if (ks.equals("gender")) {
                setGender(vs);
            }
            else if (ks.equals("gstem")) {
                setGstem(vs);
            }
            else if (ks.equals("NomSi")) {
                storeForm(ks, vs);
            }
            else {
                throw new IllegalStateException("Bad attribute " + ks + " " + vs);
            }
            return this;
        }

        @Override
        public Form getIrregularForm(CaseNumber caseNumber, Gender gender) {
            return stored.get(caseNumber);
        }
        @Override
        public Form getGstem() {
            return gstem;
        }
        @Override
        public Rule getGstemRule(CaseNumber caseNumber, Gender gender) {
            return rules.getRule(caseNumber);
        }
        public Form getForm(CaseNumber caseNumber) {
            return LatinNoun.getForm(this, caseNumber, gender);
        }
    }

    public static Form getForm(FormEntry e, CaseNumber cn, Gender g) {
        Form i = e.getIrregularForm(cn, g);
        if (i != null) {
            return i;
        }
        Rule r = e.getGstemRule(cn, g);
        if (r != null) {
            return r.apply(e.getGstem());
        }
        CaseNumber rcn = getRenamed(cn, g);
        if (rcn != null) {
            return getForm(e, rcn, g);
        }
        return null;
    }

    public static class RulesId {

        public final Declension declension;
        public final String subname;
        public final String name;

        RulesId(Declension declension, String subname) {
            this.declension = declension;
            this.subname = subname;
            this.name = declension.toString() + "." + subname;
        }

        public Object makeRuleId(CaseNumber key) {
            return name + "." + key.toString();
        }

        public String toString() {
            return name;
        }

        public Declension getDeclension() {
            return declension;
        }

        public String getSubname() {
            return subname;
        }
    }

    public interface Rules {

        public RulesId getRulesId();

        public Rule getRule(CaseNumber caseNumber);

        public Set<Map.Entry<CaseNumber, Rule>> entrySet();
    }

    public static class MapRules extends EnumMap<CaseNumber, Rule> implements Rules {

        final RulesId rulesId;

        public MapRules(RulesId rulesId) {
            super(CaseNumber.class);
            this.rulesId = rulesId;
        }

        @Override
        public RulesId getRulesId() {
            return rulesId;
        }

        @Override
        public Rule getRule(CaseNumber caseNumber) {
            return get(caseNumber);
        }

        void putAll(Rules rules) {
            for (Map.Entry<CaseNumber, Rule> e : rules.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }

        void addRule(String ks, String vs) {
            CaseNumber key = CaseNumber.fromString(ks);
            put(key, new ModRule(rulesId.makeRuleId(key), vs));
        }
    }

    public static void printRules(Rules rules) {
        System.out.println(rules.getRulesId().toString());
        for (Map.Entry<CaseNumber, Rule> e : rules.entrySet()) {
            System.out.println(e.getKey().toString() + ": " + e.getValue().toString());
        }
    }

}