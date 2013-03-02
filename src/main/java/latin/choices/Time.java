
package latin.choices;

public enum Time {

    Pas (-1),
    Pre (0),
    Fut (1);

    public final int cmpVal;

    Time (int cmpVal) {
        this.cmpVal = cmpVal;
    }

    public boolean isPast() {
        return cmpVal < 0;
    }

    public boolean isPresent() {
        return cmpVal == 0;
    }

    public boolean isFuture() {
        return cmpVal > 0;
    }

    public static Time fromString(String ks) {
        return EkeyHelper.ekeyFromString(Time.class, ks);
    }

    public static Time fromCmpVal(int cmpval) {
        if (cmpval < 0) {
            return Pas;
        }
        else if (cmpval == 0) {
            return Pre;
        }
        else {
            return Fut;
        }
    }

}