
package latin.choices;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public enum Gender {

    m {
        @Override
        public boolean isMasculine() {
            return true;
        }
    },
    f {
        @Override
        public boolean isFeminine() {
            return true;
        }
        @Override
        public int selectIndex(int s) {
            return (s < 3) ? 0 : 1;
        }
    },
    n {
        @Override
        public boolean isNeuter() {
            return true;
        }

        @Override
        public int selectIndex(int s) {
            return s-1;
        }
    },
    mf {
        @Override
        public boolean notNeuter() {
            return true;
        }
        @Override
        public int selectIndex(int s) {
            Preconditions.checkArgument(s < 3);
            return 0;
        }
    },
    c {
        @Override
        public boolean notNeuter() {
            return true;
        }
        @Override
        public int selectIndex(int s) {
            Preconditions.checkArgument(s < 3);
            return 0;
        }
    },
    mfn {
        @Override
        public boolean isMasculine() {
            return true;
        }
        @Override
        public boolean isFeminine() {
            return true;
        }
        @Override
        public boolean isNeuter() {
            return true;
        }
    };

    public boolean isMasculine() {
        return false;
    }

    public boolean isFeminine() {
        return false;
    }

    public boolean notNeuter() {
        return isMasculine() || isFeminine();
    }

    public boolean isNeuter() {
        return false;
    }

    public int selectIndex(int s) {
        return 0;
    }

    public <T> T select(List<T> tlist) {
        if (tlist == null) {
            return null;
        }
        int s = tlist.size();
        if (s == 0) {
            return null;
        }
        else {
            return tlist.get(selectIndex(s));
        }
    }

    public static Gender fromString(String ks) {
        return EkeyHelper.ekeyFromString(Gender.class, ks);
    }

    private static final List<ImmutableList<Gender>> idLists = Lists.newArrayList();

    static {
        idLists.add(ImmutableList.of(mfn));
        idLists.add(ImmutableList.of(mf,n));
        idLists.add(ImmutableList.of(m,f,n));
    }

    public static List<Gender> getIdList(int s) {
        Preconditions.checkArgument(s > 0 && s <= 3);
        return idLists.get(s-1);
    }

    public static String elementId (int p, int s) {
        return getIdList(s).get(p).toString();
    }

    public interface Key {
        public Gender getGender();
    }
}
