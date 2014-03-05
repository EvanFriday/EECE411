package clientserver;

public class KeyValuePair {

	public static byte[] key = new byte[32];
	public static byte[] value = new byte[1024];
	
	// Constructor
	KeyValuePair(byte[] k, byte[] v)
	{
		key = k;
		value = v;
	}
	
	// Get and Set methods
	public static byte[] getKey() {
		return key;
	}
	public static void setKey(byte[] key) {
		KeyValuePair.key = key;
	}
	public static byte[] getValue() {
		return value;
	}
	public static void setValue(byte[] value) {
		KeyValuePair.value = value;
	}	
}
