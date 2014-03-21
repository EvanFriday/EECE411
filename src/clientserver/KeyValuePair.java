/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

@Deprecated
public class KeyValuePair {


	public byte[] key; 
	public byte[] value;
	
	// Constructor
	KeyValuePair(byte[] k, byte[] v)
	{
		key = new byte[32];
		value = new byte[1024];
		key = k;
		value = v;
	}

	KeyValuePair(){
		
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
