/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import clientserver.message.Command;
import clientserver.message.ErrorCode;
import clientserver.message.Key;
import clientserver.message.LeadByte;
//import clientserver.message.Message;
import clientserver.message.Value;

public class Server implements Remote {
	private ServerSocket socket;
	private int port = 5050;
	
	public static final int MAX_NUM_KEYS = 40000;
	public static final int PUT = 0x01;
	public static final int GET = 0x02;
	public static final int REMOVE = 0x03;
	public static final int SHUTDOWN = 0x04;
	public static final int PROP_PUT = 0x21;
	public static final int PROP_GET = 0x22;
	public static final int PROP_REMOVE = 0x23;
	public static final int PROP_SHUTDOWN = 0x24;
	public static final int OK = 0x00;
	public static final int DNE = 0x01;
	public static final int OOS = 0x02;
	public static final int OL = 0x03;
	public static final int FAIL = 0x04;
	public static final int BADC = 0x05;

	private ArrayList<KeyValuePair> kvStore;

	//private Map<Key, Value> kvStore;
	//private ArrayList<Message> kvStore;
	private String PublicIP;
	private Boolean shutdown;
	private Boolean debug_mode = true;
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

//		this.kvStore = new ConcurrentHashMap<Key, Value>();
		this.kvStore = new ArrayList<KeyValuePair>();
		this.PublicIP = IpTools.getHostnameFromIp(IpTools.getIp());
		this.shutdown = false;
		this.set_one = new ArrayList<String>();
		this.set_two = new ArrayList<String>();
		this.set_three = new ArrayList<String>();
		this.set_four = new ArrayList<String>();
		this.set_five = new ArrayList<String>();
		this.set_six = new ArrayList<String>();
		this.set_seven = new ArrayList<String>();
		this.set_eight = new ArrayList<String>();
	}
	
	public void acceptUpdate() {
		try {
			//Accept incoming connections
			Socket con = this.socket.accept();
			InputStream in = con.getInputStream();
			OutputStream out = con.getOutputStream();
			//Incoming message from client
			byte[] original = new byte[1 + KeyValuePair.KEY_SIZE + KeyValuePair.VALUE_SIZE];
			byte[] reply = new byte[1];
			byte[] reply_put = new byte[1 + KeyValuePair.VALUE_SIZE];
			
			in.read(original);		

			//Get Command, Key and Value from Message
			byte c = original[0];
			byte[] k = new byte[KeyValuePair.KEY_SIZE];
			byte[] v = new byte[KeyValuePair.VALUE_SIZE];
			
			// Store in k and v
			if(c != SHUTDOWN && c != PROP_SHUTDOWN) {
				for( int i=0; i<KeyValuePair.KEY_SIZE; i++) {
					k[i] = original[i+1];
				}
				if(c == PUT) {
					for(int j=0; j<KeyValuePair.VALUE_SIZE; j++) {
						v[j] = original[j+1+KeyValuePair.KEY_SIZE];
					}
				}
			}
			
			/*
			if(this.debug_mode){
			for(int i=0; i<this.kvStore.size(); i++){
				System.err.println("message: " + i + " out of: " + kvStore.size());
					for(int j = 0; j< Key.SIZE; j++){
						System.err.print(m.getMessageKey().getValue(i));
						}
						System.err.print("\n");
						for(int k = 0; k< Value.SIZE; k++){
							System.err.print(m.getMessageValue().getValue(i));
						}
						System.err.print("\n");									
				}
			}
			*/
			String remoteAddress = con.getRemoteSocketAddress().toString();
			switch(c){
			case PUT: System.out.println("Receiving PUT command from: " + remoteAddress);
				break;
			case GET:	System.out.println("Receiving GET command from: " + remoteAddress);
				break;
			case REMOVE: System.out.println("Receiving REMOVE command from: " + remoteAddress);
				break;
			case PROP_PUT: System.out.println("Receiving PROP_PUT command from: " + remoteAddress);
				break;
			case PROP_GET: System.out.println("Receiving PROP_GET command from: " + remoteAddress);
				break;
			case PROP_REMOVE: System.out.println("Receiving PROP_REMOVE command from: " + remoteAddress);
				break;
			default:
				break;
			}
			
		
			//Create list of nodes responsible for this key
			List<String> nodeList = new ArrayList<String>();
			//Is this node responsible for this key?
			Boolean in_local;
			if(this.debug_mode){
				nodeList.add("planetlab2.cs.ubc.ca");
				nodeList.add("pl002.ece.upatras.gr");
				nodeList.add("gschembra3.diit.unict.it");
				nodeList.add("pl1.cis.uab.edu");
				nodeList.add("pl2.rcc.uottawa.ca");
				in_local = true;
			}
			else{
				//nodeList = getIpListForKeySpace(k);
				in_local = nodeList.contains(this.PublicIP);
			}	
			//If this node is responsible, and it is a get, and we successfully get it?
			Boolean in_local_and_get = false;
			
			//Is this a propagated message?
			Boolean is_a_propagation = false;
			

			//Are we shutting down?
			if(c==SHUTDOWN){
				this.setShutdownStatus(true);
				reply[0] = OK;
			}
			else{
				
				if (in_local) {
					//As we are handling this node locally, remove it from propagation list
					nodeList.remove(this.PublicIP);
					switch(c){
						case PUT:
							System.out.println("Handing PUT command locally");
							is_a_propagation =false;
							if (this.kvStore.size() < MAX_NUM_KEYS) {
								reply[0] = OK;
								this.kvStore.add(new KeyValuePair(k, v)); // Add KVP to the local store
								if(debug_mode) System.out.println("Handing PUT command locally");
							}
						
							else {
								reply[0] = OOS;
							}
							break;
						case GET:
							boolean match_found;
							KeyValuePair kvp;
							if(debug_mode) System.out.println("Handing GET command locally");
							in_local_and_get = true;
							is_a_propagation = false;
							
							for(int i=0; i<this.kvStore.size(); i++) {
								kvp = this.kvStore.get(i);
								match_found = true;
								for(int j=0; j<KeyValuePair.KEY_SIZE; j++) {
									if(kvp.getKey(j) != k[j]) { // Mismatch
										match_found = false;
										break;
									}
								}
								if(match_found) {
									reply[0] = OK;
									for(int j=0; j<KeyValuePair.VALUE_SIZE; j++) {
										reply[j+1] = kvp.getValue(j);
									}
									break;
								}
								else {
									reply[0] = DNE;
								}
							}
							break;
						case REMOVE:
							if(debug_mode) System.out.println("Handing REMOVE command locally");
							is_a_propagation =false;
							for(int i=0; i<this.kvStore.size(); i++) {
								kvp = this.kvStore.get(i);
								match_found = true;
								for(int j=0; j<KeyValuePair.KEY_SIZE; j++) {
									if(kvp.getKey(j) != k[j]) { // Mismatch
										match_found = false;
										break;
									}
								}
								if(match_found) {
									this.kvStore.remove(i);
									break;
								}
							}
							if(match_found == false) {
								reply[0] = DNE;
							}
							else {
								reply[0] = OK;
							}
							break;
						case PROP_PUT:
							
							if (this.kvStore.size() < Key.MAX_NUM) {
								this.kvStore.add(kk, vv);
								reply.setLeadByte(ErrorCode.OK);
							} 
							else {
								reply.setLeadByte(ErrorCode.OUT_OF_SPACE);
							}
							is_a_propagation = true;
							break;
						case PROP_GET:
							in_local_and_get = true;
							if (this.kvStore.containsKey(kk)) {
								reply.setValue(new Value(this.kvStore.get(kk).getRaw()));
							if (kvStore.containsKey(k)) {
								reply.setMessageValue(kvStore.get(k));
								reply.setLeadByte(ErrorCode.OK);
								
							} 
							else {
								reply.setLeadByte(ErrorCode.KEY_DNE);
							}
							is_a_propagation = true;
							break;
						case PROP_REMOVE:
							if (this.kvStore.containsKey(kk)) {
								this.kvStore.remove(kk);
								reply.setLeadByte(ErrorCode.OK);
							} 
							else {
								reply.setLeadByte(ErrorCode.KEY_DNE);
							}
							is_a_propagation = true;
							break;
						default:
							reply.setLeadByte(ErrorCode.BAD_COMMAND);
							break;
						}
					
				}
				if(!in_local_and_get){
					if (!is_a_propagation){
					
						//Create list of replies from the 9/10 propagations
						Map<String,Message> nodeReplies = new ConcurrentHashMap<String,Message>();
						
							//Change Command to send to have PROP status.
							switch(c){
								case PUT: original.setLeadByte(Command.PROP_PUT);
									break;
								case GET: original.setLeadByte(Command.PROP_GET);
									break;
								case REMOVE: original.setLeadByte(Command.PROP_REMOVE);
									break;
								default:
									break;
								}
							// Propagate to all nodes in nodeList
							for(String nodeAddress : nodeList){
								if(debug_mode) {
									switch(c){
									case PUT: System.out.println("Propagating PUT command to: " + nodeAddress);
										break;
									case GET:  System.out.println("Propagating GET command to: " + nodeAddress);
										break;
									case REMOVE: System.out.println("Propagating REMOVE command to: " + nodeAddress);
										break;
										default:
											break;
									}
								}
									
									Propagate p = new Propagate("Propagation Thread for: "+nodeAddress,this,nodeAddress,original);
									//nodeReplies holds all of the replies
									nodeReplies.put(nodeAddress, p.propagate());
								}
						
							//Handle Replies
							for(Entry<String,Message> nodeReply : nodeReplies.entrySet() ){
								Message message = nodeReply.getValue();
								
								String address = nodeReply.getKey();
								ErrorCode e = (ErrorCode) message.getLeadByte();	
								
								switch(c){
								case PROP_PUT:
									if(e == ErrorCode.OK){
										if(debug_mode) System.out.println("Put operation successful at: " + address);
									}
									break;
								case PROP_GET:
									if(e == ErrorCode.OK && in_local_and_get){
									reply.setMessageValue(message.getMessageValue());
									reply.setLeadByte(e);
									}
									break;
								case PROP_REMOVE:
									if(reply.getLeadByte()==ErrorCode.KEY_DNE){
										if(e == ErrorCode.KEY_DNE)
										reply.setLeadByte(e);
										}
									break;
								default:
									break;
								
									}
								}
							}
				}
			}
			reply.sendReplyTo(out);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Sends m to the given node
	public Message propagateMessage(Message m,String address) throws Exception {
		Message nodeReply = new Message(); 
		nodeReply=(m.sendTo(address, this.port));
		return nodeReply;
		}

	// Reads in a file of IPs
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
	
	// Returns the first three bits of the key, and splits it into a value 1-8 for keyspaces
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

		int key_space_division_value = this.getFirstThreeBits(k.getValue(0));
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

	public Boolean getShutdownStatus() {
		return shutdown;
	}

	public void setShutdownStatus(Boolean shutdown) {
		this.shutdown = shutdown;
	}

	public Boolean getDebug_mode() {
		return debug_mode;
	}

	public void setDebug_mode(Boolean debug_mode) {
		this.debug_mode = debug_mode;
	}
}