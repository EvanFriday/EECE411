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

import clientserver.message.Command;
import clientserver.message.ErrorCode;
import clientserver.message.Key;
import clientserver.message.Message;
import clientserver.message.Value;

public class Server implements Remote {
	private ServerSocket serverSocket;
	private int port = 9999;
	private Map<Key, Value> kvStore;
	
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
				
				switch((Command) message.getLeadByte()){
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
				
				os.write(reply.getRaw());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void propagate() {
		String address1, address2, address3;
		// TODO:  Call selectAddresses() to get addresses to propagate to.
		address1 = address2 = address3 = "localhost";
		// Create three threads, to propagate to three nodes
		new Propagate(address1, "First node" , this).propagate();
		new Propagate(address2, "Second node", this).propagate();
		new Propagate(address3, "Third node", this).propagate();
	}
	
	public synchronized void propagateUpdate(String address) throws Exception {
		Socket connection = new Socket(address,port);
		InputStream is = connection.getInputStream();
		OutputStream os = connection.getOutputStream();
		byte[] replyRaw = new byte[Message.MAX_SIZE];
		Message reply;
		
		// Write data to OutputStream about each KeyValuePair
		for(Key k : kvStore.keySet()) {
			// Gets a value 1-8 for the keyspace division
			int key_space_division_value = this.getFirstThreeBits(k.getValue()[0]);
			
			Message toSend = new Message(Command.PUT, k, kvStore.get(k));
			os.write(toSend.getRaw());
			os.flush();
		}
		
		// Read error codes
		is.read(replyRaw, 0, Message.MAX_SIZE);
		reply = new Message(replyRaw);
		
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
		
		//TODO: Populate addressList with IP values
		
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