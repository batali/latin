
package latin.slots;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.List;

public class SettingList {

    private SettingList () {
    }

    public static List<ISetting> singletonList(ISetting setting) {
        return Lists.newArrayList(setting);
    }

    public static int compareProps(ISetting s1, ISetting s2) {
        return s1.getTraits().compareProps(s1, s2);
    }

    public static final Ordering<ISetting> ISettingOrdering = new Ordering<ISetting> () {
        @Override
        public int compare(ISetting setting1, ISetting setting2) {
            int d = compareProps(setting1, setting2);
            if (d == 0) {
                d = SettingTraits.booleanChoiceIndex(setting2.getValue()) -
                    SettingTraits.booleanChoiceIndex(setting1.getValue());
            }
            return d;
        }
    };

    public static List<ISetting> sortSettingList(List<ISetting> sl) {
        Collections.sort(sl, ISettingOrdering);
        return sl;
    }

    public static List<ISetting> mergeSettingLists(List<ISetting> sl1, List<ISetting> sl2) {
        List<ISetting> nsl = Lists.newArrayList();
        int p1 = 0;
        int n1 = sl1.size();
        int p2 = 0;
        int n2 = sl2.size();
        while (p1 < n1 && p2 < n2) {
            ISetting sp1 = sl1.get(p1);
            ISetting sp2 = sl2.get(p2);
            int d = compareProps(sp1, sp2);
            if (d < 0) {
                nsl.add(sp1);
            }
            else if (d > 0) {
                nsl.add(sp2);
            }
            else {
                if (sp1.getValue() == sp2.getValue()) {
                    nsl.add(sp1);
                }
                else {
                    return null;
                }
            }
            if (d <= 0) {
                p1 += 1;
            }
            if (d >= 0) {
                p2 += 1;
            }
        }
        while (p1 < n1) {
            nsl.add(sl1.get(p1++));
        }
        while (p2 < n2) {
            nsl.add(sl2.get(p2++));
        }
        return nsl;
    }

    public static int findProp(ISetting st, List<? extends ISetting> sl, int lp, int up) {
        while (lp < up) {
            int mp = (lp + up)/2;
            int d = compareProps(st, sl.get(mp));
            if (d < 0) {
                up = mp;
            }
            else if (d > 0) {
                lp = mp + 1;
            }
            else {
                return mp;
            }
        }
        return -1;
    }

    public static <S extends ISetting> S getSetting(ISetting st, List<S> sl) {
        int fp = findProp(st, sl, 0, sl.size());
        return (fp < 0) ? null : sl.get(fp);
    }

    public static boolean isSubset(List<? extends ISetting> slsub, List<? extends ISetting> slsup) {
        int lp = 0;
        int up = 1 + slsup.size() - slsub.size();
        for (ISetting st1 : slsub) {
            int fp = findProp(st1, slsup, lp, up);
            if (fp < 0 || st1.getValue() != slsup.get(fp).getValue()) {
                return false;
            }
            lp = fp + 1;
            up += 1;
        }
        return true;
    }

}