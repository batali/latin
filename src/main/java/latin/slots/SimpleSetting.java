
package latin.slots;

public class SimpleSetting implements ISetting {

    private String pathString;
    private String choiceName;
    private int index;
    private boolean value;
    private SettingTraits traits;

    public SimpleSetting(String pathString, String choiceName, int index, boolean value, SettingTraits traits) {
        this.pathString = pathString;
        this.choiceName = choiceName;
        this.index = index;
        this.value = value;
        this.traits = traits;
    }

    @Override
    public String getPathString() {
        return pathString;
    }

    @Override
    public String getChoiceName() {
        return choiceName;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public SettingTraits getTraits () {
        return traits;
    }

    @Override
    public String toString() {
        return getTraits().settingString(this);
    }

    public static PathHandler<SimpleSetting> pathHandler = new PathHandler<SimpleSetting>() {

        @Override
        public SimpleSetting onBoolean(String pathString, boolean sv) {
            return new SimpleSetting(pathString, "", 0, sv, BooleanSettingTraits.traits);
        }

        @Override
        public SimpleSetting onBinary(String pathString, String choiceName, boolean sv) {
            return new SimpleSetting(pathString, choiceName, 0, sv, BinarySettingTraits.traits);
        }

        @Override
        public SimpleSetting onValue(String pathString, String choiceName, int index, boolean sv) {
            return new SimpleSetting(pathString, choiceName, index, sv, ValueSettingTraits.traits);
        }
    };

}