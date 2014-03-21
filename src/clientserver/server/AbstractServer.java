/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import clientserver.NodeList;
import clientserver.message.Command;
import clientserver.message.ErrorCode;
import clientserver.message.Key;
import clientserver.message.Message;
import clientserver.message.Value;

public abstract class AbstractServer {
	protected ServerSocket socket;
	protected Map<Key, Value> kvStore;
	protected NodeList ipList;
	
	protected AbstractServer(int port) throws IOException {
		this.socket = new ServerSocket(port);
		this.kvStore = new HashMap<Key, Value>();
		this.ipList = new NodeList("localhost");
	}
	
	public void run() {
		Command last = null;
		
		do {
			try {
				last = this.action();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while(last != Command.SHUTDOWN);
	}
	
	protected abstract Command action() throws IOException;

	protected synchronized Message acceptUpdate() throws IOException {
		Socket con = this.socket.accept();
		Message original = Message.getFrom(con);
		
		if (original.getLeadByte() == Command.SHUTDOWN) {
			new Message(ErrorCode.OK).sendTo(con);
			this.shutdown();
		} else {
			Message reply = new Message();
			Key k = original.getKey();
			Value v = original.getValue();
			
			// TODO: Validate keyspace
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
	}
	
	protected abstract void shutdown();
}
