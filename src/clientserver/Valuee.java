package clientserver;

public class Valuee {
	public static final int SIZE = 1024;
	private byte[] value;
	
	public Valuee() {
		this.value = new byte[SIZE];
	}
	
	public Valuee(byte[] b) {
		this.value = b;
	}
	
	public byte getValue(int index) {
		return value[index];
	}
	
	public void setValue(byte v, int index) {
		value[index] = v;
	}
}
