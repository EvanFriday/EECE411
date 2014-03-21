/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.message;

public class Key {
	public static final int SIZE = 32;
	public static final int MAX_NUM = 40000;
	
	private byte[] value;
	
	public Key() {
		this.value = new byte[SIZE];
	}
	
	public Key(byte[] key) {
		this.value = key;
	}
	
	public Key(byte[] message, int offset) {
		this();
		for (int i = 0; i < SIZE; i++) {
			this.value[i] = message[offset + i];
		}
	}
	
	public byte[] getValue() {
		return this.value;
	}
}