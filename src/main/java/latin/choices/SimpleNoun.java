package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import latin.forms.Form;
import latin.forms.StringForm;
import latin.util.DomElement;
import latin.util.PathId;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class SimpleNoun {

    static CaseNumber NOMSI = CaseNumber.NomSi;

    PathId pathId;
    StringForm gstem;
    Declension.Rules rules;
    Gender gender;
    EnumMap<CaseNumber,StringForm> storedFormMap;
    English.CountNounEntry englishEntry;
    Map<String,String> featureMap;

    class LatinEntry implements LatinNoun.Entry {

        @Override
        public Form getGstem() {
            return gstem;
        }

        @Override
        public Declension.Rules getRules() {
            return rules;
        }

        @Override
        public Form getStoredForm(CaseNumber cn) {
            return (storedFormMap != null) ? storedFormMap.get(cn) : null;
        }

        @Override
        public Gender getGender() {
            return gender;
        }
    };

    LatinEntry latinEntry;

    public SimpleNoun(PathId pathId) {
        this.pathId = pathId;
        this.latinEntry = new LatinEntry();
    }

    void setStoredForm(CaseNumber cn, String vs) {
        if (storedFormMap == null) {
            storedFormMap = new EnumMap<CaseNumber, StringForm>(CaseNumber.class);
        }
        storedFormMap.put(cn, new StringForm(pathId.makeChild(cn), vs));
    }

    void setFeature(String ks, String vs) {
        if (featureMap == null) {
            featureMap = Maps.newHashMap();
        }
        featureMap.put(ks, vs);
    }

    public void set(String ks, String vs) {
        if (ks.equals("gstem")) {
            gstem = new StringForm(pathId.makeChild(gstem), vs);
        }
        else if (ks.equals("rules")) {
            rules = Declension.getRules(vs);
        }
        else if (ks.equals("gender")) {
            gender = Gender.fromString(vs);
        }
        else if (ks.equals("english")) {
            englishEntry = English.parseCountNounEntry(vs);
        }
        else {
            CaseNumber cn = CaseNumber.fromString(ks, false);
            if (cn != null) {
                setStoredForm(cn, vs);
            }
            else {
                setFeature(ks, vs);
            }
        }
    }

    boolean checkNomSi () {
        return rules.get(NOMSI) != null || (storedFormMap != null && storedFormMap.get(NOMSI) != null);
    }

    public boolean check() {
        return gstem != null && gender != null && rules != null && englishEntry != null &&
               checkNomSi();
    }

    public void loadDomElement(DomElement domElement) {
        for (String attributeName : domElement.attributeNames()) {
            set(attributeName, domElement.getAttribute(attributeName));
        }
    }

    public static final File WordsDirectory = new File("words");

    public static SimpleNoun fromXML(File file) throws Exception {
        DomElement domElement = DomElement.fromFile(file);
        SimpleNoun simpleNoun = new SimpleNoun(PathId.makeRoot(file));
        simpleNoun.loadDomElement(domElement);
        Preconditions.checkState(simpleNoun.check(), "check failed " + file.toString());
        return simpleNoun;
    }


}