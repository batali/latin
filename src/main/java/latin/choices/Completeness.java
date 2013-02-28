
package latin.choices;

public enum Completeness {

    In,

    Cm {
        @Override
        public boolean isComplete() {
            return true;
        }
    };

    public boolean isComplete() {
        return false;
    }

    public static Completeness fromString(String ks) {
        return EkeyHelper.ekeyFromString(Completeness.class, ks);
    }

    public static Completeness fromBoolean(boolean completep) {
        return completep ? Cm : In;
    }
}