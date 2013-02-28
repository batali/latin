
package latin.slots;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.List;

public class NormalForm {

    private NormalForm() {
    }

    public static final Ordering<Iterable<ISetting>> ISettingListOrdering =
            SettingList.ISettingOrdering.lexicographical();

    public static List<List<ISetting>> sortNormalForm(List<List<ISetting>> sll) {
        Collections.sort(sll, ISettingListOrdering);
        return sll;
    }

    public static void adjoinSettingList(List<ISetting> nsl, List<List<ISetting>> tosll) {
        if (nsl == null) {
            return;
        }
        List<List<ISetting>> rll = null;
        int nsn = nsl.size();
        for (List<ISetting> osl : tosll) {
            if (osl.size() <= nsn) {
                if (SettingList.isSubset(osl, nsl)) {
                    return;
                }
            }
            else if (SettingList.isSubset(nsl, osl)) {
                if (rll == null) {
                    rll = Lists.newArrayList();
                }
                rll.add(osl);
            }
        }
        if (rll != null) {
            tosll.removeAll(rll);
        }
        tosll.add(nsl);
    }

    public static List<List<ISetting>> adjoinNormalForm(List<List<ISetting>> nsll, List<List<ISetting>> tosll) {
        for (List<ISetting> nsl : nsll) {
            adjoinSettingList(nsl, tosll);
        }
        return tosll;
    }

    public static List<List<ISetting>> combineNormalForms(List<List<ISetting>> sll1, List<List<ISetting>> sll2) {
        List<List<ISetting>> tosll = Lists.newArrayList();
        adjoinNormalForm(sll1, tosll);
        adjoinNormalForm(sll2, tosll);
        return sortNormalForm(tosll);
    }

    public static List<List<ISetting>> mergeNormalForms(List<List<ISetting>> sll1,
                                                        List<List<ISetting>> sll2) {
        List<List<ISetting>> tosll = Lists.newArrayList();
        for (List<ISetting> sl1 : sll1) {
            for (List<ISetting> sl2 : sll2) {
                adjoinSettingList(SettingList.mergeSettingLists(sl1, sl2), tosll);
            }
        }
        return sortNormalForm(tosll);
    }

    public static List<List<ISetting>> singletonCnf(ISetting setting) {
        List<List<ISetting>> sll = Lists.newArrayList();
        sll.add(SettingList.singletonList(setting));
        return sll;
    }

    public static SettingHandler<ISetting> getSettingHandler (GetSettingSpecHandler getSettingSpecHandler,
                                                              PathHandler<? extends ISetting> pathHandler) {
        return getSettingSpecHandler.getSettingHandler(ISetting.class, pathHandler);
    }
}