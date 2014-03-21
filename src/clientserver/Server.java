/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import clientserver.message.Command;
import clientserver.message.ErrorCode;
import clientserver.message.Key;
import clientserver.message.Message;
import clientserver.message.Value;

public class Server implements Remote {
	private ServerSocket socket;
	private int port = 9999;
	private Map<Key, Value> kvStore;
	
	// I still think we can do this better
	private List<String> set_one;
	private List<String> set_two;
	private List<String> set_three;
	private List<String> set_four;
	private List<String> set_five;
	private List<String> set_six;
	private List<String> set_seven;
	private List<String> set_eight;

	public Server(int port) throws Exception {
		this.port = port;
		this.socket = new ServerSocket(port);
		this.kvStore = new ConcurrentHashMap<Key, Value>();
	}
	
	public synchronized void acceptUpdate() {
		try {
			Socket con = this.socket.accept();
			Message original = Message.getFrom(con);
			Message reply = new Message();
			Key k = original.getKey();
			Value v = original.getValue();
			
			/* Logic to check if stored locally, or on other node sets.
			 * 
			 * PUT: check if the value in dirtyPut is within this node's keyspace,
			 * 		
			 * 		If it is	-- put in this node, and other 9 nodes in keyspace
			 * 		If it is not-- propagate the put to the ten nodes that have it
			 * 
		 	 * GET: check if the value in dirtyGet is within this node's keyspace.
			 * 
			 *  	If it is 	-- return the value
			 * 		If it is not-- query a node who is within the keyspace for this key
			 * 
			 * REMOVE: check if the value in dirtyRemove is within this node's keyspace, if it is remove it from this node and other nodes in keyspace. If not, forward
			 * 		
			 * 		If it is	-- remove the value locally, and on other 9 nodes
			 * 		If it is not-- call remove on 10 nodes in the proper keyspace
			 */
			
//			if (inThisKeySpace) {
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
//			} else {
//				// Send it along
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* Need to totally re-create to push to nodes in keyspaces.
	 * 
	 */
	
	// Sends m to the necessary 9 or 10 nodes
	private void propagateMessage(Message m) throws Exception {
		int key_space_division_value = getFirstThreeBits(m.getKey().getValue()[0]);
		
		// Does nobody else see how we could make this easier for us?
		switch(key_space_division_value) {
		case 1:
			for(int i=0; i<set_one.size(); i++) {
				m.sendTo(set_one.get(i), this.port);
			}
			break;
		case 2:
			for(int i=0; i<set_two.size(); i++) {
				m.sendTo(set_two.get(i), this.port);
			}
			break;
		case 3:
			for(int i=0; i<set_three.size(); i++) {
				m.sendTo(set_three.get(i), this.port);
			}
			break;
		case 4:
			for(int i=0; i<set_four.size(); i++) {
				m.sendTo(set_four.get(i), this.port);
			}
			break;
		case 5:
			for(int i=0; i<set_five.size(); i++) {
				m.sendTo(set_five.get(i), this.port);
			}
			break;
		case 6:
			for(int i=0; i<set_six.size(); i++) {
				m.sendTo(set_six.get(i), this.port);
			}
			break;
		case 7:
			for(int i=0; i<set_seven.size(); i++) {
				m.sendTo(set_seven.get(i), this.port);
			}
			break;
		case 8:
			for(int i=0; i<set_eight.size(); i++) {
				m.sendTo(set_eight.get(i), this.port);
			}
			break;
		default:
			
		}
		
	}
	
	public void fileRead(String file_location) throws Exception{
		FileReader file = new FileReader(file_location);
		BufferedReader in = new BufferedReader(file);
		for(int i=1;i<=8;i++)

			for(int j=1;j<=10;j++){
				switch(i){	
				case 1:
					set_one.add(in.readLine());
					break;
				case 2:
					set_two.add(in.readLine());
					break;
				case 3:
					set_three.add(in.readLine());
					break;
				case 4:
					set_four.add(in.readLine());
					break;
				case 5:
					set_five.add(in.readLine());
					break;
				case 6:
					set_six.add(in.readLine());
					break;
				case 7:
					set_seven.add(in.readLine());
					break;
				case 8:
					set_eight.add(in.readLine());
					break;
				default:
					System.err.println("oops, your IP list has too many lines!");
					break;
				}
			}
		file.close();
	}
	
	public int getFirstThreeBits(byte byte_in)
	{
		int ret=0;
		byte temp = byte_in;
		byte temp2;
		temp2 = (byte) (temp | (1 << 8));
		if (temp2==1)
		{
			ret += 4;
		}
		temp2 = (byte) (temp | (1 << 7));
		if (temp2==1)
		{
			ret += 2;
		}
		temp2 = (byte) (temp | (1 << 6));
		if (temp2==1)
		{
			ret += 1;
		}
		
		return ret + 1;
	}
	
	public List<String> getIPs(Key k) {
		int key_space_division_value = this.getFirstThreeBits(k.getValue()[0]);
		switch(key_space_division_value) {
		case 1:
			return this.set_one;
		case 2:
			return this.set_two;
		case 3:
			return this.set_three;
		case 4:
			return this.set_four;
		case 5:
			return this.set_five;
		case 6:
			return this.set_six;
		case 7:
			return this.set_seven;
		case 8:
			return this.set_eight;
		default:
			return this.set_one;
		}
	}
	
	public void selectAddresses(){
		//TODO: Randomly select a number of items from addressList and populate propagateAddressList with them
	}
}