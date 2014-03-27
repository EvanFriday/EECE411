/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver.message;

public class Key {
	public static final int SIZE = 32; //as we loop from 0-31 this gives us 32 bytes.
	public static final int MAX_NUM = 40000;
	private byte[] key = new byte[SIZE];
	
	public Key() {
	this.key = new byte[SIZE];
	}
	
	public Key(byte[] key) {
		this();
		for(int i = 0; i< Key.SIZE; i++){
			this.setValue(key[i],i);
		}
	}
	
	public Key(byte[] message, int offset) {
		this();
		for (int i = 0; i < SIZE; i++) {
			this.setValue(message[offset + i],i);
		}
	}
	public Key(Key k){
		this();
		for(int i = 0; i< Key.SIZE; i++){
			this.setValue(k.getValue(i),i);
		}
	}
	
	public byte getValue(int index) {
		return key[index];
	}
	
	public void setValue(byte value, int index){
		this.key[index] = value;
	}
	
<<<<<<< HEAD
	public byte getKey(int index) {
		return this.value[index];
	}
	
	public void setKey(byte b, int index) {
		this.value[index] = b;
	}
=======
>>>>>>> 434ee628b614afb9c2c77e64736d260cca870f77
}