
package latin.choices;

public enum PersonNumber {

    FpSi(Person.Fp, Number.Si),
    SpSi(Person.Sp, Number.Si),
    TpSi(Person.Tp, Number.Si),
    FpPl(Person.Fp, Number.Pl),
    SpPl(Person.Sp, Number.Pl),
    TpPl(Person.Tp, Number.Pl);

    public final Person personKey;
    public final Number numberKey;

    PersonNumber(Person personKey, Number numberKey) {
        this.personKey = personKey;
        this.numberKey = numberKey;
    }

    public static PersonNumber fromString(String ks) {
        return EkeyHelper.ekeyFromString(PersonNumber.class, ks);
    }

    public static PersonNumber fromPersonNumber(Person personKey, Number numberKey) {
        return values()[personKey.ordinal() + numberKey.ordinal() * Person.values().length];
    }

}


