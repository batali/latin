
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

public abstract class Prop {

    public final Setting trueSetting;
    public final Setting falseSetting;
    private @Nullable Setting supportedSetting;

    public Setting getBooleanSetting(boolean bv) {
        return bv ? trueSetting : falseSetting;
    }

    public int setterCount() {
        return 2;
    }

    public abstract String getSettingString(boolean sv);

    public Setting getIndexSetting(int p) {
        Preconditions.checkElementIndex(p, 2);
        return getBooleanSetting(p != 0);
    }

    public boolean whenSet(Setting setting) {
        Preconditions.checkState(supportedSetting == null);
        Preconditions.checkState(setting.haveSupporter());
        supportedSetting = setting;
        return true;
    }

    public boolean whenUnset(Setting setting) {
        Preconditions.checkState(Objects.equal(supportedSetting, setting));
        Preconditions.checkState(!setting.haveSupporter());
        supportedSetting = null;
        return true;
    }

    public void whenSetAnnounced(Setting setting, DeduceQueue deduceQueue) throws ContradictionException {
        Preconditions.checkState(Objects.equal(setting, supportedSetting));
    }

    public boolean whenUnsetAnnounced(Setting setting, RetractQueue retractQueue, Object stopAt) {
        Preconditions.checkState(supportedSetting == null);
        return true;
    }

    public class Setting extends BooleanSetting {

        public final boolean value;

        public Setting(boolean value) {
            super();
            this.value = value;
        }

        public Setting getOpposite() {
            return getBooleanSetting(!booleanValue());
        }

        public boolean booleanValue() {
            return value;
        }

        public String toString() {
            return getSettingString(value);
        }

        @Override
        public boolean setSupport(Supporter newSupporter) {
            return super.setSupport(newSupporter) && whenSet(this);
        }

        @Override
        public boolean removeSupport() {
            return super.removeSupport() && whenUnset(this);
        }

        @Override
        public void announceSet(DeduceQueue deduceQueue) throws ContradictionException {
            whenSetAnnounced(this, deduceQueue);
            super.announceSet(deduceQueue);
        }

        @Override
        public void announceUnset(RetractQueue retractQueue, Object stopAt) {
            if (whenUnsetAnnounced(this, retractQueue, stopAt)) {
                super.announceUnset(retractQueue, stopAt);
            }
        }

    }

    public Prop() {
        this.trueSetting = new Setting(true);
        this.falseSetting = new Setting(false);
        this.supportedSetting = null;
    }

    public @Nullable Setting getSupportedSetting() {
        return supportedSetting;
    }

}