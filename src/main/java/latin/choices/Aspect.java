
package latin.choices;

public enum Aspect {

    In, Cm;

    public boolean isComplete() {
        return ordinal() == 1;
    }

    public static Aspect fromString(String ks) {
        return EkeyHelper.ekeyFromString(Aspect.class, ks);
    }

    public static Aspect fromBoolean(boolean completep) {
        return completep ? Cm : In;
    }
}