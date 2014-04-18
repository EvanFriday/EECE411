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
			client2 = new TestClient("Client 2");
			client3 = new TestClient("Client 3");
			client4 = new TestClient("Client 4");
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
	    test.client1.editMessage();
	    test.client1.sendMessage();
	    test.client2.editMessage();
	    test.client2.sendMessage();
	    test.client3.editMessage(Command.GET,test.client2.getMessage().getFullMessageKey());
	    test.client3.sendMessage();
	    test.client4.editMessage(Command.REMOVE,test.client2.getMessage().getFullMessageKey());
	    test.client4.sendMessage();
		
		

	}

}
