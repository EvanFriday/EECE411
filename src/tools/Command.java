/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package tools;

public enum Command implements LeadByte{

	
	PUT((byte) 0x01),
	GET((byte) 0x02),
	REMOVE((byte) 0x03),
	SHUTDOWN((byte) 0x04),
	PROP_PUT((byte) 0x21),
	PROP_GET((byte) 0x22),
	PROP_REMOVE((byte) 0x23),
	PROP_SHUTDOWN((byte) 0x24),
	DEATH((byte) 0x40),
	REPLICA_PUT((byte) 0x41),
	REPLICA_REMOVE((byte) 0x41);
	public static final int SIZE = 1;
	private byte value;

	private Command(byte hex) {
		this.value = hex;
	}

	public static Command getCommand(byte hex) {
		for (Command c : Command.values()) {
			if (c.value == hex) {
				return c;
			}
		}
		return null;
	}

	public byte getByte() {
		return this.value;
	}
}