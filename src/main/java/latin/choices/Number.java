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

    public boolean isPlural() { return false; }

    public static Number fromString(String ks) {
        return EkeyHelper.ekeyFromString(Number.class, ks);
    }

    public static Number getKey(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Number) {
            return (Number) o;
        }
        else if (o instanceof Number.Key) {
            return ((Number.Key)o).getNumber();
        }
        else {
            return fromString(o.toString());
        }
    }

    public interface Key {
        public Number getNumber();
    }

}
