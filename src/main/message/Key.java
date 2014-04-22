package main.message;

import java.util.Arrays;

/**
 * Represents the key portion of a Message, which maps to a value stored in, or
 * to be stored in the system.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public class Key {
	public static final int SIZE = 32;
	public static final int MAX_NUM = 40000;
	private byte[] hexValue;
	
	/**
	 * Creates a new Key from existing data.
	 * 
	 * @param message A raw Message containing a key. 
	 * @param offset The index of the beginning of the key in the message.
	 */
	public Key(byte[] message, int offset) {
		this(Arrays.copyOfRange(message, offset, offset + SIZE));
	}

	/**
	 * Creates a new Key from existing data.
	 * 
	 * @param key An array of 32 bytes to be made into a Key object.  
	 */
	public Key(byte[] key) {
		this.setHexValue(Arrays.copyOf(key, SIZE));
	}
	
	/**
	 * Indicates whether some other key is "equal to" this one.
	 * 
	 * @param k
	 *            The reference key with which to compare.
	 * @return {@code true} if this Key is the same as the k argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Key k) {
		return Arrays.equals(this.hexValue, k.getHexValue());
	}
	
	@Override
	public String toString() {
		return new String(this.getHexValue());
	}
	
	/**
	 * @return The value of this key.
	 */
	public byte[] getHexValue() {
		return hexValue;
	}

	/**
	 * @param hex
	 *            The value to set to this key
	 */
	public void setHexValue(byte[] hex) {
		this.hexValue = hex;
	}
}