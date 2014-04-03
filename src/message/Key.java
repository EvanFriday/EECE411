/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package message;

public class Key {
	public static final int SIZE = 32; //as we loop from 0-31 this gives us 32 bytes.
	public static final int MAX_NUM = 40000;

	private byte[] key;


	public Key() {
	this.key = new byte[SIZE];
	}

	public Key(byte[] k) {
		this();
		this.setValue(k);
	}

	public Key(Key k){
		this();
		this.setValue(k.getValue());
	}

	public byte[] getValue() {
		return key;
	}

	public void setValue(byte[] v){
		this.key = v;
	}

}