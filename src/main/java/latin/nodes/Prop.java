
package latin.nodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

public abstract class Prop {

    public final Setting trueSetting;
    public final Setting falseSetting;

    public Setting getBooleanSetting(boolean bv) {
        return bv ? trueSetting : falseSetting;
    }

    public static int getBooleanIndex(boolean bv) {
        return bv ? 1 : 0;
    }

    public static boolean getIndexBoolean(int i) {
        Preconditions.checkElementIndex(i, 2);
        return i != 0;
    }

    public Setting getIndexSetting(int i) {
        return getBooleanSetting(getIndexBoolean(i));
    }

    public int setterCount() {
        return 2;
    }

    public BSRule getRule() {
        return null;
    }

    public abstract String getSettingString(boolean sv);

    public boolean whenSet(Setting setting) {
        Preconditions.checkState(setting.haveSupporter());
        return true;
    }

    public boolean whenUnset(Setting setting) {
        Preconditions.checkState(!setting.haveSupporter());
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
            BSRule rule = getRule();
            if (rule != null) {
                rule.recordSet(trueSetting, value, deduceQueue);
            }
            super.announceSet(deduceQueue);
        }

        @Override
        public void announceUnset(RetractQueue retractQueue, Object stopAt) {
            BSRule rule = getRule();
            if (rule != null) {
                rule.recordUnset(trueSetting, value, retractQueue);
                if (Objects.equal(rule, stopAt)) {
                    return;
                }
            }
            super.announceUnset(retractQueue, stopAt);
        }

    }

    public Prop() {
        this.trueSetting = new Setting(true);
        this.falseSetting = new Setting(false);
    }

    public @Nullable BooleanSetting getSupportedSetting() {
        return trueSetting.getSupportedSetting();
    }

}