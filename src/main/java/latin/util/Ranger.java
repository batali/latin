
package latin.util;

import java.util.AbstractList;

public class Ranger extends AbstractList<Integer> {

    private int start;
    private int step;
    private int count;

    public Ranger(int start, int step, int count) {
        this.start = start;
        this.step = step;
        this.count = count;
    }

    public int size() {
        return count;
    }

    public Integer get(int i) {
        return start + i * step;
    }

    public static Ranger ranger(int start, int step, int count) {
        return new Ranger(start, step, count);
    }

    public static Ranger ranger(int start, int count) {
        return ranger(start, 1, count);
    }

    public static Ranger ranger(int count) {
        return ranger(0, 1, count);
    }
}




