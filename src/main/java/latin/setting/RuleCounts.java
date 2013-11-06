
package latin.setting;

import com.google.common.base.Preconditions;

import java.util.List;

public class RuleCounts {
    int settingCount;
    int trueCount;
    int falseCount;
    public RuleCounts(int settingCount, int trueCount, int falseCount) {
        this.settingCount = settingCount;
        this.trueCount = trueCount;
        this.falseCount = falseCount;
    }
    public RuleCounts(int settingCount) {
        this(settingCount, 0, 0);
    }
    public RuleCounts add(int delta, boolean bv) {
        if (bv) {
            trueCount += delta;
        }
        else {
            falseCount += delta;
        }
        return this;
    }
    public RuleCounts inc(boolean bv) {
        return add(1, bv);
    }
    public RuleCounts dec(boolean bv) {
        return add(-1, bv);
    }
    public int getUnknownCount() {
        return settingCount - (trueCount + falseCount);
    }
    public boolean disjunctionDeduceCheck() {
        return trueCount == 0 && falseCount + 1 == settingCount;
    }
    public boolean disjunctionRetractCheck() {
        return falseCount + 1 < settingCount;
    }
    public RuleCounts setCounts(List<? extends Setting> settings) {
        Preconditions.checkArgument(settingCount == settings.size());
        int tc = 0;
        int fc = 0;
        for (Setting s : settings) {
            if (s.isSupported()) {
                tc += 1;
            }
            else if (s.getOpposite().isSupported()) {
                fc += 1;
            }
        }
        trueCount = tc;
        falseCount = fc;
        return this;
    }
    public static RuleCounts getCounts(List<? extends Setting> settings) {
        RuleCounts ruleCounts = new RuleCounts(settings.size());
        return ruleCounts.setCounts(settings);
    }
}