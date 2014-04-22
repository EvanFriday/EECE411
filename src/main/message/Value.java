package main.message;

import java.util.Arrays;

public class Value {
	public static final int SIZE = 1024;
	private byte[] hexValue;
	
	/**
	 * Creates a new Value from existing data.
	 * 
	 * @param message A raw Message containing a value. 
	 * @param offset The index of the beginning of the value in the message.
	 */
	public Value(byte[] message, int offset) {
		this(Arrays.copyOfRange(message, offset, offset + SIZE));
	}

	/**
	 * Creates a new Value from existing data.
	 * 
	 * @param value An array of 1024 bytes to be made into a Value object.  
	 */
	public Value(byte[] value) {
		this.setHexValue(Arrays.copyOf(value, SIZE));
	}
	
	/**
	 * Indicates whether some other Value is "equal to" this one.
	 * 
	 * @param v
	 *            The reference Value with which to compare.
	 * @return {@code true} if this Value is the same as the v argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Value v) {
		return Arrays.equals(this.hexValue, v.getHexValue());
	}
	
	@Override
	public String toString() {
		return new String(this.getHexValue());
	}
	
	/**
	 * @return The value of this Value object.
	 */
	public byte[] getHexValue() {
		return hexValue;
	}

	/**
	 * @param hex
	 *            The value to set to this Value object.
	 */
	public void setHexValue(byte[] hex) {
		this.hexValue = hex;
	}
}