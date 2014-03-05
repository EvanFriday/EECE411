/*
 * Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 * 
 * 
 */



package clientserver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class Server implements Remote {
	
	
	
	private static ServerSocket serverSocket;
	
	public static int i;
	
	public static boolean matchingKeyFound = false;
	public static boolean isGetOperation = false;
	public static byte[] command = new byte[1];
	public static byte[] key = new byte[32];
	public static byte[] value = new byte[1024];
	public static byte[] error_code = new byte[1];
	public static byte[] return_value = new byte[1024];
	final static int port = 9999;
	public static String address1,address2,address3;
	public static ArrayList<KeyValuePair> KVStore;
	public static ArrayList<String> addressList;
	
	public Server(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}
	public static void propagate(){
		//TODO: CREATE A RANDOM IP PICKER AFTER CALLING FILE READ.
		// Create three threads, to propagate to three nodes
		Thread t1 = new Thread(new Runnable()
		{
			public void run(){
				try {
					Server.propagateUpdate(address1, port);
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		});
		Thread t2 = new Thread(new Runnable()
		{
			public void run(){
				try {
					Server.propagateUpdate(address2, port);
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		});
		Thread t3 = new Thread(new Runnable()
		{
			public void run(){
				try {
					Server.propagateUpdate(address3, port);
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		});
		t1.start();
		t2.start();
		t3.start();
		
		
	}
	
	public static synchronized void propagateUpdate(String address, int port) throws IOException, OutOfMemoryError{
			
			Socket connection = new Socket(address,port);
			InputStream is = connection.getInputStream();
			OutputStream os = connection.getOutputStream();
			byte[] returnedErrorCode = new byte[1];
			// byte[] returnedValue = new byte[1024];
			
			// Write data to OutputStream about each KeyValuePair
			for(int j=0; j<KVStore.size(); j++) {
				KeyValuePair KVP = KVStore.get(j);
				byte[] b = new byte[1+32+1024];
				b[0] = 0x01; // Put command
				for(int k=0; k<32; k++) { // Copy "key" value into b
					b[k+1] = KVP.key[k];
				}
				for(int k=0; k<1024; k++) { // Copy "value" value into b
					b[k+33] = KVP.value[k];
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
	public static synchronized void acceptUpdate() throws IOException, OutOfMemoryError{
		//TODO: properly read in commands from propagate
		try {
			Socket connection = serverSocket.accept();
			InputStream is = connection.getInputStream();
			OutputStream os = connection.getOutputStream();
			KeyValuePair localKey = new KeyValuePair();
			
			while(true) {
				//Read values
				is.read(command, 0, 1);
				is.read(key, 1, 32);
				is.read(value, 33, 1024);
				
				switch((int)command[0]){
				case 0x01: //put operation
						
							for(i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
							{
								localKey=KVStore.get(i);
								if(localKey.getKey() == key) // Match found
								{
									localKey.setValue(value);
									matchingKeyFound = true;
									break;
								}
							}
							if(matchingKeyFound)
								matchingKeyFound = false;
							else // Only add a new entry if there was none already with matching key
							{
								if(KVStore.size() < 40000)
								{
									KVStore.add(new KeyValuePair(key, value));
									error_code[0] = 0x00;
								}
								else // Out of space
								{
									error_code[0] = 0x02;
								}
							}
								
							error_code[0] = 0x00;
							break;
				case 0x02: // search operation

					isGetOperation = true;
							for(i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
							{
								localKey = KVStore.get(i);
								if(localKey.getKey() == key) // Match found
								{
									return_value = localKey.getValue();
									error_code[0] = 0x00;
									matchingKeyFound = true;
									break;
								}
							}
							if(matchingKeyFound)
								matchingKeyFound = false;
							else
								error_code[0] = 0x01;
							break;
				case 0x03: //remove operation
							for(i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
							{
								localKey=KVStore.get(i);
								if(localKey.getKey() == key) // Match found
								{
									KVStore.remove(i);
									error_code[0] = 0x00;
									matchingKeyFound = true;
									break;
								}
							}
							if(matchingKeyFound)
								matchingKeyFound = false;
							else
								error_code[0] = 0x01;
							break;
				default:
							//Command wasn't anything we wanted... derp!
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
		}
	}
	public static void fileRead(String file_location) throws IOException{
		//TODO: read the IP list file dawg.
		FileReader file = new FileReader(file_location);
		//TODO: pick three random files to propagate to
		
		file.close();
		
	}
	
	
}