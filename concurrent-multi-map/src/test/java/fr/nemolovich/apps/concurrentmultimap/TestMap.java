package fr.nemolovich.apps.concurrentmultimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Nemolovich
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMap {

    public static ConcurrentMultiMap<Integer, String> map;
    public static final int MAX_OCC = 10;
    public static final String VALUE_TEMPLATE = "Value%02d";

    @BeforeClass
    public static void init() {
        map = new ConcurrentMultiMap<>();
    }

    @Before
    public void before() {
        assertTrue(map.isEmpty());
    }

    @Test
    public void test1() {
        fillValues(MAX_OCC);
        assertEquals(MAX_OCC, map.size());
        checkValues(MAX_OCC);
    }

    @Test
    public void test2() {
        fillValues(MAX_OCC);
        assertTrue(MAX_OCC > 0);
        int canNotAdd = MAX_OCC - 1;
        map.putIfAbsent(canNotAdd, String.format(VALUE_TEMPLATE, canNotAdd));
        assertEquals(MAX_OCC, map.size());
        checkValues(MAX_OCC);
    }

    @Test
    public void test3() {
        fillValues(MAX_OCC);
        assertTrue(MAX_OCC > 2);
        int remove1 = MAX_OCC - 1;
        int remove2 = MAX_OCC - 2;
        assertFalse(map.remove(remove1, String.format(VALUE_TEMPLATE,
            remove2)));
        assertTrue(map.remove(remove1, String.format(VALUE_TEMPLATE,
            remove1)));
        assertEquals(map.remove(remove2), String.format(VALUE_TEMPLATE,
            remove2));
        assertNull(map.remove(remove2));
        assertEquals(MAX_OCC - 2, map.size());
        checkValues(MAX_OCC - 2);
    }

    @Test
    public void test4() {
        Map<Integer, String> map2 = new HashMap<>();
        for (int i = 0; i < MAX_OCC; i++) {
            map2.put(i, String.format(VALUE_TEMPLATE, i));
        }
        assertEquals(MAX_OCC, map2.size());
        map.putAll(map2);
        assertEquals(map2.size(), map.size());
        checkValues(MAX_OCC);
    }

    @Test
    public void test5() {
        fillValues(MAX_OCC);
        assertEquals(MAX_OCC, map.size());

        assertTrue(MAX_OCC > 1);
        int replace = MAX_OCC - 1;
        String value = map.get(replace);
        String newValue = "newValue";
        String oldValue = map.replace(replace, newValue);
        assertEquals(value, oldValue);
        assertEquals(newValue, map.get(replace));
        assertFalse(map.replace(replace, oldValue, oldValue));
        assertTrue(map.replace(replace, newValue, oldValue));
        checkValues(MAX_OCC);
    }

    @Test
    public void test6() {
        fillValues(MAX_OCC);
        checkValues(MAX_OCC);
        Set<Integer> keys = map.keySet();
        Collection<String> values = map.values();
        Set<Entry<Integer, String>> entries = map.entrySet();
        assertEquals(MAX_OCC, entries.size());
        assertEquals(keys.size(), entries.size());
        assertEquals(values.size(), entries.size());
        for (Entry<Integer, String> e : entries) {
            Integer k = e.getKey();
            String v = e.getValue();
            assertTrue(map.containsKey(k));
            assertTrue(map.containsValue(v));
            assertEquals(v, map.get(k));
            assertTrue(keys.contains(k));
            assertTrue(values.contains(v));
        }
    }

    public void fillValues(int max) {
        for (int i = 0; i < max; i++) {
            map.putIfAbsent(i, String.format(VALUE_TEMPLATE, i));
        }
    }

    public void checkValues(int max) {
        for (int i = 0; i < max; i++) {
            assertTrue(map.containsKey(i));
            assertTrue(map.containsValue(String.format(VALUE_TEMPLATE, i)));
            assertEquals(String.format(VALUE_TEMPLATE, i), map.get(i));
        }
    }

    @After
    public void after() {
        map.clear();
    }

    @AfterClass
    public static void end() {
        assertTrue(map.isEmpty());
    }
}
