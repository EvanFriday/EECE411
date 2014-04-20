package tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Propagate;
import server.Server;
import tools.*;

public class TestClient {
	public InputStream is;
	public OutputStream os;
	private Message message;
	private Message reply;
	public Socket socket;
	private String name;
	public TestClient(String name){
		this.name = name;
		try {
			this.socket = new Socket("127.0.0.1",9999);
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.message = new Message();
		this.reply = new Message();
	}
	
	public Message getMessage(){
		return this.message;
	}
	public void editMessage(){
		Key k = new Key();
		Value v = new Value();
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
		this.message.setLeadByte(Command.PUT);
		this.message.setFullMessageKey(k);
		this.message.setFullMessageValue(v);
		System.out.println("CLIENT: Key values set= "+k.hashCode()+", "+this.message.getFullMessageKey().hashCode());
	}
	public void editMessage(Command command, Key key){
		this.message.setLeadByte(command);
		this.message.setFullMessageKey(key);
		Value v = new Value();
		byte rand = 0x01;
		for(int i = 0; i< Value.SIZE; i++){
			rand += rand;
			v.setValue(rand, i);
		}
		this.message.setFullMessageValue(v);
	}
	public void editMessage(Command command, Key key, Value value){
		this.message.setLeadByte(command);
		this.message.setFullMessageKey(key);
		this.message.setFullMessageValue(value);	
	}
	public void sendMessage(){
		try {
			System.out.println("CLIENT: "+this.name+" message values = "+this.message.getLeadByte().toString()+", "+this.message.getFullMessageKey().hashCode()+", "+this.message.getFullMessageValue().hashCode());
			this.reply = this.message.sendTo(this.os, this.is);
			if(this.message.getLeadByte() == Command.PUT || this.message.getLeadByte() == Command.REMOVE)
			System.out.println("CLIENT: "+this.name+":reply values = "+this.reply.getLeadByte());
			else
			System.out.println("CLIENT: "+this.name+":reply values = "+this.reply.getLeadByte().toString()+", "+this.reply.getFullMessageKey().hashCode()+", "+this.reply.getFullMessageValue().hashCode());
		} catch (IOException e) {
			System.err.println("CLIENT: Message sending Failed on: " + this.name);
		}	
	}

}
