
package latin.slots;

public class SettingPoop {

    private String pstring;

    public SettingPoop(String pstring) {
        this.pstring = pstring;
    }

    public String toString() {
        return "Poop:" + pstring;
    }

    public static final PathHandler<SettingPoop> poopPathHandler = new PathHandler<SettingPoop>() {
        @Override
        public SettingPoop onBoolean(String pathString, boolean sv) throws SettingSpecException {
            return new SettingPoop("boolean:" + pathString + ":" + sv);
        }

        @Override
        public SettingPoop onBinary(String pathString, String choiceName, boolean sv) throws SettingSpecException {
            return new SettingPoop("binary:" + pathString + "." + choiceName + ":" + sv);
        }

        @Override
        public SettingPoop onValue(String pathString, String choiceName, int index, boolean sv) throws SettingSpecException {
            return new SettingPoop("value:" + pathString + "." + choiceName + "." + index + ":" + sv);
        }
    };
}
