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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import clientserver.message.Command;
import clientserver.message.ErrorCode;
import clientserver.message.Key;
import clientserver.message.Message;
import clientserver.message.Value;

public class Server implements Remote {
	private ServerSocket serverSocket;
	private int port = 9999;
	private Map<Key, Value> kvStore;
	
	//Map for possibly non local values;
	private Map<Key, Value> dirtyPut;
	private Map<Key, Value> dirtyGet;
	private Map<Key, Value> dirtyRemove;
	
	
	public static ArrayList<String> set_one;
	public static ArrayList<String> set_two;
	public static ArrayList<String> set_three;
	public static ArrayList<String> set_four;
	public static ArrayList<String> set_five;
	public static ArrayList<String> set_six;
	public static ArrayList<String> set_seven;
	public static ArrayList<String> set_eight;

	public Server(int port) throws Exception {
		this.port = port;
		this.serverSocket = new ServerSocket(port);
		this.kvStore = new HashMap<Key, Value>();
		this.dirtyPut = new HashMap<Key, Value>();
		this.dirtyGet = new HashMap<Key, Value>();
		this.dirtyRemove = new HashMap<Key, Value>();
	}
	
	public synchronized void acceptUpdate() {
		try {
			while(true) {
				Socket connection = this.serverSocket.accept();
				InputStream is = connection.getInputStream();
				OutputStream os = connection.getOutputStream();
				
				byte[] raw = new byte[Message.MAX_SIZE];
				Message message, reply;
				Key k;
				Value v;
				
				// Read values
				is.read(raw, 0, Message.MAX_SIZE);
				message = new Message(raw);
				reply = new Message();
				k = message.getKey();
				v = message.getValue();
				
				/*
				 * 
				 * Logic to check if stored locally, or on other node sets.
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
				switch((Command) message.getLeadByte()){
				case PUT:
					dirtyPut.put(k,v);
					break;
				case GET:
					dirtyGet.put(k,v);
					break;
				case REMOVE:
					dirtyRemove.put(k,v);
					break;
				default:
						//error
					break;
						
				}

				/*
				switch((Command) message.getLeadByte()){
				case PUT:
					if (kvStore.size() < Key.MAX_NUM) {
						dirtyList.put(k, v);
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
				
				os.write(reply.getRaw());*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * 
	 * Need to totally re-create to push to nodes in keyspaces.
	 * 
	 */
	
	
	public void propagate(){
		
	}
	
	
	public Message sendMessageToNode(Message message, String address) throws Exception {
		Socket connection = new Socket(address,port);
		InputStream is = connection.getInputStream();
		OutputStream os = connection.getOutputStream();
		os.write(message.getRaw());
		os.flush();
		byte[] replyByteForm = new byte[Message.MAX_SIZE];
		is.read(replyByteForm,0,Message.MAX_SIZE);
		
		Message reply = new Message(replyByteForm);
		
		switch((ErrorCode) reply.getLeadByte()) {
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
		connection.close();
		return reply;
		
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
	
	public void selectAddresses(){
		//TODO: Randomly select a number of items from addressList and populate propagateAddressList with them
	}
}