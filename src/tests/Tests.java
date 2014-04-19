package tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import server.Server;
import tools.Command;
import tools.Key;
import tools.LeadByte;
import tools.Message;
import tools.Tools;
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
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Tests test = new Tests();
		Thread t = new Thread(new Runnable() {
	        @Override
	        public void run() {
	            try {
	                server.AcceptConnections();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
	    t.start();
	    Key k = new Key();
		Value v = new Value();
		//byte rand = 0x01;
		byte rand2 = 0x01;
		Random rn = new Random();
		rn.nextBytes(k.key);
		rn.nextBytes(v.value);
		byte[] message = new byte[1+32+1024];
		byte[] reply = new byte[1+32+1024];
		message[0] = 0x01;
		for(int i = 0; i < 32+1024; i++){
			if(i<32)
			message[1+i] = k.getValue(i);
			else
			message[1+i] = v.getValue(i-32);
		}
		
		
		
	    client1.os.write(message);
	    Tools.print("sending");
	    Tools.printByte(message);
	    client1.is.read(reply);
	    Tools.print("receiving");
	    Tools.printByte(reply);
	    
//	    test.client1.sendMessage();
//	    Thread.sleep(500);
//	    test.client2.editMessage();
//	    test.client2.sendMessage();
//	    Thread.sleep(500);
//	    test.client3.editMessage(Command.GET,test.client2.getMessage().getMessageKey());
//	    test.client3.sendMessage();
//	    Thread.sleep(500);
//	    test.client4.editMessage(Command.REMOVE,test.client2.getMessage().getMessageKey());
//	    test.client4.sendMessage();
		
		server.getServer().close();

	}

}
