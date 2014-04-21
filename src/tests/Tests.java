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
		boolean debug = true;
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

		/*
		for(int i = 0; i < 1+32+1024; i++){
			if(i == 0)
				message[i]= c[0];
			else if(i<32+1 && i>0)
				message[i] = k.getValue(i-1);
			else
				message[i] = v.getValue(i-32-1);
		}
		*/
				
	    
	    Tools.print("CLIENT: Sending = ");
	    Tools.print(m1.getLeadByte().toString());
	    Tools.printByte(m1.getFullMessageKey().key);
	    Tools.printByte(m1.getFullMessageValue().value);
	    
	    r1 = m1.sendTo(client1.os, client1.is);
		Tools.print("CLIENT: Receiving Reply: ");
	    Tools.print(r1.getLeadByte().toString());
	    client1.is.read(r1.getRaw());

	    Tools.print("CLIENT Reply ErrorCode = "+ErrorCode.getErrorCode(replyerr).toString());
	    
	    
	    /*
	     * CLIENT 2 : Get
	     */
	    /*
	    for(int i = 0; i < 32; i++){
			if(i == 0)
				message2[i] = c[1];
	    	if(i<32)
	    		message2[1+i] = k.getValue(i);
		}
		*/
	    
	    Message m2 = new Message(Command.GET, k);
	    Message r2 = new Message();
	    
	    Tools.print("CLIENT: Sending = ");
	    Tools.print(m2.getLeadByte().toString());
	    Tools.print(m2.getFullMessageKey());
	    
	    m2.sendTo(client2.os, client2.is);
	    
	    client2.is.read(r2.getRaw());

		Tools.print("CLIENT: Receiving Reply: ");
		Tools.print("CLIENT: ErrorCode = "+r2.getLeadByte().toString());
	    Tools.printByte(r2.getFullMessageValue().value);

//	    Thread.sleep(500);
//	    client2.editMessage();
//	    client2.sendMessage();
//	    Thread.sleep(500);
//	    test.client3.editMessage(Command.GET,test.client2.getMessage().getMessageKey());
//	    test.client3.sendMessage();
//	    Thread.sleep(500);
//	    test.client4.editMessage(Command.REMOVE,test.client2.getMessage().getMessageKey());
//	    test.client4.sendMessage();
		
		server.getServer().close();
	}

}
