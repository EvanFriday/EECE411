package clientserver;

public class KeyValuePair {

	private byte[] key = new byte[32];
	private byte[] value = new byte[1024];
	
	// Constructor
	KeyValuePair(byte[] k, byte[] v)
	{
		key = k;
		value = v;
	}
	KeyValuePair(){
		int i;
		for(i=0; i<32; i++)
			key[i] = 0;
		for(i=0; i<1024; i++)
			value[i] = 0;
	}
	
	// Get and Set methods
	public byte getKey(int index) {
		return key[index];
	}
	public void setKey(byte k, int index) {
		key[index] = k;
	}
	public byte getValue(int index) {
		return value[index];
	}
	public void setValue(byte v, int index) {
		value[index] = v;
	}	
}
