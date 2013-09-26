
package latin.util;

import com.google.common.base.Preconditions;

import java.util.UUID;

public class TitanUUID {

    /**
     * "Base" UUID whose bits will be combined with those of a TitanId to create
     * the UUID for the TitanId.
     * (It is just a random UUID.)
     */
    static final UUID bid = UUID.fromString("c5d63753-5c4b-4e19-b3de-26f83a318397");

    /** The bottom 32 bits of the TitanId will be combined with the bottom 32 bits of the
     * leastSignificantBits of the base UUID.
     */
    static final long lmsk = (-1L)>>>32;

    /** The top 32 bits of the TitanId will be combined with the top 32 bits of the
     * mostSignificanBits of the base UUID.
     */
    static final long hmsk = ~lmsk;

    // Construct the UUID from the TitanId and the base uuid.
    static UUID makeTitanUUID(long tid) {
        long lv = bid.getLeastSignificantBits() ^ (tid & lmsk);
        long hv = bid.getMostSignificantBits() ^ (tid & hmsk);
        return new UUID(hv,lv);
    }

    // Construct the TitanId value this UUID was created from.
    static long getTitanId(UUID tuid) {
        Preconditions.checkArgument(isTitanUUID(tuid));
        return ((tuid.getLeastSignificantBits()^bid.getLeastSignificantBits())&lmsk |
                (tuid.getMostSignificantBits()^bid.getMostSignificantBits())&hmsk);
    }

    // Check whether this UUID was constructed using makeTitanUUID.
    // (Can return false positive with probability 1/2^64.)
    static boolean isTitanUUID(UUID tuid) {
        // Note that the UUID's "variant" value is stored in the top bits of its lsb, and
        // the UUID's "version" value is stored in the lower 32 bits of its msb.  So both values
        // will be the same as they are in the base UUID.
        return ((tuid.getMostSignificantBits()&lmsk) == (bid.getMostSignificantBits()&lmsk) &&
                (tuid.getLeastSignificantBits()&hmsk) == (bid.getLeastSignificantBits()&hmsk));
    }

    // Don't instantiate: only use static methods
    private TitanUUID() {
    }
}