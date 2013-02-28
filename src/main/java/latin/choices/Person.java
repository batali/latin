
package latin.choices;

public enum Person {

    Fp {
        @Override
        public boolean isFirst() {
            return true;
        }
    },

    Sp {
        @Override
        public boolean isSecond() {
            return true;
        }
    },

    Tp {
        @Override
        public boolean isThird() {
            return true;
        }
    };

    public boolean isFirst() {
        return false;
    }

    public boolean isSecond() {
        return false;
    }

    public boolean isThird() {
        return false;
    }

    public static Person fromString(String ks) {
        return EkeyHelper.ekeyFromString(Person.class, ks);
    }

}