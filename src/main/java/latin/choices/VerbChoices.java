package latin.choices;

public enum VerbChoices {

    ImpAct (false, -1, true),
    ImpPas (false, -1, false),
    PreAct (false, 0, true),
    PrePas (false, 0, false),
    FutAct (false, 1, true),
    FutPas (false, 1, false),
    PluAct (true, -1, true),
    PluPas (true, -1, false),
    PerAct (true, 0, true),
    PerPas (true, 0, false),
    FupAct (true, 1, true),
    FupPas (true, 1, false);

    public final Aspect aspect;
    public final Time time;
    public final Voice voice;

    VerbChoices(boolean c, int t, boolean ap) {
        aspect = Aspect.fromBoolean(c);
        time = Time.fromCmpVal(t);
        voice = Voice.fromBoolean(ap);
    }

    public static VerbChoices fromString(String ks) {
        return EkeyHelper.ekeyFromString(VerbChoices.class, ks);
    }

    public static VerbChoices fromEnums(Aspect c, Time t, Voice v) {
        return values()[c.ordinal()*6 + t.ordinal()*2 + v.ordinal()];
    }

    public static VerbChoices fromVals(boolean cp, int tcmp, boolean ap) {
        return fromEnums(Aspect.fromBoolean(cp), Time.fromCmpVal(tcmp), Voice.fromBoolean(ap));
    }

    public boolean isComplete() {
        return aspect.isComplete();
    }

    public boolean isPresent() {
        return time.isPresent();
    }

    public boolean isPast() {
        return time.isPast();
    }

    public boolean isFuture() {
        return time.isFuture();
    }

    public boolean isActive() {
        return voice.isActive();
    }

    public VerbChoices setAspect(Aspect c) {
        return fromEnums(c, time, voice);
    }

    public VerbChoices setTime(Time t) {
        return fromEnums(aspect, t, voice);
    }

    public VerbChoices setVoice(Voice v) {
        return fromEnums(aspect, time, v);
    }

    public VerbChoices setComplete (boolean c) {
        return setAspect(Aspect.fromBoolean(c));
    }

    public VerbChoices setActive (boolean a) {
        return setVoice(Voice.fromBoolean(a));
    }
}