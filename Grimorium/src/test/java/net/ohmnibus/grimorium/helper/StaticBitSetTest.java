package net.ohmnibus.grimorium.helper;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ohmnibus on 26/08/2016.
 */
public class StaticBitSetTest {

	@Test
	public void testClear() throws Exception {
		StaticBitSet bitSet = new StaticBitSet();
		bitSet.set(16, true);
		assertTrue("Bit not set.", bitSet.get(16));
		bitSet.set(129, true);
		assertTrue("Bit not set.", bitSet.get(129));
		bitSet.set(2029, true);
		assertTrue("Bit not set.", bitSet.get(2029));

		bitSet.clear();
		assertFalse("Bit set.", bitSet.get(16));
		assertFalse("Bit set.", bitSet.get(129));
		assertFalse("Bit set.", bitSet.get(2029));
		assertEquals("Bit set.", 0, bitSet.size());
	}

	@Test
	public void testResize() throws Exception {
		StaticBitSet bitSet = new StaticBitSet(15);
		bitSet.set(16, true);
		assertTrue("Extra bit not set.", bitSet.get(16));
		bitSet.set(129, true);
		assertTrue("Extra bit not set.", bitSet.get(129));
		bitSet.set(2029, true);
		assertTrue("Extra bit not set.", bitSet.get(2029));
	}

	@Test
	public void testCardinality() throws Exception {
		StaticBitSet bitSet = new StaticBitSet();
		bitSet.set(16, true);
		assertEquals("Bit not set.", 1, bitSet.size());
		bitSet.set(129, true);
		assertEquals("Bit not set.", 2, bitSet.size());
		bitSet.set(2029, true);
		assertEquals("Bit not set.", 3, bitSet.size());
		bitSet.clear();
		assertEquals("Bit set.", 0, bitSet.size());
	}

	@Test
	public void testGet() throws Exception {
		StaticBitSet bitSet = new StaticBitSet();
		assertFalse("Bit set.", bitSet.get(16));
		bitSet.set(16, true);
		assertTrue("Bit not set.", bitSet.get(16));
	}

	@Test
	public void testSet() throws Exception {

	}

	@Test
	public void testUnset() throws Exception {
		StaticBitSet bitSet = new StaticBitSet();
		bitSet.set(2029, true);
		assertTrue("Bit not set.", bitSet.get(2029));
		bitSet.set(2029, false);
		assertFalse("Bit set.", bitSet.get(2029));
	}

	@Test
	public void testSet1() throws Exception {

	}

	@Test
	public void testToArray() throws Exception {
		StaticBitSet bitSet = new StaticBitSet();
		bitSet.set(16, true);
		bitSet.set(129, true);
		bitSet.set(2029, true);
		int[] set = bitSet.toArray();
		assertEquals("Wrong size", 3, set.length);
		assertEquals("Wrong value", 16, set[0]);
		assertEquals("Wrong value", 129, set[1]);
		assertEquals("Wrong value", 2029, set[2]);
	}

	@Test
	public void testNextSetBit() throws Exception {
		StaticBitSet bitSet = new StaticBitSet();
		bitSet.set(0, true);
		bitSet.set(1, true);
		bitSet.set(2, true);
		bitSet.set(3, true);
		bitSet.set(5, true);
		bitSet.set(7, true);
		bitSet.set(11, true);
		assertEquals("", 0, bitSet.nextSetBit(0));
		assertEquals("", 1, bitSet.nextSetBit(1));
		assertEquals("", 2, bitSet.nextSetBit(2));
		assertEquals("", 3, bitSet.nextSetBit(3));
		assertEquals("", 5, bitSet.nextSetBit(4));
		assertEquals("", 7, bitSet.nextSetBit(6));
		assertEquals("", 11, bitSet.nextSetBit(8));
	}
}