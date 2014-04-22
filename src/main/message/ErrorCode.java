package main.message;

/**
 * Represents the lead byte of a reply Message, mapping to a reponse to a
 * command received.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public enum ErrorCode implements LeadByte {
	OK((byte) 0x00),
	KEY_DNE((byte) 0x01),
	OUT_OF_SPACE((byte) 0x02),
	OVERLOAD((byte) 0x03),
	KVSTORE_FAIL((byte) 0x04),
	BAD_COMMAND((byte) 0x05),
	;

	static final int SIZE = 1;
	private byte hexValue;

	private ErrorCode(byte hex) {
		this.hexValue = hex;
	}
	
	/**
	 * Find the correct ErrorCode from the raw byte.
	 * 
	 * @param hex
	 *            The raw byte to be converted.
	 * @return The ErrorCode that is represented by the byte given, or null if no
	 *         ErrorCode matches.
	 */
	public static ErrorCode getErrorCode(byte hex) {
		for (ErrorCode c : ErrorCode.values()) {
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