package latin.util;

import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IDTest {

    static final UUID bid = UUID.fromString("c5d63753-5c4b-4e19-b3de-26f83a318397");
    static final long lmsk = (-1L)>>>32;
    static final long hmsk = ~lmsk;

    static UUID makeTitanUUID(long tid) {
        long lv = bid.getLeastSignificantBits() ^ (tid & lmsk);
        long hv = bid.getMostSignificantBits() ^ (tid & hmsk);
        return new UUID(hv,lv);
    }

    static long getTitanId(UUID tuid) {
        return ((tuid.getLeastSignificantBits()^bid.getLeastSignificantBits())&lmsk |
                (tuid.getMostSignificantBits()^bid.getMostSignificantBits())&hmsk);
    }

    static boolean isTitanUUID(UUID tuid) {
        return ((tuid.getMostSignificantBits()&lmsk) == (bid.getMostSignificantBits()&lmsk) &&
                (tuid.getLeastSignificantBits()&hmsk) == (bid.getLeastSignificantBits()&hmsk));
    }

    @Test
    public void testIdValue() {
        long lv = 0;
        long hv = 0;
        int ntimes = 10;
        for (int i = 0; i < ntimes; i++) {
            UUID uuid = UUID.randomUUID();
            if (i == 0) {
                lv = uuid.getLeastSignificantBits();
                hv = uuid.getMostSignificantBits();
            }
            else {
                lv &= uuid.getLeastSignificantBits();
                hv &= uuid.getMostSignificantBits();
            }
        }
        System.out.println(Long.toBinaryString(lv));
        System.out.println(Long.toBinaryString(hv));
        System.out.println(Long.toHexString(lv));
        System.out.println(Long.toHexString(hv));
        UUID cid = new UUID(hv,lv);
        assertEquals(4, cid.version());
        System.out.println(cid.toString());
        Random random = new Random();
        long tid = random.nextLong();
        System.out.println("tid " + Long.toString(tid));
        UUID tu = makeTitanUUID(tid);
        System.out.println("tu  " + tu.toString());
        assertTrue(isTitanUUID(tu));
        assertEquals(4, tu.version());
        long fid = getTitanId(tu);
        System.out.println("fid " + Long.toString(fid));
        assertEquals(tid, fid);
    }
}

