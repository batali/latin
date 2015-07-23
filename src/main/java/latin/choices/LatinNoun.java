package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import latin.forms.Form;
import latin.forms.Mod;
import latin.forms.ModRule;
import latin.forms.Rule;
import latin.forms.StringForm;
import latin.util.DomElement;
import latin.util.PathId;
import latin.util.Splitters;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    interface Rules extends Map<CaseNumber,Rule>, PathId.Identified {
    }

    interface Entry {
        Form getForm(CaseNumber caseNumber);
    }

    interface FormEntry extends Entry {

        Form getIrregularForm(CaseNumber caseNumber, Gender gender);

        Form getGstem();

        Rule getGstemRule(CaseNumber caseNumber, Gender gender);
    }

    public static class NounEntry implements Entry {

        PathId id;
        Rules rules;
        Form gstem;
        Gender gender;
        Map<CaseNumber, Form> stored;

        public NounEntry(PathId id,
                         Rules rules,
                         Form gstem,
                         Gender gender,
                         Map<CaseNumber, Form> stored) {
            this.id = id;
            this.rules = rules;
            this.gstem = gstem;
            this.gender = gender;
            this.stored = stored;
        }

        public PathId getPathId () { return id; }
        public Rules getRules() { return rules; }
        public Gender getGender() { return gender; }
        public Form getStored(CaseNumber caseNumber) {
            return stored.get(caseNumber);
        }

        public Form getForm(CaseNumber caseNumber) {
            Form sf = stored.get(caseNumber);
            if (sf != null) {
                return sf;
            }
            Rule r = rules.get(caseNumber);
            if (r != null) {
                return r.apply(gstem);
            }
            CaseNumber rcn = getRenamed(caseNumber, gender);
            if (rcn != null) {
                return getForm(rcn);
            }
            return null;
        }
    }

    public static Rules getRules(String dn, String sn){
        return Declension.valueOf(dn).getRules(sn);
    }

    public static Rules getRules(String rs) {
        int p = rs.indexOf('.');
        Preconditions.checkState(p > 0);
        return getRules(rs.substring(0, p), rs.substring(p + 1));
    }

    static final CaseNumber nsiKey = CaseNumber.NomSi;

    public static class EntryBuilder {
        PathId pathId;
        Optional<Rules> oRules;
        Optional<Form> oGstem;
        Optional<Gender> oGender;
        Map<CaseNumber,Form> stored;

        public EntryBuilder(PathId pathId) {
            this.pathId = pathId;
            this.oRules = Optional.empty();
            this.oGstem = Optional.empty();
            this.oGender = Optional.empty();
            this.stored = new EnumMap<CaseNumber,Form>(CaseNumber.class);
        }

        public EntryBuilder setRules(Rules rules) {
            this.oRules = Optional.of(rules);
            return this;
        }

        public EntryBuilder setRules(String dn, String subname) {
            return setRules(getRules(dn, subname));
        }

        public EntryBuilder setRules(String rs) {
            return setRules(getRules(rs));
        }

        StringForm makeStringForm(Object key, String vs) {
            return new StringForm(pathId.makeChild(key), vs);
        }

        public EntryBuilder setGstem(String cs) {
            this.oGstem = Optional.of(makeStringForm("gstem", cs));
            return this;
        }

        public EntryBuilder setGender(Object g) {
            Gender gender = Gender.getKey(g);
            this.oGender = Optional.of(gender);
            return this;
        }

        public EntryBuilder storeForm(Object k, String vs) {
            CaseNumber key = CaseNumber.getKey(k);
            stored.put(key, makeStringForm(key, vs));
            return this;
        }

        public EntryBuilder classify(String nsi, String gsi, String gs, String features) {
            Gender g = Gender.fromString(gs);
            setGender(g);
            String gst = null;
            Rules rules = null;
            if (gsi.endsWith("ae")) {
                gst = Mod.butLastString(gsi, 2);
                String sn = g.gkeyString();
                if (nsi.equals(gst + "a")) {
                    sn = "a";
                }
                rules = Declension.First.getRules(sn);
            } else if (gsi.endsWith("is")) {
                gst = Mod.butLastString(gsi, 2);
                String sn = g.gkeyString();
                if (!features.isEmpty()) {
                    sn += "." + features;
                }
                rules = Declension.Third.getRules(sn);
            }
            else if (nsi.endsWith("ēs") &&
                     (gsi.endsWith("ēī") || gsi.endsWith("eī"))) {
                gst = Mod.butLastString(gsi, 2);
                String sn = gsi.endsWith("eī") ? "c" : "v";
                rules = Declension.Fifth.getRules(sn);
            }
            else if (gsi.endsWith("ūs")) {
                gst = Mod.butLastString(gsi, 2);
                String sn = g.gkeyString();
                if (g.notNeuter() && nsi.endsWith("us")) {
                    sn = "us";
                }
                else if (g.isNeuter() && nsi.endsWith("ū")) {
                    sn = "u";
                }
                rules = Declension.Fourth.getRules(sn);
            }
            else if (gsi.endsWith("ī")) {
                gst = Mod.butLastString(gsi, 1);
                String sn = g.gkeyString();
                if (g.isMasculine() && nsi.equals(gst + "us")) {
                    sn = gst.endsWith("i") ? "ius" : "us";
                }
                else if (g.isNeuter() && nsi.equals(gst + "um")) {
                    sn = gst.endsWith("i") ? "ium" : "um";
                }
                else if (g.notNeuter() && nsi.endsWith("er")) {
                    if (nsi.equals(gst)) {
                        sn = "er";
                    }
                    else if (Mod.butLastString(nsi,2).equals(Mod.butLastString(gst,1))) {
                        sn = "r";
                    }
                }
                rules = Declension.Second.getRules(sn);
            }
            Preconditions.checkNotNull(gst);
            Preconditions.checkNotNull(rules);
            setGstem(gst);
            setRules(rules);
            if (rules.get(nsiKey) == null) {
                storeForm(nsiKey, nsi);
            }
            return this;
        }

        public EntryBuilder classify(String cs) {
            List<String> sl = Lists.newArrayList(Splitters.csplitter(cs));
            int s = sl.size();
            Preconditions.checkState(s >= 3 && s <= 4);
            String nsi = sl.get(0);
            String gsi = sl.get(1);
            String gs = sl.get(2);
            String fs = s == 3 ? "" : sl.get(3);
            return classify(nsi, gsi, gs, fs);
        }

        public EntryBuilder setAttribute(String ks, String vs) {
            if (ks.equals("rules")) {
                setRules(vs);
            }
            else if (ks.equals("gender")) {
                setGender(vs);
            }
            else if (ks.equals("gstem")) {
                setGstem(vs);
            }
            else if (ks.equals("lspec")) {
                classify(vs);
            }
            else {
                CaseNumber key = CaseNumber.fromString(ks, false);
                if (key != null) {
                    storeForm(key, vs);
                }
                else {
                    throw new IllegalStateException("Bad attribute " + ks + " " + vs);
                }
            }
            return this;
        }

        public boolean isComplete() {
            return (oRules.isPresent() && oGender.isPresent() && oGstem.isPresent());
        }

        public NounEntry build() {
            Preconditions.checkState(isComplete());
            return new NounEntry(pathId, oRules.get(), oGstem.get(), oGender.get(), stored);
        }

    }

    public static EntryBuilder builder(Object pid) {
        PathId pathId = (pid instanceof PathId) ? (PathId) pid : PathId.makeRoot(pid);
        return new EntryBuilder(pathId);
    }

    static Rules classifyRules(String nsi, String gsi, Gender gender, String features) {
        if (gsi.endsWith("ae")) {
            String gst = Mod.butLastString(gsi, 2);
            if (nsi.equals(gst + "a")) {
                return Declension.First.getRules("a");
            }
            else {
                return Declension.First.getRules("mf");
            }
        }
        return null;
    }

    public static NounEntry fromDomElement(Object pid, DomElement domElement) {
        EntryBuilder b = builder(pid);
        for(String ks : domElement.attributeNames()) {
            String vs = domElement.getAttribute(ks);
            b.setAttribute(ks, vs);
        }
        for(DomElement fe : domElement.children("form")) {
            b.storeForm(fe.getAttribute("key"), fe.getTextContent());
        }
        return b.build();
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


    public static class MapRules extends EnumMap<CaseNumber, Rule> implements Rules {

        final PathId pathId;

        public MapRules(PathId pathId) {
            super(CaseNumber.class);
            this.pathId = pathId;
        }

        @Override
        public PathId getPathId() {
            return pathId;
        }

        void addRule(String ks, String vs) {
            CaseNumber key = CaseNumber.fromString(ks);
            put(key, new ModRule(pathId.makeChild(key), vs));
        }
    }

    public static void printRules(Rules rules) {
        System.out.println(rules.getPathId().toString());
        for (Rule r : rules.values()) {
            PathId pathId = r.getPathId();
            CaseNumber key = (CaseNumber) pathId.name;
            System.out.println(key.toString() + ": " + r.toString());
        }
    }

}