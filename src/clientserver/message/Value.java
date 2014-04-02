/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.message;

public class Value {
	public static final int SIZE = 1024;

	private byte[] value;

	public Value() {
		this.value = new byte[SIZE];
	}

	public Value(byte[] v) {
		this();
		this.setValue(v);
	}

	public byte[] getValue() {
		return this.value;
	}
	public void setValue(byte[] v){
		this.value = v;
	}
}