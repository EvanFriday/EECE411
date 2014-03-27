/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.util.Map;

import clientserver.message.Value;

//@Deprecated
public class KeyValuePair {

	public final static int KEY_SIZE = 32;
	public final static int VALUE_SIZE = 1024;
	private byte[] key;
	private byte[] value;
	
	// Constructors
	KeyValuePair(byte[] kk, byte[] vv)
	{
		this.key = kk;
		this.value = vv;
	}

	KeyValuePair(){
		this.key = new byte[KEY_SIZE];
		this.value = new byte[VALUE_SIZE];
	}	
	
	//@Deprecated
	public byte getKey(int index) {
		return key[index];
	}
	
	//@Deprecated
	public void setKey(byte k, int index) {
		key[index] = k;
	}
	
	//@Deprecated
	public byte getValue(int index) {
		return value[index];
	}
	
	//@Deprecated
	public void setValue(byte v, int index) {
		value[index] = v;
	}
		
}
