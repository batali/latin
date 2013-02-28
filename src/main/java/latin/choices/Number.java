package latin.choices;

public enum Number {

    Si {
        @Override
        public boolean isSingular() {
            return true;
        }
    },

    Pl {
        @Override
        public boolean isPlural() {
            return true;
        }
    };

    public boolean isSingular() {
        return false;
    }

    public boolean isPlural() {
        return false;
    }

    public static Number fromString(String ks) {
        return EkeyHelper.ekeyFromString(Number.class, ks);
    }

    public interface Key {
        public Number getNumber();
    }

}
