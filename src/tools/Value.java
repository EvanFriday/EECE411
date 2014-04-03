package tools;

public class Value {
	public static final int SIZE = 1024;

	private byte[] value;

	public Value() {
		this.value = new byte[SIZE];
	}

	public Value(byte[] value) {
		this();
		for(int i = 0; i< Value.SIZE; i++){
			this.value[i]=value[i];
		}
	}
	public Value(Value v){
		this();
		for(int i = 0; i< Value.SIZE; i++){
			this.value[i]=v.getValue(i);
		}
	}

	public byte getValue(int index) {
		return this.value[index];
	}
	public void setValue(byte value, int index){
		this.value[index] = value;
	}
}