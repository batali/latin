
package latin.choices;

public enum Time {

    Pas {
        @Override
        public boolean isPast() {
            return true;
        }
    },

    Pre {
        @Override
        public boolean isPresent() {
            return true;
        }
    },

    Fut {
        @Override
        public boolean isFuture() {
            return true;
        }
    };

    public boolean isPast() {
        return false;
    }

    public boolean isPresent() {
        return false;
    }

    public boolean isFuture() {
        return false;
    }

    public static Time fromString(String ks) {
        return EkeyHelper.ekeyFromString(Time.class, ks);
    }

}