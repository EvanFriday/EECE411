/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver.message;

public class Message {
	public static final int MAX_SIZE = Command.SIZE + Key.SIZE + Value.SIZE;
	private LeadByte lead;
	private Key key;
	private Value value;
	
	public Message() {
		this(null, null, null);
	}
	
	public Message(LeadByte l) {
		this(l, null, null);
	}
	
	public Message(LeadByte l, Key k) {
		this(l, k, null);
	}
	
	public Message(LeadByte l, Value v) {
		this(l, null, v);
	}
	
	public Message(LeadByte l, Key k, Value v) {
		this.setLeadByte(l);
		this.setKey(k);
		this.setValue(v);
	}
	
	public Message(byte[] message) {
		switch (message.length) {
		case Command.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			break;
		case Command.SIZE + Key.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			this.setKey(new Key(message, Command.SIZE));
			break;
		case Command.SIZE + Value.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			this.setValue(new Value(message, Command.SIZE));
			break;
		case Command.SIZE + Key.SIZE + Value.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			this.setKey(new Key(message, Command.SIZE));
			this.setValue(new Value(message, Command.SIZE + Key.SIZE));
			break;
		default:
			throw new NullPointerException("Message is a strange length.");
		}
	}
	
	public byte[] getRaw() {
		int size = 0;
		size += (this.lead != null ? Command.SIZE : 0);
		size += (this.key != null ? Key.SIZE : 0);
		size += (this.value != null ? Value.SIZE : 0);
		byte[] raw = new byte[size];
		byte[] keyRaw, valueRaw;
		
		switch (size) {
		case Command.SIZE:
			raw[0] = this.lead.getHex();
			break;
		case Command.SIZE + Key.SIZE:
			raw[0] = this.lead.getHex();
			keyRaw = this.key.getValue();
			
			for (int i = 0; i < Key.SIZE; i++) {
				raw[Command.SIZE + i] = keyRaw[i];
			}
			break;
		case Command.SIZE + Value.SIZE:
			raw[0] = this.lead.getHex();
			valueRaw = this.value.getValue();
			
			for (int i = 0; i < Value.SIZE; i++) {
				raw[Command.SIZE + i] = valueRaw[i];
			}
			break;
		case Command.SIZE + Key.SIZE + Value.SIZE:
			raw[0] = this.lead.getHex();
			keyRaw = this.key.getValue();
			valueRaw = this.value.getValue();
			
			for (int i = 0; i < Key.SIZE; i++) {
				raw[Command.SIZE + i] = keyRaw[i];
			}
			for (int i = 0; i < Value.SIZE; i++) {
				raw[Command.SIZE + Key.SIZE + i] = valueRaw[i];
			}
			break;
		default:
			throw new NullPointerException("Message is a strange length.");
		}
		
		return raw;
	}

	public LeadByte getLeadByte() {
		return this.lead;
	}

	public void setLeadByte(LeadByte lead) {
		this.lead = lead;
	}

	public Key getKey() {
		return this.key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setKey(byte[] raw) {
		this.key = new Key(raw);
	}

	public Value getValue() {
		return this.value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public void setValue(byte[] raw) {
		this.value = new Value(raw);
	}
}