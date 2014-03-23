/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
	
	public static Message getFrom(Socket con) throws Exception {
		return getFrom(con.getInputStream());
	}
	
	public static Message getFrom(InputStream is) throws Exception {
		byte[] raw = new byte[MAX_SIZE];
		is.read(raw, 0, MAX_SIZE);
		return new Message(raw);
	}
	
	public Message sendTo(String address, int port) throws Exception {
		Socket con = new Socket(address, port);
		Message reply = this.sendTo(con);
		
		con.close();
		return reply;
	}
	
	public Message sendTo(Socket con) throws Exception {
		return this.sendTo(con.getOutputStream(), con.getInputStream());
	}
	
	public Message sendTo(OutputStream os, InputStream replyStream) throws Exception {
		Message reply = null;
		ErrorCode error = null;
		os.write(this.getRaw());
		os.flush();
		
		if (replyStream != null) {
			reply = Message.getFrom(replyStream);
			try{
			error = (ErrorCode) reply.getLeadByte();
			}
			catch (NullPointerException e) {
				System.err.println("Error: replystream has no lead byte. NPE");
			}
			switch(error) {
			case OK:
				System.out.println("Operation successful.");
			case KEY_DNE:
				System.out.println("Error: Inexistent key.");
			case OUT_OF_SPACE:
				System.out.println("Error: Out of space.");
			case OVERLOAD:
				System.out.println("Error: System overload.");
			case KVSTORE_FAIL:
				System.out.println("Error: Internal KVStore failure.");
			case BAD_COMMAND:
				System.out.println("Error: Unrecognized command.");
			default:
				System.out.println("Error: Unknown error.");
			}
		}
		
		return reply;
	}
	public void sendReplyTo(OutputStream os) throws Exception {
		os.write(this.getRaw());
		os.flush();
	}
	
	private byte[] getRaw() {
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