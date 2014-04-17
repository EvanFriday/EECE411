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
	private InputStream is;
	private OutputStream os;
	private Message message;
	private Message reply;
	private Socket socket;
	private String name;
	public TestClient(String name){
		this.name = name;
		try {
			this.socket = new Socket("127.0.0.1",9999);
			this.is = socket.getInputStream();
			this.os = socket.getOutputStream();
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
		this.message.setMessageKey(k);
		this.message.setMessageValue(v);
	}
	public void editMessage(Command command, Key key){
		this.message.setLeadByte(command);
		this.message.setMessageKey(key);
	}
	public void editMessage(Command command, Key key, Value value){
		this.message.setLeadByte(command);
		this.message.setMessageKey(key);
		this.message.setMessageValue(value);	
	}
	public void sendMessage(){
		try {
			System.out.println("CLIENT "+name+":message values = "+this.message.getLeadByte().toString()+","+this.message.getMessageKey().hashCode()+this.message.getMessageValue().hashCode());
			this.reply = this.message.sendTo(os, is);
			System.out.println("CLIENT "+name+":reply values = "+this.reply.getLeadByte().toString()+","+this.reply.getMessageKey().hashCode()+this.reply.getMessageValue().hashCode());
		} catch (IOException e) {
			System.err.println("Message sending Failed on:" + this.name);
		}	
	}

}
