
package latin.veritas;

public interface SettingSpec {

    public String getPathString();
    public String getValueString();
    public boolean getPolarity();
    public SettingSpec getOpposite();

    public interface Handler<T extends SettingSpec> {
        T getValueSetting(String pathString, String valueString, boolean polarity);
        T getBooleanSetting(String pathString, boolean polarity);
    }
}

