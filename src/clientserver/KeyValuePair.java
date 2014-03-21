/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver;

@Deprecated
public class KeyValuePair {
	private byte[] key; 
	private byte[] value;
	
	@Deprecated
	public KeyValuePair(byte[] k, byte[] v) {
		key = new byte[32];
		value = new byte[1024];
		key = k;
		value = v;
	}

	@Deprecated
	public KeyValuePair() {
		//key = new byte[32];
		//value = new byte[1024];
		//for(int i=0; i<32; i++)
		//	key[i] = 0;
		//for(int i=0; i<1024; i++)
		//	value[i] = 0;
	}
	
	@Deprecated
	public byte getKey(int index) {
		return key[index];
	}
	
	@Deprecated
	public void setKey(byte k, int index) {
		key[index] = k;
	}
	
	@Deprecated
	public byte getValue(int index) {
		return value[index];
	}
	
	@Deprecated
	public void setValue(byte v, int index) {
		value[index] = v;
	}	
}
