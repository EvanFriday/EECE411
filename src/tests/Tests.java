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
	    byte[] c = new byte[2];
	    c[0] = 0x01;
	    c[1] = 0x02;
	    Key k = new Key();
		Value v = new Value();
		Random rn = new Random();
		rn.nextBytes(k.key);
		rn.nextBytes(v.value);
		byte[] message = new byte[1+32+1024];
		byte[] reply = new byte[1+32+1024];
		byte replyerr = 0;
		

	    /*
	     * CLIENT 1 : Put
	     */	

		for(int i = 0; i < 1+32+1024; i++){
			if(i == 0)
				message[i]= c[0];
			else if(i<32+1 && i>0)
				message[i] = k.getValue(i-1);
			else
				message[i] = v.getValue(i-32-1);
		}
				
	    client1.os.write(message);
	    Tools.print("CLIENT: Sending = ");
	    Tools.print(Command.getCommand(c[0]).toString());
	    Tools.printByte(k.key);
	    Tools.printByte(v.value);
	    client1.is.read(reply);
		for(int i=0;i<reply.length;i++){
			if(i==0)
				replyerr = reply[i];
			else if(1<=i && i<33)
				k.setValue(message[i], i-1);
			else
				v.setValue(message[i], i-1-32);
		}
	    Tools.print("CLIENT: Receiving =");
	    Tools.print(ErrorCode.getErrorCode(replyerr).toString());
	    Tools.printByte(k.key);
	    Tools.printByte(v.value);
	    /*
	     * CLIENT 2 : Get
	     */
	    byte[] message2 = new byte[1+32];
	    byte[] reply2 = new byte[1+32+1024];
	    for(int i = 0; i < 32; i++){
			if(i == 0)
				message2[i] = c[1];
	    	if(i<32)
	    		message2[1+i] = k.getValue(i);
		}
	    client2.os.write(message2);
	    Tools.print("CLIENT: Sending = ");
	    Tools.print(Command.getCommand(message2[0]).toString());
	    Tools.printByte(k.key);
	    
	    client1.is.read(reply2);
		for(int i=0;i<reply2.length;i++){
			if(i==0)
				replyerr = reply2[i];
			else if(1<=i && i<33)
				k.setValue(message[i], i-1);
			else
				v.setValue(message[i], i-1-32);
		}
	    Tools.print("CLIENT: Receiving =");
	    Tools.print(ErrorCode.getErrorCode(replyerr).toString());
	    Tools.printByte(k.key);
	    Tools.printByte(v.value);

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
