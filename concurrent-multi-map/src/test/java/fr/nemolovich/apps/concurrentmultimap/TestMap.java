package fr.nemolovich.apps.concurrentmultimap;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
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
		map.clear();
	}

	@Test
	public void test1() {
		assertTrue(map.isEmpty());
		for (int i = 0; i < MAX_OCC; i++) {
			map.put(i, String.format(VALUE_TEMPLATE, i));
		}
		assertEquals(MAX_OCC, map.size());
		for (int i = 0; i < MAX_OCC; i++) {
			assertTrue(map.containsKey(i));
			assertTrue(map.containsValue(String.format(VALUE_TEMPLATE, i)));
			assertEquals(String.format(VALUE_TEMPLATE, i), map.get(i));
		}
	}

	@AfterClass
	public static void end() {

	}
}
