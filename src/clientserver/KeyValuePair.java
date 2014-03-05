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
		key = null;
		value = null;
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
