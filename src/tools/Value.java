package tools;

public class Value {
	public static final int SIZE = 1024;

	public byte[] value = null;

	public Value() {
		this.value = new byte[SIZE];
	}

	public Value(byte[] value) {
		this();
		if(value != null){
			for(int i = 0; i< Value.SIZE; i++){
				this.value[i]=value[i];
			}
		}
		else
			this.value = null;
		
	}
	public Value(Value value){
		this();
		if(value != null){
			
			for(int i = 0; i< Value.SIZE; i++){
				this.value[i]=value.getValue(i);
			}
		}
		else
			this.value = null;
	}

	public Value(byte[] message, int offset) {
		this();
		for(int i=0; i< Value.SIZE; i++) {
			this.setValue(message[i+offset], i);
		}
	}

	public byte getValue(int index) {
		return this.value[index];
	}
	public void setValue(byte value, int index){
		this.value[index] = value;
	}
}