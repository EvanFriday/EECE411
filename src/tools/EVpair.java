package tools;

public class EVpair {
	private ErrorCode error;
	private Value value;
	public EVpair(ErrorCode error, Value value) {
		this.setError(error);
		this.setValue(value);
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	public ErrorCode getError() {
		return error;
	}
	public void setError(ErrorCode error) {
		this.error = error;
	}

}
