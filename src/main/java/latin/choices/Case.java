
package latin.choices;

public enum Case {

    Nom {
        @Override
        public boolean isNominative() {
            return true;
        }
    },
    Acc {
        @Override
        public boolean isAccusative() {
            return true;
        }
    },
    Gen {
        @Override
        public boolean isGenitive() {
            return true;
        }
    },
    Dat {
        @Override
        public boolean isDative() {
            return true;
        }
    },
    Abl {
        @Override
        public boolean isAblative() {
            return true;
        }
    },
    Voc {
        @Override
        public boolean isVocative() {
            return true;
        }
    },
    Loc {
        @Override
        public boolean isLocative() {
            return true;
        }
    };

    public boolean isNominative() {
        return false;
    }

    public boolean isAccusative() {
        return false;
    }

    public boolean isGenitive() {
        return false;
    }

    public boolean isDative() {
        return false;
    }

    public boolean isAblative() {
        return false;
    }

    public boolean isVocative() {
        return false;
    }

    public boolean isLocative() {
        return false;
    }

    public static Case fromString(String ks) {
        return EkeyHelper.ekeyFromString(Case.class, ks);
    }

    public static Case getKey(Object o) {
        if (o == null) {
            return null;
        }
        else if (o instanceof Case) {
            return (Case)o;
        }
        else if (o instanceof Case.Key) {
            return ((Case.Key)o).getCase();
        }
        else {
            return fromString(o.toString());
        }
    }

    public static interface Key {
        Case getCase();
    }

}
