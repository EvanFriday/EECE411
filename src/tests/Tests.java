package tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Server;
import tools.Command;
import tools.Key;
import tools.Message;
import tools.Value;

public class Tests {
	static Server server;
	static TestClient client1;
	static TestClient client2;
	static TestClient client3;
	static TestClient client4;
	
	public Tests() {
		try {
			server = new Server();
			client1 = new TestClient("Client 1");
			client1 = new TestClient("Client 2");
			client1 = new TestClient("Client 3");
			client1 = new TestClient("Client 4");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Tests test = new Tests();
		System.out.println("CLIENT: Key to input"+k.toString());
		System.out.println("CLIENT: Value to input"+v.toString());
		Thread t = new Thread(new Runnable() {
	        @Override
	        public void run() {
	            try {
	                server.AcceptConnections();
	                server.getServer().close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
	    t.start();
		
		

	}

}
