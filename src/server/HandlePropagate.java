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
		this.reply = new Message();
		this.address = address;
	}

	@Override
	public Message call() throws Exception{
		try {
			propagation_socket = new Socket(this.address,9999);
			
		} catch (UnknownHostException e) {
			Tools.print("Unknown Host Ex");
		} catch (IOException e) {
			Tools.print("Socket Creation Failed");
		}
		try {
			is = propagation_socket.getInputStream();
			os = propagation_socket.getOutputStream();
		} catch (IOException e) {
			Tools.print("Stream Creation Failed");
		}

		System.out.println("SERVER: Propagating "+this.message.getLeadByte().toString()+" to: " + address.toString());
		try {
				this.reply = this.message.sendTo(this.os, this.is);
				Tools.print("Reply from Propagation");
				Tools.printByte(this.reply.getMessageKey().key);
				Tools.printByte(this.reply.getMessageValue().value);
			
		} catch (Exception e) {
			Tools.print("Failed to receive reply from Propagation");
		}
		
		try {
			propagation_socket.close();
		} catch (IOException e) {
			Tools.print("Failed to close socket");
		}
	
		
		
		return this.reply;
	}

}
