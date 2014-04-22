package main.message;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;


/**
 * Object representing the replies sent between nodes. This consists of a lead
 * byte, which represents the action to be taken, a key mapping to a value, and
 * for the <i>put</i> operation, a value to be stored in the system.<br>
 * 
 * This object can also be used for replies, with the lead byte now representing
 * an error code, and for the <i>get</i> operation, the value being returned.
 * The key field is no longer needed.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public class Reply extends Message {
	/**
	 * Creates a new reply from the raw reply data.
	 * 
	 * @param raw
	 *            The raw message data.
	 */
	public Reply(byte[] raw) {
		super(raw);
		this.setLeadByte(ErrorCode.getErrorCode(raw[0]));
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
	public static Reply getReplyFrom(Socket con) throws IOException {
		return getReplyFrom(con.getInputStream());
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
	public static Reply getReplyFrom(InputStream is) throws IOException {
		byte[] raw = new byte[MAX_SIZE];
		int read = is.read(raw, 0, MAX_SIZE);
		return new Reply(Arrays.copyOf(raw, read));
	}
}