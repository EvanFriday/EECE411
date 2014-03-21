/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import clientserver.message.Command;
import clientserver.message.ErrorCode;
import clientserver.message.Key;
import clientserver.message.Message;
import clientserver.message.Value;

public class Server implements Remote {
	private ServerSocket socket;
	private int port = 9999;
	private Map<Key, Value> kvStore;
	private String PublicIP;
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
		this.kvStore = new HashMap<Key, Value>();
		this.PublicIP = IpTools.getHostnameFromIp(IpTools.getIp());
	}
	
	public synchronized void acceptUpdate() {
		try {
			//Accept incoming connections
			Socket con = this.socket.accept();
			//Incoming message from client
			Message original = Message.getFrom(con);
			//Reply message to send to client
			Message reply = new Message();
			
			//Get Command, Key and Value from Message
			Key k = original.getKey();
			Value v = original.getValue();
			Command c = (Command) original.getLeadByte();
			
			//Create list of nodes responsible for this key
			List<String> nodeList = getIpListForKeySpace(k);
			//Is this node responsible for this key?
			Boolean in_local = nodeList.contains(this.PublicIP);
			//If this node is responsible, and it is a get, and we successfully get it?
			Boolean in_local_and_get_ok = false;
			
			
			//Create list of replies from the 9/10 propagations
			Map<String,Message> nodeReplies = new HashMap<String,Message>();
			//List<Message> nodeReplies = new ArrayList<Message>();
			
			
			
			if (in_local) {
				//As we are handling this node locally, remove it from propagation list
				nodeList.remove(this.PublicIP);
				switch(c){
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
						in_local_and_get_ok = true;
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
			} else {
				// Send it along to proper nodes in new thread!
				for(String nodeAddress : nodeList){
				Propagate p = new Propagate("Propagation Thread",this,nodeAddress,original);
				//nodeReplies holds all of the replies
				nodeReplies.put(nodeAddress, p.propagate());
				}
				
				
				//If we call a get, and it is locally stored and found, we don't need to process replies from other nodes
				if(!in_local_and_get_ok){
					//TODO: Handle replies from nodes
					for(Entry<String,Message> nodeReply : nodeReplies.entrySet() ){
						Message message = nodeReply.getValue();
						String address = nodeReply.getKey();
						ErrorCode e = (ErrorCode) message.getLeadByte();
						
						switch(e) {
						case OK:
							System.out.println("Operation successful at: " + address);
						case KEY_DNE:
							System.out.println("Error: Inexistent key at: " + address);
						case OUT_OF_SPACE:
							System.out.println("Error: Out of space at: " + address);
						case OVERLOAD:
							System.out.println("Error: System overload at: " + address);
						case KVSTORE_FAIL:
							System.out.println("Error: Internal KVStore failure at: " + address);
						case BAD_COMMAND:
							System.out.println("Error: Unrecognized command at: " + address);
						default:
							System.out.println("Error: Unknown error at: " + address);
							}
						}
						
					}
					
					
					
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* Need to totally re-create to push to nodes in keyspaces.
	 * 
	 */
	
	// Sends m to the necessary 9 or 10 nodes
	public Message propagateMessage(Message m,String address) throws Exception {
		//Nodes replies
		Message nodeReply; 

		
			nodeReply=(m.sendTo(address, this.port));
		
		return nodeReply;
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
	
	//Returns the appropriate list of IP's for a given keyspace
	public List<String> getIpListForKeySpace(Key k) {

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
}