/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver.message;

public class Value {
	public static final int SIZE = 1024;
	private byte[] value;
	
	public Value() {
		this.value = new byte[SIZE];
	}
	
	public Value(byte[] value) {
		for(int i = 0; i< Value.SIZE; i++){
			this.value[i]=value[i];
		}
	}
	
	public Value(byte[] message, int offset) {
		this();
		for (int i = 0; i < SIZE; i++) {
			this.value[i] = message[offset + i];
		}
	}
	
	public byte[] getValue() {
		byte[] temp = new byte[Value.SIZE];
		
		for(int i=0;i<Key.SIZE;i++){
			temp[i]=value[i];
		}
		return temp;
	}
}