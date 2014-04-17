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
	static Key k;
	static Value v;
	
	public Tests() {
		try {
			server = new Server();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		k = new Key();
		v = new Value();
		byte rand = 0x01;
		byte rand2 = 0x01;
		for(int i = 0; i< Key.SIZE; i++){
			rand += rand;
			k.setValue(rand, i);
		}
		for(int i = 0; i< Value.SIZE; i++){
			rand2 += rand2;
			v.setValue(rand2, i);
		}
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Tests test = new Tests();
		Socket testsocket = null;
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
		
	    try {
			testsocket = new Socket("127.0.0.1",9999);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = testsocket.getInputStream();
		OutputStream os = testsocket.getOutputStream();
		Message message = new Message();
		Message reply = new Message();
		message.setLeadByte(Command.PUT);
		message.setMessageKey(k);
		message.setMessageValue(v);
		reply = message.sendTo(os, is);
		System.out.print("CLIENT: Reply message" + reply.getLeadByte() +" "+ reply.getMessageKey());
		
		
		
		testsocket.close();
		

	}

}
