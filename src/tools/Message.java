package tools;

import java.io.IOException;
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
		this.key = null;
		this.value = null;
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
		this();
		this.setLeadByte(l);
		this.key = k;
		this.value = v;
	}
	public Message(EVpair evpair){
		this(evpair.getError(),evpair.getValue());	
	}
	
	public Message(byte[] message) {
		boolean debug = false;
		switch (message.length) {
		case Command.SIZE:
			if(Command.getCommand(message[0]) != null)
				this.setLeadByte(Command.getCommand(message[0]));
			else if(ErrorCode.getErrorCode(message[0]) != null)
				this.setLeadByte(ErrorCode.getErrorCode(message[0]));
			break;
		case Command.SIZE + Key.SIZE:
			if(Command.getCommand(message[0]) != null)
				this.setLeadByte(Command.getCommand(message[0]));
			else if(ErrorCode.getErrorCode(message[0]) != null)
				this.setLeadByte(ErrorCode.getErrorCode(message[0]));
			this.setMessageKey(new Key(message, Command.SIZE));
			break;
		case Command.SIZE + Value.SIZE:
			if(Command.getCommand(message[0]) != null)
				this.setLeadByte(Command.getCommand(message[0]));
			else if(ErrorCode.getErrorCode(message[0]) != null)
				this.setLeadByte(ErrorCode.getErrorCode(message[0]));
			this.setMessageValue(new Value(message, Command.SIZE));
			break;		
		case Command.SIZE + Key.SIZE + Value.SIZE:
			if(Command.getCommand(message[0]) != null)
				this.setLeadByte(Command.getCommand(message[0]));
			else if(ErrorCode.getErrorCode(message[0]) != null)
				this.setLeadByte(ErrorCode.getErrorCode(message[0]));
			this.setMessageKey(new Key(message,Command.SIZE));
			this.setMessageValue(new Value(message,Command.SIZE+Key.SIZE));
				break;
		default:
			throw new NullPointerException("Message is a strange length.");
		}
	}

	public void getFrom(Socket con) throws IOException {
		this.getFrom(con.getInputStream());
	}

	public void getFrom(InputStream is) throws IOException {
		boolean debug = true;
		if(debug) System.out.println("[debug] Entering getFrom");
		byte[] command = new byte[1];
		byte[] key = new byte[32];
		byte[] value = new byte[1024];
		is.read(command, 0, 1);
		this.setLeadByte(Command.getCommand(command[0]));
		if(debug) System.out.println("[debug] getFrom: Done reading Command");
		if(this.lead==Command.PUT || this.lead==Command.GET || this.lead==Command.REMOVE) {
			is.read(key,0,32);
			this.setFullMessageKey(new Key(key));
			if(debug) System.out.println("[debug] getFrom: Done reading Key");
		}
		if(this.lead==Command.PUT){
			is.read(value,0,1024);
			this.setFullMessageValue(new Value(value));
			if(debug) System.out.println("[debug] getFrom: Done reading Value");
		}
		if(debug) System.out.println("[debug] Leaving getFrom");
	}

	public Message sendTo(String address, int port) throws IOException {
		Socket con = new Socket(address, port);
		Message reply = this.sendTo(con);

		con.close();
		return reply;
	}

	public Message sendTo(Socket con) throws IOException {
		return this.sendTo(con.getOutputStream(), con.getInputStream());
	}

	public Message sendTo(OutputStream os, InputStream replyStream) throws IOException {

		ErrorCode error = null;
		byte[] b = new byte[1+32+1024];
		boolean debug = false;
		if(debug) Tools.print("[debug] sendTo: About to write to OS");
		os.write(this.getRaw());
		//os.flush();
		if(debug) Tools.print("[debug] sendTo: Done writing to OS, about to getFrom IS");
		
		try {
			//this.sendReplyTo(os);
		} catch (Exception e1) {
			Tools.print("failed to send message");
		}

		int IS_read_length = replyStream.read(b);
		byte[] bb = new byte[IS_read_length];
		// Copy b into new byte array of proper length
		for(int i=0; i<IS_read_length; i++) {
			bb[i] = b[i];
		}
		if(debug) { 
			Tools.print("[debug] sendTo: Bytestream read from IS:");
			Tools.printByte(bb); 
		}
		Message reply = new Message(bb);
		//reply.getFrom(replyStream);
		try{
		error = reply.getErrorByte();
		}
		catch (NullPointerException e) {
			System.err.println("SERVER: "+"Error: replystream has no lead byte. NPE");
		}
			if (error != null) {
				if(debug) Tools.print("[debug] sendTo: ErrorCode != Null");
			switch(error) {
			case OK:
				System.out.println("SERVER: "+"Operation successful."); break;
			case KEY_DNE:
				System.out.println("SERVER: "+"Error: Inexistent key."); break;
			case OUT_OF_SPACE:
				System.out.println("SERVER: "+"Error: Out of space."); break;
			case OVERLOAD:
				System.out.println("SERVER: "+"Error: System overload."); break;
			case KVSTORE_FAIL:
				System.out.println("SERVER: "+"Error: Internal KVStore failure."); break;
			case BAD_COMMAND:
				System.out.println("SERVER: "+"Error: Unrecognized command."); break;
			default:
				System.out.println("SERVER: "+"Error: Unknown error."); break;
			}
		}
			if(debug) {
			Tools.print("[debug] sendTo: Returning Reply:");
			Tools.print(reply.lead);
			if(this.lead == Command.GET) Tools.printByte(reply.getFullMessageValue().value);
			}

		return reply;
	}
	public void sendReplyTo(Socket con) throws Exception {
		sendReplyTo(con.getOutputStream());
	}

	public void sendReplyTo(OutputStream os) throws Exception {
		os.write(this.getRaw());
	}

	public byte[] getRaw() {
		int size = 0;
		size += (this.lead != null ? Command.SIZE : 0);
		size += (this.key != null ? Key.SIZE : 0);
		size += (this.value != null ? Value.SIZE : 0);
		byte[] raw = new byte[size];
		switch (size) {
		case Command.SIZE:
				raw[0] = this.lead.getByte();
				break;

		case Command.SIZE + Key.SIZE:
				raw[0] = this.lead.getByte();
				for (int i = 0; i < Key.SIZE; i++) {
					raw[Command.SIZE + i] = this.key.key[i];
				}
				break;

		case Command.SIZE + Value.SIZE:
				raw[0] = this.lead.getByte();
				for (int i = 0; i < Value.SIZE; i++) {
					raw[Command.SIZE + i] = this.value.value[i];
				}
				break;
		case Key.SIZE + Value.SIZE:
				raw[0] = 0x00;
				for(int i = 0; i < Key.SIZE; i++){
				raw[Command.SIZE + i] = this.key.key[i];
				}
				for (int i = 0; i < Value.SIZE; i++) {
					raw[Command.SIZE + Key.SIZE + i] = this.value.value[i];
				}
				break;

		case Command.SIZE + Key.SIZE + Value.SIZE:
				if(this.lead != null) raw[0] = this.lead.getByte();
				else raw[0] = 0x00;
				for (int i = 0; i < Key.SIZE; i++) {
					raw[Command.SIZE + i] = this.key.key[i];
				}
				for (int i = 0; i < Value.SIZE; i++) {
					raw[Command.SIZE + Key.SIZE + i] = this.value.value[i];
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
		if(this.lead == Command.PUT)
			return ErrorCode.KEY_DNE;
		if(this.lead == Command.GET)
			return ErrorCode.OUT_OF_SPACE;
		if(this.lead == Command.REMOVE)
			return ErrorCode.OVERLOAD;
		if(this.lead == Command.SHUTDOWN)
			return ErrorCode.KVSTORE_FAIL;
		return (ErrorCode) this.lead;
	}
	
	public void setEVpair(EVpair reply){
		this.lead=reply.getError();
		this.value=reply.getValue();
	}

	public Key getMessageKey() {
		Key temp = new Key();
		for(int i = 0; i < Key.SIZE; i++){
			temp.setValue(this.key.key[i], i);
		}
		return temp;
	}
	
	public Key getFullMessageKey() {
		return this.key;
	}

	public void setMessageKey(Key key_in) {
		this.key = new Key();
		for(int i = 0; i< Key.SIZE; i++){
			this.key.setValue(key_in.key[i], i);
		}

	}
	
	public void setFullMessageKey(Key key_in) {
		this.key = key_in;
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
		if(this.value != null){
			Value temp = new Value();
			for(int i = 0; i < Value.SIZE; i++){
				temp.setValue(this.value.getValue(i), i);
			}
			return temp;
		}
		else
			return null;
		
	}
	
	public Value getFullMessageValue() {
		return this.value;
	}

	public void setMessageValue(Value value) {
		this.value = new Value();
		for(int i = 0; i< Value.SIZE; i++){
			this.value.setValue(value.getValue(i), i);
		}
	}
	
	public void setFullMessageValue(Value v) {
		this.value = v;
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
