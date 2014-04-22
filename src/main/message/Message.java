package main.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Object representing the messages sent between nodes. This consists of a lead
 * byte, which represents the action to be taken, a key mapping to a value, and
 * for the <i>put</i> operation, a value to be stored in the system.<br>
 * 
 * This object can also be used for replies, with the lead byte now representing
 * an error code, and for the <i>get</i> operation, the value being returned.
 * The key field is no longer needed.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public class Message {
	protected static final int MAX_SIZE = Command.SIZE + Key.SIZE + Value.SIZE;
	
	private LeadByte lead;
	private Key key;
	private Value value;
	
	/**
	 * Creates a new message from its constituent parts.
	 * 
	 * @param l
	 *            The lead byte of the message.
	 */
	public Message(LeadByte l) {
		this(l, null, null);
	}

	/**
	 * Creates a new message from its constituent parts.
	 * 
	 * @param l
	 *            The lead byte of the message.
	 * @param k
	 *            The key to be used.
	 * @param v
	 *            The value to be used.
	 */
	public Message(LeadByte l, Key k) {
		this(l, k, null);
	}

	/**
	 * Creates a new message from its constituent parts.
	 * 
	 * @param l
	 *            The lead byte of the message.
	 * @param k
	 *            The key to be used.
	 * @param v
	 *            The value to be used.
	 */
	public Message(LeadByte l, Value v) {
		this(l, null, v);
	}

	/**
	 * Creates a new message from its constituent parts.
	 * 
	 * @param l
	 *            The lead byte of the message.
	 * @param k
	 *            The key to be used.
	 * @param v
	 *            The value to be used.
	 */
	public Message(LeadByte l, Key k, Value v) {
		this.setLeadByte(l);
		this.setKey(k);
		this.setValue(v);
	}

	/**
	 * Creates a new message from the raw message data.
	 * 
	 * @param raw
	 *            The raw message data.
	 */
	public Message(byte[] raw) {
		switch (raw.length) {
		case Command.SIZE:
			this.setLeadByte(Command.getCommand(raw[0]));
			break;
		case Command.SIZE + Key.SIZE:
			this.setLeadByte(Command.getCommand(raw[0]));
			this.setKey(new Key(raw, Command.SIZE));
			break;
		case Command.SIZE + Value.SIZE:
			this.setLeadByte(Command.getCommand(raw[0]));
			this.setValue(new Value(raw, Command.SIZE));
			break;
		case Command.SIZE + Key.SIZE + Value.SIZE:
			this.setLeadByte(Command.getCommand(raw[0]));
			this.setKey(new Key(raw, Command.SIZE));
			this.setValue(new Value(raw, Command.SIZE + Key.SIZE));
			break;
		default:
			throw new RuntimeException("Message is a strange length.");
		}
	}

	/**
	 * Retrieve a Message from a source.
	 * 
	 * @param con
	 *            The source of the message.
	 * @return The Message received on the source given.
	 * @exception IOException
	 *                If the first byte cannot be read for any reason other than
	 *                end of file, or if the input stream has been closed, or if
	 *                some other I/O error occurs.
	 * @exception NullPointerException
	 *                If <code>b</code> is <code>null</code>.
	 * @exception IndexOutOfBoundsException
	 *                If <code>off</code> is negative, <code>len</code> is
	 *                negative, or <code>len</code> is greater than
	 *                <code>b.length - off</code>
	 * @see java.io.InputStream#read()
	 */
	public static Message getFrom(Socket con) throws IOException {
		return getFrom(con.getInputStream());
	}
	
	/**
	 * Retrieve a Message from a source.
	 * 
	 * @param is
	 *            The source of the message.
	 * @return The Message received on the source given.
	 * @exception IOException
	 *                If the first byte cannot be read for any reason other than
	 *                end of file, or if the input stream has been closed, or if
	 *                some other I/O error occurs.
	 * @exception NullPointerException
	 *                If <code>b</code> is <code>null</code>.
	 * @exception IndexOutOfBoundsException
	 *                If <code>off</code> is negative, <code>len</code> is
	 *                negative, or <code>len</code> is greater than
	 *                <code>b.length - off</code>
	 * @see java.io.InputStream#read()
	 */
	public static Message getFrom(InputStream is) throws IOException {
		byte[] raw = new byte[MAX_SIZE];
		int read = is.read(raw, 0, MAX_SIZE);
		return new Message(Arrays.copyOf(raw, read));
	}
	
	/**
	 * Send this message to another node.
	 * 
	 * @param address
	 *            The address of the node.
	 * @param port
	 *            The port number of the node.
	 * @exception UnknownHostException
	 *                if the IP address of the host could not be determined.
	 * @exception IOException
	 *                if an I/O error occurs when creating the socket.
	 * @exception SecurityException
	 *                if a security manager exists and its
	 *                <code>checkConnect</code> method doesn't allow the
	 *                operation.
	 * @exception IllegalArgumentException
	 *                if the port parameter is outside the specified range of
	 *                valid port values, which is between 0 and 65535,
	 *                inclusive.
	 */
	public void sendTo(String address, int port) throws IOException {
		Socket con = new Socket(address, port);
		this.sendTo(con);
		con.close();
	}
	
	/**
	 * Send this message to another node.
	 * 
	 * @param con
	 *            A Socket to another Node.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public void sendTo(Socket con) throws IOException {
		this.sendTo(con.getOutputStream());
	}
	
	/**
	 * Send this message to another node.
	 * 
	 * @param os
	 *            The OutputStream to another Node.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public void sendTo(OutputStream os) throws IOException {
		os.write(this.getRaw());
		os.flush();
	}

	/**
	 * Converts this Message into a byte array to be sent.
	 * 
	 * @return The contents of the Message in the wire format.
	 */
	private byte[] getRaw() {
		int size = 0;
		size += (this.lead != null ? Command.SIZE : 0);
		size += (this.key != null ? Key.SIZE : 0);
		size += (this.value != null ? Value.SIZE : 0);
		byte[] raw = new byte[size];
		
		switch (size) {
		case Command.SIZE:
			raw[0] = this.lead.getHexValue();
			break;
		case Command.SIZE + Key.SIZE:
			raw[0] = this.lead.getHexValue();
			for (int i = 0; i < Key.SIZE; i++) {
				raw[Command.SIZE + i] = this.key.getHexValue()[i];
			}
			break;
		case Command.SIZE + Value.SIZE:
			raw[0] = this.lead.getHexValue();
			for (int i = 0; i < Value.SIZE; i++) {
				raw[Command.SIZE + i] = this.value.getHexValue()[i];
			}
			break;
		case Command.SIZE + Key.SIZE + Value.SIZE:
			raw[0] = this.lead.getHexValue();
			for (int i = 0; i < Key.SIZE; i++) {
				raw[Command.SIZE + i] = this.key.getHexValue()[i];
			}
			
			for (int i = 0; i < Value.SIZE; i++) {
				raw[Command.SIZE + Key.SIZE + i] = this.value.getHexValue()[i];
			}
			break;
		default:
			throw new RuntimeException("Message is a strange length; Size = " + size);
		}
		
		return raw;
	}
	
	/**
	 * Indicates whether some other message is "equal to" this one.
	 * 
	 * @param m
	 *            The reference message with which to compare.
	 * @return {@code true} if this Message is the same as the m argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Message m) {
		return Arrays.equals(this.getRaw(), m.getRaw());
	}

	/**
	 * @return The LeadByte of the Message.
	 */
	public LeadByte getLeadByte() {
		return lead;
	}

	/**
	 * @param lead
	 *            The LeadByte to put in the Message.
	 */
	public void setLeadByte(LeadByte lead) {
		this.lead = lead;
	}

	/**
	 * @return The Key in the Message.
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key
	 *            The Key to put in the Message.
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return The Value in the Message.
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The Value to put in the Message.
	 */
	public void setValue(Value value) {
		this.value = value;
	}
}