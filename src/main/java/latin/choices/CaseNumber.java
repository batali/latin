package latin.choices;

import java.util.Map;

public enum CaseNumber implements Case.Key, Number.Key {

    NomSi(Case.Nom, Number.Si),
    AccSi(Case.Acc, Number.Si),
    GenSi(Case.Gen, Number.Si),
    DatSi(Case.Dat, Number.Si),
    AblSi(Case.Abl, Number.Si),
    VocSi(Case.Voc, Number.Si),
    LocSi(Case.Loc, Number.Si),
    NomPl(Case.Nom, Number.Pl),
    AccPl(Case.Acc, Number.Pl),
    GenPl(Case.Gen, Number.Pl),
    DatPl(Case.Dat, Number.Pl),
    AblPl(Case.Abl, Number.Pl),
    VocPl(Case.Voc, Number.Pl),
    LocPl(Case.Loc, Number.Pl);

    public final Case caseKey;
    public final Number numberKey;

    CaseNumber(Case caseKey, Number numberKey) {
        this.caseKey = caseKey;
        this.numberKey = numberKey;
    }

    @Override
    public Case getCase() {
        return caseKey;
    }

    @Override
    public Number getNumber() {
        return numberKey;
    }

    private static final Map<String,CaseNumber> fromStringMap = EkeyHelper.stringToEnumMap(CaseNumber.class);

    public static CaseNumber fromString(String ks) {
        CaseNumber key = fromStringMap.get(ks);
        if (key != null) {
            return key;
        }
        throw new IllegalArgumentException("Unknown CaseNumber key " + ks);
    }

    public static CaseNumber fromCaseNumber(Case caseKey, Number numberKey) {
        return values()[caseKey.ordinal() + numberKey.ordinal() * Case.values().length];
    }

    public interface Key {
        public CaseNumber getCaseNumber();
    }
}



