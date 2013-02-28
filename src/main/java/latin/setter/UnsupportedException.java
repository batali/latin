
package latin.setter;

import latin.slots.ISetting;

public class UnsupportedException extends Exception {

    public final ISetting setting;

    public UnsupportedException (ISetting setting) {
        this.setting = setting;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}