package tests;

import java.io.IOException;
import java.util.Random;
import server.Server;
import tools.*;


public class Tests {
	Server server;
	TestClient client1;
	TestClient client2;
	TestClient client3;
	TestClient client4;
	TestClient client5;
	TestClient client6;
	
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
		Tests test = new Tests();
		final Server s = test.server;
		Thread t = new Thread(new Runnable() {
	        @Override
	        public void run() {
	            try {
	            	while(true){
	            		if(s.getNode().getAlive()){
	            			s.AcceptConnections();
	            		}	
	            		else{
	            			Tools.print("Server has Died");
	            			s.getServer().close();
	            			break;
	            		}

	            	}
	            	
	            } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
	    t.start();
	    test.client1 = new TestClient("Client 1");
	    test.client2 = new TestClient("Client 2");
	    test.client3 = new TestClient("Client 3");


		
	    
	    Key k = new Key();
		Value v = new Value();
		Random rn = new Random();
		rn.nextBytes(k.key);
		rn.nextBytes(v.value);
		Message m1 = new Message(Command.PUT,k,v);
		Message r1 = new Message();

	    /*
	     * CLIENT 1 : Put
	     */					
	    
	    Tools.print("CLIENT: Sending = "+m1.getLeadByte().toString());
	    Tools.printByte(m1.getFullMessageKey().key);
	    Tools.printByte(m1.getFullMessageValue().value);
	    
	    r1 = m1.sendTo(test.client1.os, test.client1.is);
		Tools.print("CLIENT: Receiving Reply: "+r1.getLeadByte().toString());
	    
	    /*
	     * CLIENT 2 : Get
	     */
	    
	    Message m2 = new Message(Command.GET, k);
	    Message r2 = new Message();
	    
	    Tools.print("CLIENT: Sending = "+m2.getLeadByte().toString());
	    Tools.printByte(m2.getFullMessageKey().key);
	    
	    r2 = m2.sendTo(test.client2.os, test.client2.is);
		Tools.print("CLIENT: Receiving Reply: "+r2.getLeadByte().toString());
	    Tools.printByte(r2.getFullMessageValue().value);

	    
	    /*
	     * CLIENT 3 : Remove
	     */
	    
	    Message m3 = new Message(Command.REMOVE, k);
	    Message r3 = new Message();
	    
	    Tools.print("CLIENT: Sending = "+m3.getLeadByte().toString());
	    Tools.printByte(m3.getFullMessageKey().key);
	    
	    r3 = m3.sendTo(test.client3.os, test.client3.is);
		Tools.print("CLIENT: Receiving Reply: "+r3.getLeadByte().toString());
		
		
		/*
	     * CLIENT 4 : Get
	     */
		test.client4 = new TestClient("Client 4");
	    Message m4 = new Message(Command.GET, k);
	    Message r4 = new Message();
	    
	    Tools.print("CLIENT: Sending = "+m4.getLeadByte().toString());
	    Tools.printByte(m4.getFullMessageKey().key);
	    
	    r4 = m4.sendTo(test.client4.os, test.client4.is);
		Tools.print("CLIENT: Receiving Reply: "+r4.getLeadByte().toString());
	    if(r4.getFullMessageValue() != null)
	    	Tools.printByte(r4.getFullMessageValue().value);
	    
	    /*
	     * CLIENT 5: Shutdown
	     */
	    test.client5 = new TestClient("Client 5");
	    Message m5 = new Message();
	    m5.setLeadByte(Command.SHUTDOWN);
	    Message r5 = new Message();
	    Tools.print("CLIENT: 5 Sending = "+m5.getLeadByte().toString());
	    r5 = m5.sendTo(test.client5.os, test.client5.is);
	    Tools.print("CLIENT: Receiving Reply: "+r5.getLeadByte().toString());
	    
	    /*
	     * CLIENT 6: Checking Shutdown
	     */
	    Thread.sleep(3000);
	    test.client6 = new TestClient("Client 6");
	    Message m6 = new Message(Command.GET,k);
	    Message r6 = new Message();
	    Tools.print("CLIENT: Sending = "+m6.getLeadByte().toString());
	    Tools.printByte(m6.getFullMessageKey().key);
	    r6 = m6.sendTo(test.client6.os, test.client6.is);
	    Tools.print("CLIENT: Receiving Reply: "+r6.getLeadByte().toString());
	}

}
