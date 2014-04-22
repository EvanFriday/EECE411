package main.message;

/**
 * Represents the lead byte of a Message, mapping to an action to be taken by
 * the system.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public enum Command implements LeadByte {
	PUT((byte) 0x01),
	GET((byte) 0x02),
	REMOVE((byte) 0x03),
	SHUTDOWN((byte) 0x04),
	;

	static final int SIZE = 1;
	private byte hexValue;

	private Command(byte hex) {
		this.hexValue = hex;
	}
	
	/**
	 * Find the correct Command from the raw byte.
	 * 
	 * @param hex
	 *            The raw byte to be converted.
	 * @return The Command that is represented by the byte given, or null if no
	 *         Command matches.
	 */
	public static Command getCommand(byte hex) {
		for (Command c : Command.values()) {
			if (c.hexValue == hex) {
				return c;
			}
		}
		return null;
	}

	public byte getHexValue() {
		return this.hexValue;
	}
}