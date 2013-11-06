
package latin.setting;

public interface Settings {
    public int getSettingCount();
    public Setting getSetting(int index);
    public Setting getSupportedSetting();
    public boolean addValueRule(Setting.Rule rule);
    public boolean removeValueRule(Setting.Rule rule);
}