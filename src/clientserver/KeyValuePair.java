/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

@Deprecated
public class KeyValuePair {
	private byte[] key = new byte[32];
	private byte[] value = new byte[1024];
	
	@Deprecated
	KeyValuePair(byte[] k, byte[] v) {
		key = k;
		value = v;
	}
	
	@Deprecated
	KeyValuePair() {
		int i;
		for(i=0; i<32; i++)
			key[i] = 0;
		for(i=0; i<1024; i++)
			value[i] = 0;
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
