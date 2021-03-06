package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import tools.Message;
import tools.Tools;

public class Propagate implements Runnable {
	private String address;
	private Message message;
	private Message reply;
	public Message getReply() {
		return reply;
	}

	private InputStream is;
	private OutputStream os;
	private Socket propagation_socket;

	public Propagate(Thread thread,String address ,Message message, Message reply) {
		this.address = address;
		this.message = message;
		this.reply = new Message();
		this.reply = reply;
		
	}

	public void run() {
		this.reply = propagate();
	}

	public Message propagate(){
		try {
			propagation_socket = new Socket(this.address,9999);
			is = propagation_socket.getInputStream();
			os = propagation_socket.getOutputStream();
		} catch (UnknownHostException e) {
			Tools.print("Unknown Host Ex");
		} catch (IOException e) {
			Tools.print("IO Ex");
		}
		System.out.println("SERVER: Propagating Changes to: " + address.toString());
		try {
			this.reply = this.message.sendTo(this.os, this.is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.reply;
	}

}