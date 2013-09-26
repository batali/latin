package latin.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SortedMapTest {

    @Test
    public void testBinarySearch() {
        List<Integer> integerList = Arrays.asList(1,3,5,7);
        assertEquals(0, Collections.binarySearch(integerList, 1));
        assertEquals(2, Collections.binarySearch(integerList, 5));
        // ip = (- rp - 1)
        // ip + 1 = - rp
        // rp = (- ip - 1)
        assertEquals(-1, Collections.binarySearch(integerList, 0));
        assertEquals(-5, Collections.binarySearch(integerList, 8));
    }

    @Test
    public void testSortedMap() {
        Map<String,String> sm = Maps.newHashMap();
        sm.put("alf", "alfy");
        sm.put("balf", "balfy");
        sm.put("coo", "cooey");
        sm.put("doof", "doofy");
        sm.put("eep", "eepy");
        ImmutableSortedMap<String,String> ism = new ImmutableSortedMap<String,String>(sm, Ordering.<String>natural());
        String s1 = ism.getValue("alf");
        assertEquals("alfy", s1);
        ImmutableSortedMap<String,String> h1 = ism.headMap("boo");
        assertEquals("alf", h1.firstKey());
        assertEquals("balf", h1.lastKey());
        ImmutableSortedMap<String,String> h2 = ism.headMap("doof");
        assertEquals("alf", h2.firstKey());
        assertEquals("coo", h2.lastKey());
        assertNull(h2.get("doof"));
        ImmutableSortedMap<String,String> t1 = ism.tailMap("boo");
        assertEquals("coo", t1.firstKey());
        assertEquals("eep", t1.lastKey());
        ImmutableSortedMap<String,String> t2 = ism.tailMap("doof");
        assertEquals("doof", t2.firstKey());
        assertEquals("eep", t2.lastKey());

    }




}