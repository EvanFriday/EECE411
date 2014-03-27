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
		this();
		for(int i = 0; i< Value.SIZE; i++){
			this.value[i]=value[i];
		}
	}
	public Value(Value v){
		this();
		for(int i = 0; i< Value.SIZE; i++){
			this.value[i]=v.getValue(i);
		}
	}
	
	public Value(byte[] message, int offset) {
		this();
		for (int i = 0; i < SIZE; i++) {
			this.setValue(message[offset + i], i);
		}
	}
	
	public byte getValue(int index) {
		return this.value[index];
	}
	public void setValue(byte value, int index){
		this.value[index] = value;
	}
}