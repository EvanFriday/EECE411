package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import tools.Message;

public class Propagate implements Runnable {
	private String address;
	private Server server;
	private Message message;
	private Message reply;
	private InputStream is;
	private OutputStream os;
	private Thread t;
	private Socket propagation_socket;

	public Propagate(String threadname, Server server,String address ,Message message) {
		this.address = address;
		this.server = server;
		this.message = message;
		this.reply = new Message();
		this.t = new Thread(this, threadname);
	}

	public void run() {
		try {
			
			reply = this.message.sendTo(this.os, this.is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message propagate() throws IOException {
		try {
			propagation_socket = new Socket(this.address,9999);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		is = propagation_socket.getInputStream();
		os = propagation_socket.getOutputStream();

		System.out.println("SERVER: Propagating Changes to: " + address.toString());
		t.start();

		return this.reply;
	}

}