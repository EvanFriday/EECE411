/*
 * Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 * 
 * 
 */



package clientserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class Server implements Remote {
	
	
	
	private ServerSocket serverSocket;
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
	private int port = 9999;
	
	
	public Server(int port) throws IOException{
		this.port = port;
		serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(10000);
		KVStore = new ArrayList<KeyValuePair>();
		
		addressList = new ArrayList<String>();
		propagateAddressList = new ArrayList<String>();		
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
	public synchronized void acceptUpdate() throws IOException, OutOfMemoryError, SocketTimeoutException{
		//TODO: properly read in commands from propagate
		try {
			while(true){
			Socket connection = serverSocket.accept();
			InputStream is = connection.getInputStream();
				//	InputStream(connection.getInputStream());
			OutputStream os = connection.getOutputStream();
			KeyValuePair localKey = new KeyValuePair();
			KeyValuePair newKey = new KeyValuePair();
			byte[] input_read = new byte[1+32+1024];
			
			
				//Read values
			/*	
			is.read(input_read, 0, 1+32+1024);
				command[0] = input_read[0];
				for(int ii=0; ii<32; ii++)
					key[ii] = input_read[ii+1];
				if(command[0] == 0x01) {
					for(int ii=0; ii<1024; ii++)
						value[ii] = input_read[ii+33];
				}*/
				
				is.read(command, 0, 1);
				is.read(key, 1, 32);
				if(command[0] == 0x01) // There is only a value input if it's a put operation
				is.read(value, 33, 1024);
				
				switch(command[0]){
				case 0x01: //put operation
						
							for(int i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
							{
								localKey=KVStore.get(i);
								matchingKeyFound = true;
								for(int j=0; j<32; j++) { // Compare each byte of key
									if(localKey.getKey(j) != key[j]) { // Mismatch
										matchingKeyFound = false;
										break;
									}
								}
								if(matchingKeyFound) // Match has been found
								{
									for(int j=0; j<32; j++) // Copy key bytes one by one
										newKey.setKey(key[j], j);
									for(int j=0; j<1024; j++) // Copy value bytes one by one
										newKey.setValue(value[j],  j);
									KVStore.set(i, newKey); // Copy new KVP into KVStore
									break;
								}
							}
							if(matchingKeyFound){
								matchingKeyFound = false;
								error_code[0] = 0x00;
							}
							else // Only add a new entry if there was none already with matching key
							{
								if(KVStore.size() < 40000)
								{
									for(int j=0; j<32; j++) // Copy key bytes one by one
										newKey.setKey(key[j], j);
									for(int j=0; j<1024; j++) // Copy value bytes one by one
										newKey.setValue(value[j],  j);
									KVStore.add(newKey); // Add new KVP to KVStore
									error_code[0] = 0x00;
								}
								else // Out of space
								{
									error_code[0] = 0x02;
								}
							}
								
							
							break;
				case 0x02: // search operation

					isGetOperation = true;
							for(int i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
							{
								localKey=KVStore.get(i);
								matchingKeyFound = true;
								for(int j=0; j<32; j++) { // Compare each byte of key
									if(localKey.getKey(j) != key[j]) { // Mismatch
										matchingKeyFound = false;
										break;
									}
								}
								if(matchingKeyFound) // Match has been found
								{
									for(int j=0; j<1024; j++) // Copy value bytes one by one
										return_value[j] = localKey.getValue(j);
									error_code[0] = 0x00;
									break;
								}
								else
									error_code[0] = 0x01; // Inexistent key
							}
							break;
				case 0x03: //remove operation
							for(int i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
							{
								localKey=KVStore.get(i);
								matchingKeyFound = true;
								for(int j=0; j<32; j++) { // Compare each byte of key
									if(localKey.getKey(j) != key[j]) { // Mismatch
										matchingKeyFound = false;
										break;
									}
								}
								if(matchingKeyFound) // Match found
								{
									KVStore.remove(i);
									error_code[0] = 0x00;
									break;
								}
							}
							if(!matchingKeyFound)
								error_code[0] = 0x01; // Inexistent key
							break;
				default:
							//Command wasn't anything we wanted... Derp!
							error_code[0] = 0x05;
							break;
				}
				
				// Send result
				if(isGetOperation)
				{
					os.write(error_code);
					os.write(return_value);
					isGetOperation = false;
				}
				else
					os.write(error_code);
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
			error_code[0] = 0x04; // Internal KV Store failure?
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error_code[0] = 0x04; // Internal KV Store failure?
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			error_code[0] = 0x02; // Out of space
		} catch (SocketTimeoutException e){
			e.printStackTrace();
		}
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