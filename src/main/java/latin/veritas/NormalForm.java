package latin.veritas;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.List;

public class NormalForm {

    public static int compareProps(SettingSpec ss1, SettingSpec ss2) {
        if (ss1 == ss2) {
            return 0;
        }
        int d = ss1.getPathString().compareTo(ss2.getPathString());
        if (d == 0) {
            d = ss1.getValueString().compareTo(ss2.getValueString());
        }
        return d;
    }

    public static final Ordering<SettingSpec> settingSpecOrdering = new Ordering<SettingSpec> () {
        @Override
        public int compare(SettingSpec ss1, SettingSpec ss2) {
            if (ss1 == ss2) {
                return 0;
            }
            int d = compareProps(ss1, ss2);
            if (d == 0) {
                d = Boolean.compare(ss2.getPolarity(), ss1.getPolarity());
            }
            return d;
        }
    };

    public static List<SettingSpec> sortSettingSpecs(List<SettingSpec> settingSpecList) {
        Collections.sort(settingSpecList, settingSpecOrdering);
        return settingSpecList;
    }

    public static List<SettingSpec> mergeSettingLists(List<SettingSpec> sl1, List<SettingSpec> sl2) {
        List<SettingSpec> nsl = Lists.newArrayList();
        int p1 = 0;
        int n1 = sl1.size();
        int p2 = 0;
        int n2 = sl2.size();
        while (p1 < n1 && p2 < n2) {
            SettingSpec sp1 = sl1.get(p1);
            SettingSpec sp2 = sl2.get(p2);
            int d = compareProps(sp1, sp2);
            if (d < 0) {
                nsl.add(sp1);
            }
            else if (d > 0) {
                nsl.add(sp2);
            }
            else {
                if (sp1.getPolarity() == sp2.getPolarity()) {
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

    public static int findProp(SettingSpec st, List<? extends SettingSpec> sl, int lp, int up) {
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

    public static <S extends SettingSpec> S getSetting(SettingSpec st, List<S> sl) {
        int fp = findProp(st, sl, 0, sl.size());
        return (fp < 0) ? null : sl.get(fp);
    }

    public static boolean isSubset(List<? extends SettingSpec> slsub, List<? extends SettingSpec> slsup) {
        int lp = 0;
        int up = 1 + slsup.size() - slsub.size();
        for (SettingSpec st1 : slsub) {
            int fp = findProp(st1, slsup, lp, up);
            if (fp < 0 || st1.getPolarity() != slsup.get(fp).getPolarity()) {
                return false;
            }
            lp = fp + 1;
            up += 1;
        }
        return true;
    }

    public static final Ordering<Iterable<SettingSpec>> settingSpecListOrdering =
        settingSpecOrdering.lexicographical();

    public static List<List<SettingSpec>> sortNormalForm(List<List<SettingSpec>> sll) {
        Collections.sort(sll, settingSpecListOrdering);
        return sll;
    }

    public static boolean adjoinSettingList(List<SettingSpec> nsl, List<List<SettingSpec>> tosll) {
        if (nsl == null) {
            return false;
        }
        List<List<SettingSpec>> rll = null;
        int nsn = nsl.size();
        for (List<SettingSpec> osl : tosll) {
            if (osl.size() <= nsn) {
                if (isSubset(osl, nsl)) {
                    return false;
                }
            }
            else if (isSubset(nsl, osl)) {
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
        return true;
    }

    public static List<List<SettingSpec>> adjoinNormalForm(List<List<SettingSpec>> nsll, List<List<SettingSpec>> tosll) {
        for (List<SettingSpec> nsl : nsll) {
            adjoinSettingList(nsl, tosll);
        }
        return tosll;
    }

    public static List<List<SettingSpec>> combineNormalForms(List<List<SettingSpec>> sll1,
                                                             List<List<SettingSpec>> sll2) {
        List<List<SettingSpec>> tosll = Lists.newArrayList();
        adjoinNormalForm(sll1, tosll);
        adjoinNormalForm(sll2, tosll);
        return sortNormalForm(tosll);
    }

    public static List<List<SettingSpec>> mergeNormalForms(List<List<SettingSpec>> sll1,
                                                           List<List<SettingSpec>> sll2) {
        List<List<SettingSpec>> tosll = Lists.newArrayList();
        for (List<SettingSpec> sl1 : sll1) {
            for (List<SettingSpec> sl2 : sll2) {
                adjoinSettingList(mergeSettingLists(sl1, sl2), tosll);
            }
        }
        return sortNormalForm(tosll);
    }

    public static List<List<SettingSpec>> disjoinSequence(List<PropExpression> subExpressions,
                                                          int sp, int ep,
                                                          boolean bv) {
        if (sp + 1 == ep) {
            return subExpressions.get(sp).getCnf(bv);
        }
        else {
            int mp = (sp + ep)/2;
            return mergeNormalForms(disjoinSequence(subExpressions, sp, mp, bv),
                                    disjoinSequence(subExpressions, mp, ep, bv));
        }
    }

    public static List<List<SettingSpec>> conjoinSequence(List<PropExpression> subExpressions, boolean bv) {
        List<List<SettingSpec>> to_sll = Lists.newArrayList();
        for (PropExpression subExpression : subExpressions) {
            adjoinNormalForm(subExpression.getCnf(bv), to_sll);
        }
        return sortNormalForm(to_sll);
    }

}





