/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

@Deprecated
public class KeyValuePair {
<<<<<<< HEAD

	public byte[] key; 
	public byte[] value;
	
	// Constructor
	KeyValuePair(byte[] k, byte[] v)
	{
		key = new byte[32];
=======
	private byte[] key = new byte[32];
	private byte[] value = new byte[1024];
	
	@Deprecated
	KeyValuePair(byte[] k, byte[] v) {
>>>>>>> b02d5ca21e806f8d82485229e853bc2a8fcfcbeb
		key = k;
		value = new byte[1024];
		value = v;
	}
<<<<<<< HEAD
	KeyValuePair(){
		
		//key = new byte[32];
		//value = new byte[1024];
		//for(int i=0; i<32; i++)
		//	key[i] = 0;
		//for(int i=0; i<1024; i++)
		//	value[i] = 0;
=======
	
	@Deprecated
	KeyValuePair() {
		int i;
		for(i=0; i<32; i++)
			key[i] = 0;
		for(i=0; i<1024; i++)
			value[i] = 0;
>>>>>>> b02d5ca21e806f8d82485229e853bc2a8fcfcbeb
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
