package tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import server.Server;
import tools.*;


public class Tests {
	static Server server;
	static TestClient client1;
	static TestClient client2;
	static TestClient client3;
	static TestClient client4;
	
	public Tests() {
		try {
			server = new Server();
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
		boolean debug = false;
		Tests test = new Tests();
		Thread t = new Thread(new Runnable() {
	        @Override
	        public void run() {
	            try {
	            	while(true){
	            		server.AcceptConnections();
	            	}
	            } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
	    t.start();
	    client1 = new TestClient("Client 1");
		client2 = new TestClient("Client 2");
		client3 = new TestClient("Client 3");
		client4 = new TestClient("Client 4");
	    
	    Key k = new Key();
		Value v = new Value();
		Random rn = new Random();
		rn.nextBytes(k.key);
		rn.nextBytes(v.value);
		byte replyerr = 0;
		
		Message m1 = new Message(Command.PUT,k,v);
		Message r1 = new Message();

	    /*
	     * CLIENT 1 : Put
	     */					
	    
	    Tools.print("CLIENT: Sending = "+m1.getLeadByte().toString());
	    Tools.printByte(m1.getFullMessageKey().key);
	    Tools.printByte(m1.getFullMessageValue().value);
	    
	    r1 = m1.sendTo(client1.os, client1.is);
		Tools.print("CLIENT: Receiving Reply: "+r1.getLeadByte().toString());
	    
	    /*
	     * CLIENT 2 : Get
	     */
	    
	    Message m2 = new Message(Command.GET, k);
	    Message r2 = new Message();
	    
	    Tools.print("CLIENT: Sending = "+m2.getLeadByte().toString());
	    Tools.printByte(m2.getFullMessageKey().key);
	    
	    r2 = m2.sendTo(client2.os, client2.is);
		Tools.print("CLIENT: Receiving Reply: "+r2.getLeadByte().toString());
	    Tools.printByte(r2.getFullMessageValue().value);

	    
	    /*
	     * CLIENT 3 : Remove
	     */
	    
	    Message m3 = new Message(Command.REMOVE, k);
	    Message r3 = new Message();
	    
	    Tools.print("CLIENT: Sending = "+m3.getLeadByte().toString());
	    Tools.printByte(m3.getFullMessageKey().key);
	    
	    r3 = m3.sendTo(client3.os, client3.is);
		Tools.print("CLIENT: Receiving Reply: "+r3.getLeadByte().toString());
		
		
		/*
	     * CLIENT 4 : Get
	     */
	    
	    Message m4 = new Message(Command.GET, k);
	    Message r4 = new Message();
	    
	    Tools.print("CLIENT: Sending = "+m4.getLeadByte().toString());
	    Tools.printByte(m4.getFullMessageKey().key);
	    
	    r4 = m4.sendTo(client4.os, client4.is);
		Tools.print("CLIENT: Receiving Reply: "+r4.getLeadByte().toString());
	    if(r4.getFullMessageValue() != null)
	    	Tools.printByte(r4.getFullMessageValue().value);
//	    Thread.sleep(500);
//	    client2.editMessage();
//	    client2.sendMessage();
//	    Thread.sleep(500);
//	    test.client3.editMessage(Command.GET,test.client2.getMessage().getMessageKey());
//	    test.client3.sendMessage();
//	    Thread.sleep(500);
//	    test.client4.editMessage(Command.REMOVE,test.client2.getMessage().getMessageKey());
//	    test.client4.sendMessage();
		
	}

}
