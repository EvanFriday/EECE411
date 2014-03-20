/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
	
	public static boolean matchingKeyFound = false;
	public static boolean isGetOperation = false;
	public static byte[] command = new byte[1];
	public static byte[] key = new byte[32];
	public static byte[] value = new byte[1024];
	public static byte[] error_code = new byte[1];
	public static byte[] return_value = new byte[1024];
	public static ArrayList<String> set_one;
	public static ArrayList<String> set_two;
	public static ArrayList<String> set_three;
	public static ArrayList<String> set_four;
	public static ArrayList<String> set_five;
	public static ArrayList<String> set_six;
	public static ArrayList<String> set_seven;
	public static ArrayList<String> set_eight;

	public ArrayList<KeyValuePair> KVStore;
	public ArrayList<KeyValuePair> DirtyStore;
	public ArrayList<String> addressList;
	public  ArrayList<String> propagateAddressList;
	private String address1,address2,address3;
	
	
	public Server(int port) throws IOException {
		this.port = port;
		this.serverSocket = new ServerSocket(port);
		this.kvStore = new HashMap<Key, Value>();
	}
	
	public synchronized void acceptUpdate() throws IOException, OutOfMemoryError, SocketTimeoutException{
		//TODO: properly read in commands from propagate
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
				//is.read(command, 0, 1);
				//is.read(key, 1, 32);
				// if(command[0] == 0x01) // There is only a value input if it's a put operation
					// is.read(value, 33, 1024);
				
				switch((Command) message.getLeadByte()){
				case PUT: // 152
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
	
	public void propagate(){
		//TODO:  Call selectAddresses() to get addresses to propagate to.
		address1 = address2 = address3 = "localhost";
		// Create three threads, to propagate to three nodes
		Propagate p1 = new Propagate(address1, "First node" , this);
		Propagate p2 = new Propagate(address2, "Second node", this);
		Propagate p3 = new Propagate(address3, "Third node", this);
		p1.propagate();
		p2.propagate();
		p3.propagate();
	}
	
	public synchronized void propagateUpdate(String address) throws IOException, OutOfMemoryError {
			
			Socket connection = new Socket(address,port);
			InputStream is = connection.getInputStream();
			OutputStream os = connection.getOutputStream();
			byte[] returnedErrorCode = new byte[1];
			// byte[] returnedValue = new byte[1024];
			
			// Write data to OutputStream about each KeyValuePair
			for(int j=0; j<KVStore.size(); j++) {
				
				KeyValuePair KVP = KVStore.get(j);
				//gets a value 1-8 for the keyspace division
				int key_space_division_value = this.getFirstThreeBits(KVP.getKey(0));
				
				
				byte[] b = new byte[1+32+1024];
				b[0] = 0x01; // Put command
				for(int k=0; k<32; k++) { // Copy "key" value into b
					b[k+1] = KVP.getKey(k);
				}
				for(int k=0; k<1024; k++) { // Copy "value" value into b
					b[k+33] = KVP.getValue(k);
				}
				os.write(b);
				os.flush();
			}
			
			// Read error codes
			is.read(returnedErrorCode, 0, 1);
			
			switch(returnedErrorCode[0]) {
			case 0x00:
				System.out.println("Operation successful.");
			case 0x01:
				System.out.println("Error: Inexistent key.");
			case 0x02:
				System.out.println("Error: Out of space.");
			case 0x03:
				System.out.println("Error: System overload.");
			case 0x04:
				System.out.println("Error: Internal KVStore failure.");
			case 0x05:
				System.out.println("Error: Unrecognized command.");
			default:
				System.out.println("Error: Unknown error.");
			}
			
		connection.close();
		
	}
	
	public void fileRead(String file_location) throws IOException{
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