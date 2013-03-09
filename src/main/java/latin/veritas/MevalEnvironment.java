
package latin.veritas;

import java.util.HashMap;

public class MevalEnvironment extends HashMap<String,String> {

    public boolean evalSlot(String slot, String value) {
        return get(slot).equals(value);
    }

}