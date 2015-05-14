package fr.nemolovich.apps.concurrentmultimap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Nemolovich
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMultiMapEntry {

    private static final Integer KEY = 1;
    private static final String VALUE = "1";

    @Test
    public void test1() {
        MultiMapEntry<Integer, String> mme = new MultiMapEntry(KEY);
        assertEquals(KEY, mme.getKey());
        assertNull(mme.getValue());
        String newValue = "newValue";
        assertNull(mme.setValue(newValue));
        assertEquals(newValue, mme.setValue(VALUE));
        assertEquals(VALUE, mme.getValue());
    }

    @Test
    public void test2() {
        MultiMapEntry<Integer, String> mme = new MultiMapEntry(KEY, VALUE);
        assertEquals(KEY, mme.getKey());
        assertEquals(VALUE, mme.getValue());
    }

    @Test
    public void test3() {
        MultiMapEntry<Integer, String> mme1 = new MultiMapEntry(KEY, VALUE);
        MultiMapEntry<Integer, String> mme = new MultiMapEntry(mme1);
        assertEquals(KEY, mme.getKey());
        assertEquals(VALUE, mme.getValue());
    }

}
