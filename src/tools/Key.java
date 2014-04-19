package tools;

public class Key {
	public static final int SIZE = 32; //as we loop from 0-31 this gives us 32 bytes.
	public static final int MAX_NUM = 40000;

	public byte[] key;
	//private int hashCode;

	public Key() {
		this.key = new byte[SIZE];
		//this.hashCode = this.getHash();
	}

	public Key(byte[] key) {
		this();
		for(int i = 0; i< Key.SIZE; i++){
			this.setValue(key[i],i);
		}
		//this.hashCode = this.getHash();
	}

	public Key(Key k){
		this();
		for(int i = 0; i< Key.SIZE; i++){
			this.setValue(k.getValue(i),i);
		}
		//this.hashCode = this.getHash();
	}

	public Key(byte[] message, int offset) {
		this();
		for(int i=0; i<Key.SIZE; i++) {
			this.setValue(message[i+offset], i);
		}
		//this.hashCode = this.getHash();
	}

	public byte getValue(int index) {
		return key[index];
	}

	public void setValue(byte value, int index){
		this.key[index] = value;
	}

//	public int getHash() {
//		return this.hashCode;
//	}
}