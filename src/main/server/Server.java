package main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import main.message.Command;
import main.message.ErrorCode;
import main.message.Key;
import main.message.Message;
import main.message.Value;

/**
 * Server object to handle connections and updates from other nodes.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public class Server {
	private ServerSocket socket;
	private Map<Key, Value> kvStore;
	
	/**
	 * Initializes the server.
	 * 
	 * @param port
	 *            The port number to bind to.
	 * @exception IOException
	 *                if an I/O error occurs when opening the socket.
	 * @exception SecurityException
	 *                if a security manager exists and its
	 *                <code>checkListen</code> method doesn't allow the
	 *                operation.
	 * @exception IllegalArgumentException
	 *                if the port parameter is outside the specified range of
	 *                valid port values, which is between 0 and 65535,
	 *                inclusive.
	 */
	public Server(int port) throws IOException {
		System.out.println("Starting server...");
		this.socket = new ServerSocket(port);
		this.kvStore = new KeyValueMap();
	}
	
	/**
	 * Gets the server to start accepting updates.
	 */
	public void run() {
		Command last = null;
		System.out.println("Server Running.");
		
		do {
			try {
				last = this.action();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while(last != Command.SHUTDOWN);
	}
	
	/**
	 * The action to be looped while the server is running.
	 * 
	 * @return The command that has just finished processing.
	 * @exception IOException
	 *                if an I/O error occurs in one of many places.
	 */
	private Command action() throws IOException {
		return (Command) this.acceptUpdate().getLeadByte();
	}

	/**
	 * Accepts a message and takes the appropriate action.
	 * 
	 * @return The message just received.
	 * @exception IOException
	 *                if an I/O error occurs in one of many places.
	 */
	private Message acceptUpdate() throws IOException  {
		// Accept connections
		Socket con = this.socket.accept();
		try {
			// Get incoming message
			Message original = Message.getFrom(con);
			Message reply = new Message(ErrorCode.OK);
			
			if (original.getLeadByte() == Command.SHUTDOWN) {
				reply.setLeadByte(ErrorCode.OK);
				reply.sendTo(con);
				this.shutdown();
				System.out.println("Server terminated.");
			} else {
				Key k = original.getKey();
				Value v = original.getValue();
				
				switch((Command) original.getLeadByte()){
				case PUT:
					if (kvStore.size() < Key.MAX_NUM) {
						kvStore.put(k, v);
						reply.setLeadByte(ErrorCode.OK);
					} else {
						reply.setLeadByte(ErrorCode.OUT_OF_SPACE);
					}
					break;
				case GET:
					if (kvStore.containsKey(k)) {
						reply.setValue(kvStore.get(k));
						reply.setLeadByte(ErrorCode.OK);
					} else {
						reply.setLeadByte(ErrorCode.KEY_DNE);
					}
					break;
				case REMOVE:
					if (kvStore.containsKey(k)) {
						kvStore.remove(k);
						reply.setLeadByte(ErrorCode.OK);
					} else {
						reply.setLeadByte(ErrorCode.KEY_DNE);
					}
					break;
				default:
					reply.setLeadByte(ErrorCode.BAD_COMMAND);
					break;
				}
				
				reply.sendTo(con);
			}
			
			return original;
		} finally {
			con.close();
		}
	}
	
	/**
	 * Cleans up after the SHUTDOWN command is given.
	 * 
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	private void shutdown() throws IOException {
		this.socket.close();
	}
}