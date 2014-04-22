package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import tools.Message;
import tools.Tools;

public class HandlePropagate implements Callable<Message>{
	private String address;
	private Message message;
	private Message reply;
	private InputStream is;
	private OutputStream os;
	private Socket propagation_socket;
	public HandlePropagate(Message message,String address) {
		this.message = message;
		this.address = address;
	}

	@Override
	public Message call() throws Exception {
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
		propagation_socket.close();
		Tools.print("Reply from Propagation");
		Tools.printByte(this.reply.getMessageKey().key);
		Tools.printByte(this.reply.getMessageValue().value);
		return this.reply;
	}

}
