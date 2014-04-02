/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.message;

public enum ErrorCode implements LeadByte {
	OK((byte) 0x00),
	KEY_DNE((byte) 0x01),
	OUT_OF_SPACE((byte) 0x02),
	OVERLOAD((byte) 0x03),
	KVSTORE_FAIL((byte) 0x04),
	BAD_COMMAND((byte) 0x05);

	public static final int SIZE = 1;

	private byte hexValue;

	private ErrorCode(byte hex) {
		this.hexValue = hex;
	}

	public static ErrorCode getErrorCode(byte hex) {
		for (ErrorCode c : ErrorCode.values()) {
			if (c.hexValue == hex) {
				return c;
			}
		}

		return null;
	}

	public byte getHex() {
		return this.hexValue;
	}
}