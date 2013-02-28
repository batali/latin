

package latin.choices;

public enum Mood{

    Ind {
        @Override
        public boolean isIndicative() {
            return true;
        }
    },

    Sub {
        @Override
        public boolean isSubjunctive() {
            return true;
        }
    },

    Imp {
        @Override
        public boolean isImperative() {
            return true;
        }
    };

    public boolean isIndicative() {
        return false;
    }

    public boolean isSubjunctive() {
        return false;
    }

    public boolean isImperative() {
        return false;
    }

    public static Mood fromString(String ks) {
        return EkeyHelper.ekeyFromString(Mood.class, ks);
    }

}