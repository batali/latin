
package latin.util;

public class Stepper {

    public final int start;
    public final int step;

    public Stepper(int start, int step) {
        this.start = start;
        this.step = step;
    }

    public Stepper() {
        this(0, 1);
    }

    public int get(int p) {
        return start + p * step;
    }

}