package latin.choices;

import com.google.common.base.Function;

import java.util.Map;

import javax.annotation.Nullable;

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

    public CaseNumber toNumber(Number toNumberKey) {
        return fromCaseNumber(caseKey, toNumberKey);
    }

    public CaseNumber toCase(Case toCaseKey) {
        return fromCaseNumber(toCaseKey, numberKey);
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

    public static CaseNumber getKey(@Nullable Object x) {
        if (x == null) {
            return null;
        }
        else if (x instanceof CaseNumber) {
            return (CaseNumber) x;
        }
        else if (x instanceof CaseNumber.Key) {
            return ((CaseNumber.Key)x).getCaseNumber();
        }
        else {
            return fromString(x.toString());
        }
    }

    public interface Key {
        public CaseNumber getCaseNumber();
    }

    public static final Function<Object,CaseNumber> toKey = new Function<Object, CaseNumber>() {
        @Override
        public CaseNumber apply(@Nullable Object o) {
            return getKey(o);
        }
    };

}



