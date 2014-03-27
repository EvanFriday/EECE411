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
		this.setLeadByte(null);
		this.key = new Key();
		this.value = new Value();
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
		this.setMessageKey(k);
		this.setMessageValue(v);
	}
	
	public Message(byte[] message) {
		byte[] temp_key = new byte[Key.SIZE];
		byte[] temp_value = new byte[Value.SIZE];
		for(int i = 0; i < Key.SIZE; i++){
			temp_key[i] = message[i+1];
		}
		for(int i = 0; i < Value.SIZE; i++){
			temp_value[i] = message[i+1+Key.SIZE];
		}
		
		/*switch (message.length) {
		case Command.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			break;
		case Command.SIZE + Key.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			this.setMessageKey(new Key(message, Command.SIZE));
			break;
		case Command.SIZE + Value.SIZE:
			this.setLeadByte(Command.getCommand(message[0]));
			this.setMessageValue(new Value(message, Command.SIZE));
			break;
			
		case Command.SIZE + Key.SIZE + Value.SIZE:*/
			this.setLeadByte(Command.getCommand(message[0]));
			this.setMessageKey(temp_key);
			this.setMessageValue(temp_value);
			/*	break;
		default:
			throw new NullPointerException("Message is a strange length.");
		}*/
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
		
		
		reply = Message.getFrom(replyStream);
		try{
		error = reply.getErrorByte();
		}
		catch (NullPointerException e) {
			System.err.println("Error: replystream has no lead byte. NPE");
		}
			if (error != null) {
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
		switch (size) {
		case Command.SIZE:
				raw[0] = this.lead.getHex();
				break;
				
		case Command.SIZE + Key.SIZE:
				raw[0] = this.lead.getHex();
				for (int i = 0; i < Key.SIZE; i++) {
					raw[Command.SIZE + i] = key.getValue(i);
				}
				break;
				
		case Command.SIZE + Value.SIZE:
				raw[0] = this.lead.getHex();
				for (int i = 0; i < Value.SIZE; i++) {
					raw[Command.SIZE + i] = value.getValue(i);
				}
				break;
		case Key.SIZE + Value.SIZE:
				raw[0] = 0x00;
				for (int i = 0; i < Key.SIZE; i++) {
				raw[Command.SIZE + i] = key.getValue(i);
				}
				for (int i = 0; i < Value.SIZE; i++) {
					raw[Command.SIZE + Key.SIZE + i] = value.getValue(i);
				}
		break;
				
		case Command.SIZE + Key.SIZE + Value.SIZE:
				raw[0] = this.lead.getHex();
				for (int i = 0; i < Key.SIZE; i++) {
					raw[Command.SIZE + i] = key.getValue(i);
				}
				for (int i = 0; i < Value.SIZE; i++) {
					raw[Command.SIZE + Key.SIZE + i] = value.getValue(i);
				}
				break;
			
		default:
				throw new NullPointerException("Message is a strange length, size of message ="+size);
	}
		
		return raw;
	}

	public LeadByte getLeadByte() {
		return this.lead;
	}

	public void setLeadByte(LeadByte lead) {
		this.lead = lead;
	}
	
	public ErrorCode getErrorByte(){
		return (ErrorCode) this.lead;
	}

	public Key getMessageKey() {
		Key temp = new Key();
		for(int i = 0; i < Key.SIZE; i++){
			temp.setValue(this.key.getValue(i), i);
		}
		return temp;
	}

	public void setMessageKey(Key key_in) {
		this.key = new Key();
		for(int i = 0; i< Key.SIZE; i++){
			this.key.setValue(key_in.getValue(i), i);
		}
		
	}

	public void setMessageKey(byte[] raw) {
		this.key = new Key();
		for(int i = 0; i < Key.SIZE; i++){
			this.key.setValue(raw[i], i);
		}
	}
	
	public Boolean compareMessageKeys(Key key_in){
		for(int i = 0; i < Key.SIZE; i++){
			if(this.key.getValue(i)!=key_in.getValue(i)){
				return false;
			}
		}
		return true;
		
	}

	public Value getMessageValue() {
		Value temp = new Value();
		for(int i = 0; i < Value.SIZE; i++){
			temp.setValue(this.value.getValue(i), i);
		}
		return temp;
	}

	public void setMessageValue(Value value) {
		this.value = new Value();
		for(int i = 0; i< Value.SIZE; i++){
			this.value.setValue(value.getValue(i), i);
		}
	}

	public void setMessageValue(byte[] raw) {
		this.value = new Value();
		for(int i = 0; i< Value.SIZE; i++){
			this.value.setValue(raw[i], i);
		}
	}
	public Boolean compareMessageValues(Value value_in){
		for(int i = 0; i < Value.SIZE; i++){
			if(this.value.getValue(i)!=value_in.getValue(i)){
				return false;
			}
		}
		return true;
		
	}
}