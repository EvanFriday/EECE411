/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver.message;

public class Key {
	public static final int SIZE = 32;
	public static final int MAX_NUM = 40000;
	private byte[] value = new byte[32];
	
	public Key() {
	//	this.value = new byte[SIZE];
	}
	
	public Key(byte[] key) {
		for(int i = 0; i< Key.SIZE; i++){
			this.value[i]=key[i];
		}
	}
	
	public Key(byte[] message, int offset) {
		this();
		for (int i = 0; i < SIZE; i++) {
			this.value[i] = message[offset + i];
		}
	}
	
	public byte getValue(int index) {
		
		return value[index];
	}
	
}