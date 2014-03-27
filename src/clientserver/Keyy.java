package clientserver;

public class Keyy {
	public final static int SIZE = 32;
	private byte[] value;
	
	public Keyy() {
		this.value = new byte[SIZE];
	}
	
	public Keyy(byte[] k) {
		for(int i=0; i<k.length; i++) 
			this.value[i] = k[i];
	}
	
	public Keyy(byte[] message, int offset) {
		this();
		for (int i = 0; i < SIZE; i++) {
			this.value[i] = message[offset + i];
		}
	}
	
	public byte getKey(int index) {
		return this.value[index];
	}
	
	public byte[] getRaw() {
		return this.value;
	}
	
	public void setKey(byte[] k) {
		for(int i=0; i<k.length; i++) 
			this.value[i] = k[i];
	}
}
