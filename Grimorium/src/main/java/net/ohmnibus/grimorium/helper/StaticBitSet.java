package net.ohmnibus.grimorium.helper;

import java.util.Arrays;

/**
 * Based on the code of D. Lemire<br />
 * blog: http://lemire.me/blog/ <br />
 * https://github.com/lemire/Code-used-on-Daniel-Lemire-s-blog/blob/master/2012/11/13/src/StaticBitSet.java
 */
public class StaticBitSet {

	private static final int BLOCK_SIZE = 512;
	long[] data;

	/**
	 * Initialize a new {@link StaticBitSet}.
	 */
	public StaticBitSet() {
		this(BLOCK_SIZE);
	}

	/**
	 * Initialize a new {@link StaticBitSet} allocating
	 * the specified size of bits.
	 * @param sizeInBits Number of bit to contain.
	 */
	protected StaticBitSet(int sizeInBits) {
		data = new long[getInnerSize(sizeInBits)];
	}

	/**
	 * Initialize the {@link StaticBitSet} setting to {@code true}
	 * given indexes.
	 * @param indexes Array of the indexes of the bit to set to {@code true}.
	 */
	public StaticBitSet(int[] indexes) {
		this();
		set(indexes);
	}

	/**
	 * Reset all the bits.
	 */
	public void clear() {
		Arrays.fill(data, 0);
	}

	protected void resize(int sizeInBits) {

		data = Arrays.copyOf(data, getInnerSize(sizeInBits));
	}

	/**
	 * Count the number of the bit set.
	 * @return Number of the bit set.
	 */
	public int size() {
		int sum = 0;
		for(long l : data)
			sum += Long.bitCount(l);
		return sum;
	}

	/**
	 * Return the status of the given bit.
	 * @param index Index of the bit.
	 * @return The status of the bit.
	 */
	public boolean get(int index) {
		int innerIndex = getInnerIndex(index);

		return innerIndex < data.length
				&& ((data[innerIndex] & (1L << (index % 64))) != 0);
	}

	/**
	 * Set a bit.
	 * @param index Index of the bit to set.
	 */
	public void set(int index) {
		int innerIndex = getInnerIndexWithResize(index);
		data[innerIndex] |= (1L << (index % 64));
	}

	/**
	 * Set specified bits.
	 * @param indexes Array of indexes of the bits to set.
	 */
	public void set(int[] indexes) {
		for (int index : indexes) {
			set(index);
		}
	}

	/**
	 * Unset a bit.
	 * @param index Index of the bit to unset.
	 */
	public void unset(int index) {
		int innerIndex = getInnerIndex(index);
		if (innerIndex < data.length) {
			data[innerIndex] &= ~(1L << (index % 64));
		}
	}

	/**
	 * Set or unset a bit.
	 * @param index Index of the bit to set/unset.
	 * @param value New value for the bit.
	 */
	public void set(int index, boolean value) {
		if(value) set(index); else unset(index);
	}

	/**
	 * Return an array of all the indexes of the bit set.
	 * @return Array of the indexes of the bit set
	 */
	public int[] toArray() {
		int[] retVal = new int[size()];
		int cnt = 0;
		for(int i=nextSetBit(0); i>=0; i=nextSetBit(i+1)) {
			retVal[cnt++] = i;
		}
		return retVal;
	}

	/**
	 * Get the index of the next bit set, starting from
	 * the given index.<br>
	 * To iterate through all the indexes of the bit set, use following code:<br>
	 * <pre>
	 * StaticBitSet bitSet = new StaticBitSet();
	 * //Populate bitSet
	 * for(int index = bitSet.nextSetBit(0);
	 *         index >= 0;
	 *         index = bitSet.nextSetBit(index + 1)) {
	 *
	 *     //operate on "index"
	 * }
	 * </pre>
	 * @param index Starting bit index from which to search
	 * @return Index of the next bit set, or {@code -1} if none found.
	 */
	public int nextSetBit(int index) {
		int x = index / 64;
		if(x>=data.length) return -1;
		long w = data[x];
		w >>>= (index % 64);
		if (w != 0) {
			return index + Long.numberOfTrailingZeros(w);
		}
		++x;
		for (; x < data.length; ++x) {
			if (data[x] != 0) {
				return x * 64 + Long.numberOfTrailingZeros(data[x]);
			}
		}
		return -1;
	}

	private int getInnerIndex(int index) {
		return index / 64;
	}

	private int getInnerIndexWithResize(int index) {
		int retVal = getInnerIndex(index);
		if (retVal >= data.length) {
			int newSize = (index + (BLOCK_SIZE - 1)) / BLOCK_SIZE;
			resize(newSize * BLOCK_SIZE);
		}
		return retVal;
	}

	private int getInnerSize(int sizeInBits) {
		return (sizeInBits + 63) / 64;
	}
}
