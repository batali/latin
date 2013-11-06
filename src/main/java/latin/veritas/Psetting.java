
package latin.veritas;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class Psetting {

    public final String pathString;
    public final String choiceString;
    public final boolean value;

    public abstract <T> T getSetting(GetSetting<T> handler);

    public Psetting(String pathString, String choiceString, boolean value) {
        this.pathString = pathString;
        this.choiceString = choiceString;
        this.value = value;
    }

    public boolean meval(MevalEnvironment menv) {
        return menv.evalSlot(pathString, choiceString) == value;
    }

    public void collectPaths(Set<String> pathStrings) {
        pathStrings.add(pathString);
    }

    public static class BooleanPsetting extends Psetting {
        public BooleanPsetting(String pathString, boolean value) {
            super(pathString, "T", value);
        }
        public String toString() {
            return value ? pathString : "!" + pathString;
        }
        public <T> T getSetting(GetSetting<T> handler) {
            return handler.getBooleanSetting(pathString, value);
        }
    }

    public static class ValuePsetting extends Psetting {
        public ValuePsetting(String ps, String cn, boolean sv) {
            super(ps, cn, sv);
        }
        public String toString() {
            String op = value ? "=" : "!=";
            return pathString + op + choiceString;
        }
        public <T> T getSetting(GetSetting<T> handler) {
            return handler.getValueSetting(pathString, choiceString, value);
        }
    }

    public static int compareProps(Psetting ps1, Psetting ps2) {
        if (ps1 == ps2) {
            return 0;
        }
        int d = ps1.pathString.compareTo(ps2.pathString);
        if (d == 0) {
            d = ps1.choiceString.compareTo(ps2.choiceString);
        }
        return d;
    }

    public int valueIndex() {
        return value ? 0 : 1;
    }

    public static List<Psetting> singletonList(Psetting setting) {
        return Lists.newArrayList(setting);
    }

    public static final List<Psetting> emptySettingList = Collections.emptyList();

    public static final List<List<Psetting>> emptySettingsList = Collections.emptyList();


    public static final Ordering<Psetting> PropOrdering = new Ordering<Psetting>() {
        @Override
        public int compare(Psetting ps1, Psetting ps2) {
            return compareProps(ps1, ps2);
        }
    };

    public static final Ordering<Psetting> PsettingOrdering = new Ordering<Psetting> () {
        @Override
        public int compare(Psetting ps1, Psetting ps2) {
            if (ps1 == ps2) {
                return 0;
            }
            int d = compareProps(ps1, ps2);
            if (d == 0) {
                d = ps1.valueIndex() - ps2.valueIndex();
            }
            return d;
        }
    };

    public static List<Psetting> sortSettingList(List<Psetting> sl) {
        Collections.sort(sl, PsettingOrdering);
        return sl;
    }

    public static List<Psetting> mergeSettingLists(List<Psetting> sl1, List<Psetting> sl2) {
        List<Psetting> nsl = Lists.newArrayList();
        int p1 = 0;
        int n1 = sl1.size();
        int p2 = 0;
        int n2 = sl2.size();
        while (p1 < n1 && p2 < n2) {
            Psetting sp1 = sl1.get(p1);
            Psetting sp2 = sl2.get(p2);
            int d = compareProps(sp1, sp2);
            if (d < 0) {
                nsl.add(sp1);
            }
            else if (d > 0) {
                nsl.add(sp2);
            }
            else {
                if (sp1.value == sp2.value) {
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

    public static int findProp(Psetting st, List<? extends Psetting> sl, int lp, int up) {
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

    public static <S extends Psetting> S getSetting(Psetting st, List<S> sl) {
        int fp = findProp(st, sl, 0, sl.size());
        return (fp < 0) ? null : sl.get(fp);
    }

    public static boolean isSubset(List<? extends Psetting> slsub, List<? extends Psetting> slsup) {
        int lp = 0;
        int up = 1 + slsup.size() - slsub.size();
        for (Psetting st1 : slsub) {
            int fp = findProp(st1, slsup, lp, up);
            if (fp < 0 || st1.value != slsup.get(fp).value) {
                return false;
            }
            lp = fp + 1;
            up += 1;
        }
        return true;
    }

    public static final Ordering<Iterable<Psetting>> PsettingListOrdering =
            PsettingOrdering.lexicographical();

    public static List<List<Psetting>> sortNormalForm(List<List<Psetting>> sll) {
        Collections.sort(sll, PsettingListOrdering);
        return sll;
    }

    public static boolean adjoinSettingList(List<Psetting> nsl, List<List<Psetting>> tosll) {
        if (nsl == null) {
            return false;
        }
        List<List<Psetting>> rll = null;
        int nsn = nsl.size();
        for (List<Psetting> osl : tosll) {
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

    public static List<List<Psetting>> adjoinNormalForm(List<List<Psetting>> nsll, List<List<Psetting>> tosll) {
        for (List<Psetting> nsl : nsll) {
            adjoinSettingList(nsl, tosll);
        }
        return tosll;
    }

    public static List<List<Psetting>> combineNormalForms(List<List<Psetting>> sll1, List<List<Psetting>> sll2) {
        List<List<Psetting>> tosll = Lists.newArrayList();
        adjoinNormalForm(sll1, tosll);
        adjoinNormalForm(sll2, tosll);
        return sortNormalForm(tosll);
    }

    public static List<List<Psetting>> mergeNormalForms(List<List<Psetting>> sll1,
                                                        List<List<Psetting>> sll2) {
        List<List<Psetting>> tosll = Lists.newArrayList();
        for (List<Psetting> sl1 : sll1) {
            for (List<Psetting> sl2 : sll2) {
                adjoinSettingList(mergeSettingLists(sl1, sl2), tosll);
            }
        }
        return sortNormalForm(tosll);
    }

    public static List<List<Psetting>> singletonCnf(Psetting setting) {
        List<List<Psetting>> sll = Lists.newArrayList();
        sll.add(singletonList(setting));
        return sll;
    }

    public interface GetSetting<T> {
        public T getBooleanSetting(String pathString, boolean sv);
        public T getValueSetting(String pathString, String choiceName, boolean sv);
    }

    public interface Handler extends GetSetting<Psetting> {
    }

    public static final Handler simpleHandler = new Handler() {
        @Override
        public Psetting getBooleanSetting(String pathString, boolean sv) {
            return new BooleanPsetting(pathString, sv);
        }
        @Override
        public Psetting getValueSetting(String pathString, String choiceName, boolean sv) {
            return new ValuePsetting(pathString, choiceName, sv);
        }
    };

    public static List<List<Psetting>> disjoinSequence(List<PropExpression> subExpressions, int sp, int ep,
                                                       boolean bv, Handler handler) {
        if (sp + 1 == ep) {
            return subExpressions.get(sp).getCnf(bv, handler);
        }
        else {
            int mp = (sp + ep)/2;
            return mergeNormalForms(disjoinSequence(subExpressions, sp, mp, bv, handler),
                                    disjoinSequence(subExpressions, mp, ep, bv, handler));
        }
    }

    public static List<List<Psetting>> conjoinSequence(List<PropExpression> subExpressions, boolean bv, Handler handler) {
        List<List<Psetting>> to_sll = Lists.newArrayList();
        for (PropExpression subExpression : subExpressions) {
            adjoinNormalForm(subExpression.getCnf(bv, handler), to_sll);
        }
        return sortNormalForm(to_sll);
    }

    public static <T> List<T> transformPsettings(List<Psetting> psettings, final GetSetting<T> handler) {
        return Lists.transform(psettings, new Function<Psetting, T>() {
            @Override
            public T apply(Psetting psetting) {
                return psetting.getSetting(handler);
            }
        });
    }

    public static <T> List<List<T>> transformCnf(List<List<Psetting>> psettingCnf, final GetSetting<T> handler) {
        return Lists.transform(psettingCnf, new Function<List<Psetting>, List<T>>() {
            @Override
            public List<T> apply(List<Psetting> psettings) {
                return transformPsettings(psettings, handler);
            }
        });
    }

    public static boolean mevalSequence(List<Psetting> psettings, MevalEnvironment menv, boolean amConjunction) {
        for (Psetting psetting : psettings) {
            if (psetting.meval(menv) != amConjunction) {
                return !amConjunction;
            }
        }
        return amConjunction;
    }

    public static boolean mevalDisjunction(List<Psetting> psettings, MevalEnvironment menv) {
        return mevalSequence(psettings, menv, false);
    }

    public static boolean mevalConjunction(List<Psetting> psettings, MevalEnvironment menv) {
        return mevalSequence(psettings, menv, true);
    }

    public static boolean mevalNormalForm(List<List<Psetting>> psettingsList, MevalEnvironment menv, boolean amCnf) {
        for (List<Psetting> psettings : psettingsList) {
            if (mevalSequence(psettings, menv, !amCnf) != amCnf) {
                return !amCnf;
            }
        }
        return amCnf;
    }

    public static boolean mevalCnf(List<List<Psetting>> psettingsList, MevalEnvironment menv) {
        return mevalNormalForm(psettingsList, menv, true);
    }

    public static boolean mevalDnf(List<List<Psetting>> psettingsList, MevalEnvironment menv) {
        return mevalNormalForm(psettingsList, menv, false);
    }

    public static void collectPsettingPaths(List<Psetting> psettingList, Set<String> pathStrings) {
        for (Psetting psetting : psettingList) {
            psetting.collectPaths(pathStrings);
        }
    }

    public static void collectPsettingsPaths(List<List<Psetting>> psettingsList, Set<String> pathStrings) {
        for (List<Psetting> psettings : psettingsList) {
            collectPsettingPaths(psettings, pathStrings);
        }
    }

}
