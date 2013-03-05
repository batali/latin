
package latin.slots;

public class SimpleSetting implements ISetting {

    private String pathString;
    private String choiceName;
    private boolean value;
    private SettingTraits traits;

    public SimpleSetting(String pathString, String choiceName, boolean value, SettingTraits traits) {
        this.pathString = pathString;
        this.choiceName = choiceName;
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

    public static final PathHandler<SimpleSetting> pathHandler = new PathHandler<SimpleSetting>() {

        @Override
        public SimpleSetting onBoolean(String pathString, boolean sv) {
            return new SimpleSetting(pathString, "T", sv, BooleanSettingTraits.traits);
        }

        @Override
        public SimpleSetting onBinary(String pathString, String choiceName, boolean sv) {
            return new SimpleSetting(pathString, choiceName, sv, BinarySettingTraits.traits);
        }

        @Override
        public SimpleSetting onValue(String pathString, String choiceName, int index, boolean sv) {
            return new SimpleSetting(pathString, choiceName, sv, ValueSettingTraits.traits);
        }

        @Override
        public SimpleSetting onValue(String pathString, String choiceName, boolean sv) {
            return new SimpleSetting(pathString, choiceName, sv, ValueSettingTraits.traits);

        }

    };

    public static final SettingHandler<SimpleSetting> settingHandler = new SettingHandler<SimpleSetting>() {
        @Override
        public SimpleSetting getValueSetting(String pathString, String choiceName, boolean sv) throws SettingSpecException {
            return new SimpleSetting(pathString, choiceName, sv, ValueSettingTraits.traits);
        }

        @Override
        public SimpleSetting getBooleanSetting(String pathString, boolean sv) throws SettingSpecException {
            return new SimpleSetting(pathString, "T", sv, BooleanSettingTraits.traits);
        }
    };


}