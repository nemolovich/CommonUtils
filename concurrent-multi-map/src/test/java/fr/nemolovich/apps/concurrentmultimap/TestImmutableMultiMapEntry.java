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
public class TestImmutableMultiMapEntry {

    private static final Integer KEY = 1;
    private static final String VALUE = "1";

    @Test
    public void test1() {
        ImmutableMultiMapEntry<Integer, String> mme
            = new ImmutableMultiMapEntry(KEY, VALUE);
        assertEquals(KEY, mme.getKey());
        assertEquals(VALUE, mme.getValue());
    }

    @Test
    public void test2() {
        ImmutableMultiMapEntry<Integer, String> mme1
            = new ImmutableMultiMapEntry(KEY, VALUE);
        ImmutableMultiMapEntry<Integer, String> mme
            = new ImmutableMultiMapEntry(mme1);
        assertEquals(KEY, mme.getKey());
        assertEquals(VALUE, mme.getValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test3() {
        ImmutableMultiMapEntry<Integer, String> mme
            = new ImmutableMultiMapEntry(KEY, VALUE);
        assertEquals(KEY, mme.getKey());
        assertEquals(VALUE, mme.getValue());
        mme.setValue(VALUE);
    }

}
