package tools;

public enum ErrorCode implements LeadByte {
	OK((byte) 0x00),
	KEY_DNE((byte) 0x01),
	OUT_OF_SPACE((byte) 0x02),
	OVERLOAD((byte) 0x03),
	KVSTORE_FAIL((byte) 0x04),
	BAD_COMMAND((byte) 0x05);

	public static final int SIZE = 1;

	private byte value;

	private ErrorCode(byte hex) {
		this.value = hex;
	}

	public static ErrorCode getErrorCode(byte hex) {
		for (ErrorCode c : ErrorCode.values()) {
			if (c.value == hex) {
				return c;
			}
		}

		return null;
	}

	public byte getByte() {
		return this.value;
	}
	public void setByte(byte value){
		this.value = value;
	}
}
