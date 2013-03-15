
package latin.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Shuffler {

    private Shuffler () {
    }

    public static final Random random = new Random();

    public static <T> boolean adjoin (T newElement, Collection<T> collection) {
        if (collection.contains(newElement)) {
            return false;
        }
        else {
            collection.add(newElement);
            return true;
        }
    }

    public static <T> T randomElement(List<T> tlist) {
        int s = tlist.size();
        if (s == 0) {
            return null;
        }
        else if (s == 1) {
            return tlist.get(0);
        }
        else {
            return tlist.get(random.nextInt(s));
        }
    }
   //  http://stackoverflow.com/questions/1608181/unique-random-numbers-in-an-integer-array-in-the-c-programming-language/1608585#1608585
    /*
    #define M 10
    #define N 100

    int in, im;

    im = 0;

    for (in = 0; in < N && im < M; ++in) {
        int rn = N - in;
        int rm = M - im;
        if (rand() % rn < rm)
            vektor[im++] = in + 1;
    }
    assert(im == M);
   */

    public static void radd(List<Integer> ilist, Integer I) {
        int s = ilist.size();
        if (s > 0) {
            int p = random.nextInt(s+1);
            if (p < s) {
                Integer J = ilist.get(p);
                ilist.set(p, I);
                I = J;
            }
        }
        ilist.add(I);
    }

    public static List<Integer> knuthChoose(int n, int k) {
        List<Integer> chosen = new ArrayList<Integer>(k);
        int ik = 0;
        for (int in = 0; in < n && ik < k; ++in) {
            if (random.nextInt(n - in) < (k - ik)) {
                radd(chosen, in);
                ik += 1;
            }
        }
        return chosen;
    }

    /*
    #define M 10
    #define N 100

    unsigned char is_used[N] = { 0 };
    int in, im;

    im = 0;

    for (in = N - M; in < N && im < M; ++in) {
        int r = rand() % (in + 1);

        if (is_used[r])
            r = in;

        assert(!is_used[r]);
        vektor[im++] = r + 1;
        is_used[r] = 1;
    }
    assert(im == M);
    */

    public static List<Integer> floydChoose(int n, int k) {
        List<Integer> chosen = new ArrayList<Integer>(k);
        for (int in = n - k; in < n; in++) {
            int r = random.nextInt(in+1);
            if (chosen.contains(r)) {
                radd(chosen, in);
            }
            else {
                radd(chosen, r);
            }
        }
        return chosen;
    }

    public static List<Integer> choose(int n, int k) {
        if (n + n > k) {
            return knuthChoose(n, k);
        }
        else {
            return floydChoose(n, k);
        }
    }

    public static <T> List<T> psel(List<T> fromList, List<Integer> positions) {
        List<T> toList = Lists.newArrayList();
        for (Integer p : positions) {
            toList.add(fromList.get(p));
        }
        return toList;
    }

    public static <T> List<T> rsel(List<T> fromList, int tosize) {
        return psel(fromList, choose(fromList.size(), tosize));
    }

}