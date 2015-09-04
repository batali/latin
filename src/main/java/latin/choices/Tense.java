package latin.choices;

public enum Tense {

    Imp(false, -1),

    Pre(false, 0),

    Fut(false, 1),

    Plu(true, -1),

    Per(true, 0),

    Fup(true, 1);

    public final Aspect aspect;
    public final Time time;

    Tense(boolean c, int t) {
        this.aspect = Aspect.fromBoolean(c);
        this.time = Time.fromCmpVal(t);
    }

    public static Tense fromString(String ks) {
        return EkeyHelper.ekeyFromString(Tense.class, ks);
    }

    public static Tense fromEnums(Aspect c, Time t) {
        return values()[c.ordinal()*3 + t.ordinal()];
    }

    public static Tense fromVals(boolean cp, int tcmp) {
        return fromEnums(Aspect.fromBoolean(cp), Time.fromCmpVal(tcmp));
    }

    public Tense forAspect(Aspect a) {
        return fromEnums(a, time);
    }

    public Tense forTime(Time t) {
        return fromEnums(aspect, t);
    }

}
