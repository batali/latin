package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.StringForm;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.EnumMap;
import java.util.Map;

public class LatinNoun {

    public static final CaseNumber NOMSI = CaseNumber.NomSi;

    public interface IEntry extends LatinNounForms.GstemRulesFormEntry {

        String getId();
        Gender getGender();
        Map<CaseNumber,StringForm> getStoredMap();
        Map<CaseNumber,ModRule> getRuleMap();
        String getFeature(String key);

        default Form getIrregularForm(CaseNumber cn, Gender g) {
            Map<CaseNumber,StringForm> m = getStoredMap();
            return m != null ? m.get(cn) : null;
        }

        default ModRule getGstemRule(CaseNumber cn, Gender g) {
            return getRuleMap().get(cn);
        }

        default Form getForm(CaseNumber cn) {
            return getNounForm(cn, getGender());
        }
    }

    static class EntryImpl implements IEntry {

        String id;
        Form gstem;
        Map<CaseNumber, StringForm> stored;
        Declension.Rules rules;
        Map<String,String> features;
        Gender gender;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getFeature(String key) {
            if (features == null) {
                return null;
            }
            return features.get(key);
        }

        @Override
        public Form getGstem() {
            Preconditions.checkNotNull(gstem);
            return gstem;
        }

        @Override
        public Map<CaseNumber, StringForm> getStoredMap() {
            return stored;
        }

        @Override
        public Map<CaseNumber, ModRule> getRuleMap() {
            return rules;
        }

        @Override
        public Gender getGender() {
            Preconditions.checkNotNull(gender);
            return gender;
        }
    }

    static class EntryBuilder {

        EntryImpl entry;
        PathId pathId;

        public EntryBuilder(String id) {
            this.entry = new EntryImpl();
            this.entry.id = id;
            this.pathId = PathId.makeRoot(id);
        }

        public EntryBuilder setGstem(Form gstem) {
            entry.gstem = gstem;
            return this;
        }

        Map<CaseNumber, StringForm> getStoredMap() {
            Map<CaseNumber, StringForm> storedMap = entry.stored;
            if (storedMap == null) {
                storedMap = new EnumMap<CaseNumber, StringForm>(CaseNumber.class);
                entry.stored = storedMap;
            }
            return storedMap;
        }

        StringForm makeStringForm(Object key, ImmutableList<String> strings) {
            return new StringForm(pathId.makeChild(key), strings);
        }

        StringForm makeStringForm(Object key, String fs) {
            return makeStringForm(key, StringForm.split(fs));
        }

        public EntryBuilder setStoredForm(CaseNumber cn, StringForm form) {
            getStoredMap().put(cn, form);
            return this;
        }

        public EntryBuilder setGender(Gender gender) {
            entry.gender = gender;
            return this;
        }

        public EntryBuilder setRules(Declension.Rules rules) {
            entry.rules = rules;
            if (rules.get(NOMSI) == null && getStoredMap().get(NOMSI) == null) {
                setStoredForm(NOMSI, makeStringForm(NOMSI, ImmutableList.<String>of(entry.id)));
            }
            return this;
        }

        public EntryBuilder setRules(String dname, String subname) {
            Declension declension = Declension.getDeclension(dname);
            return setRules(declension.getRules(subname, true));
        }

        Map<String, String> getFeaturesMap() {
            Map<String, String> featuresMap = entry.features;
            if (featuresMap == null) {
                featuresMap = Maps.newHashMap();
                entry.features = featuresMap;
            }
            return featuresMap;
        }

        public EntryBuilder setFeature(String key, String value) {
            getFeaturesMap().put(key, value);
            return this;
        }

        public EntryBuilder set(String ks, String vs) {
            if (ks.equals("gstem")) {
                setGstem(makeStringForm(ks, vs));
            } else if (ks.equals("rules")) {
                Splitters.psplit(vs, this::setRules);
            } else if (ks.equals("gender")) {
                setGender(Gender.fromString(vs));
            } else {
                CaseNumber cn = CaseNumber.fromString(ks, false);
                if (cn != null) {
                    setStoredForm(cn, makeStringForm(cn, vs));
                } else {
                    setFeature(ks, vs);

                }
            }
            return this;
        }

        public EntryBuilder add(String... strings) {
            for (String string : strings) {
                Splitters.essplit(string, this::set);
            }
            return this;
        }

        public IEntry build() {
            return entry;
        }
    }

    public static EntryBuilder entryBuilder(String id) {
        return new EntryBuilder(id);
    }

    public static EntryBuilder entryBuilder(String nsi, String gst, String rs, String gs) {
        EntryBuilder builder = entryBuilder(nsi);
        builder.set("gstem", gst);
        builder.set("rules", rs);
        builder.set("gender", gs);
        return builder;
    }

}
