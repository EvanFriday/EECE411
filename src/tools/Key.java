package tools;

public class Key {
	public static final int SIZE = 32; //as we loop from 0-31 this gives us 32 bytes.
	public static final int MAX_NUM = 40000;

	public byte[] key = null;
	//private int hashCode;

	public Key() {
		this.key = new byte[SIZE];
		//this.hashCode = this.getHash();
	}

	public Key(byte[] key) {
		this();
		if(key != null){
			for(int i = 0; i< Key.SIZE; i++){
				this.setValue(key[i],i);
			}
		}
		//this.hashCode = this.getHash();
	}

	public Key(Key key){
		this();
		if(key != null){
			for(int i = 0; i< Key.SIZE; i++){
				this.setValue(key.getValue(i),i);
			}
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