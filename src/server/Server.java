package server;

import java.io.IOException;
import java.net.*;

import sun.org.mozilla.javascript.internal.Node;

public class Server {
	Node nodeList;
	ServerSocket server;
	
	//CONSTRUCTOR
	public Server() throws IOException {
		nodeList = new Node(0);
			server = new ServerSocket();
		/*TODO: 
		 * -Create a thread pool
		 * -Populate Node List
		 * 
		 */
	}
	
	public void AcceptConnections() throws IOException{
		Socket client = server.accept();
		/*
		 * TODO: Assign handling of incoming message in a thread from thread pool
		 * 
		 * 
		 * 
		 */
	}
	
	

}
