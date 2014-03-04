/*
 * Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 * 
 * 
 */



package clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


<<<<<<< HEAD

//changes

=======
>>>>>>> 7b163c5fd5c14d7be1cbf02dd42311d8c92c8424

public class Server implements Remote {
	
	
	
	private static ServerSocket serverSocket;
	
	public static int i;
	
	public static boolean matchingKeyFound = false;
	public static boolean isPutOperation = false;
	public static byte[] command = new byte[1];
	public static byte[] key = new byte[32];
	public static byte[] value = new byte[1024];
	public static byte[] error_code = new byte[1];
	public static byte[] return_value = new byte[1024];

	
	public static ArrayList<KeyValuePair> KVStore;
	
	public Server(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}	
	public static void propagateUpdate() throws IOException, OutOfMemoryError{
		//TODO: Implement Pushing features
	}
	public static synchronized void acceptUpdate() throws IOException, OutOfMemoryError{
		//TODO: properly read in commands from propagate
		try {
			Socket connection = serverSocket.accept();
			InputStream is = connection.getInputStream();
			OutputStream os = connection.getOutputStream();
			
			while(true) {
				
				is.read(command, 0, 1);
				
				if(command[0] == 0x01) // Put operation - includes value
				{
					isPutOperation = true;
					is.read(key, 1, 32);
					is.read(value, 33, 1024);
					for(i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
					{
						KVStore.get(i);
						if(KeyValuePair.getKey() == key) // Match found
						{
							KeyValuePair.setValue(value);
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
				}
				
				else if(command[0] == 0x02) // Get operation
				{
					is.read(key, 1, 32);
					for(i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
					{
						KVStore.get(i);
						if(KeyValuePair.getKey() == key) // Match found
						{
							return_value = KeyValuePair.getValue();
							error_code[0] = 0x00;
							matchingKeyFound = true;
							break;
						}
					}
					if(matchingKeyFound)
						matchingKeyFound = false;
					else
						error_code[0] = 0x01;
				}
				
				else if(command[0] == 0x03) // Remove operation
				{
					is.read(key, 1, 32);
					for(i=0; i<KVStore.size(); i++) // Search for a KV pair with matching key
					{
						KVStore.get(i);
						if(KeyValuePair.getKey() == key) // Match found
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
				}
				
				else // Invalid command
					error_code[0] = 0x05;
				
				// Send result
				if(isPutOperation)
				{
					os.write(error_code);
					os.write(return_value);
					isPutOperation = false;
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
	
}