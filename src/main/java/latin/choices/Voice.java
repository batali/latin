
package latin.choices;

import org.apache.commons.lang3.tuple.Pair;

public enum Voice {

    Pas {
        @Override
        public boolean isPassive() {
            return true;
        }
    },
    Act {
        @Override
        public boolean isActive() {
            return true;
        }
    };

    public boolean isPassive() {
        return false;
    }

    public boolean isActive() {
        return false;
    }

    public <T> T select(T at, T pt) {
        return isActive() ? at : pt;
    }

    public <T> T select(Pair<T,T> tpair) {
        return select(tpair.getLeft(), tpair.getRight());
    }

    public static Voice fromString(String ks) {
        return EkeyHelper.ekeyFromString(Voice.class, ks);
    }

    public static Voice fromBoolean(boolean activep) {
        return activep ? Act : Pas;
    }
}