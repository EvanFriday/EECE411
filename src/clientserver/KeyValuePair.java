package clientserver;

public class KeyValuePair {

	public byte[] key = new byte[32];
	public byte[] value = new byte[1024];
	
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
	public byte[] getKey() {
		return this.key;
	}
	public void setKey(byte[] key) {
		this.key = key;
	}
	public byte[] getValue() {
		return this.value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}	
}
