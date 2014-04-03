package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;

import node.NodeData;
import sun.org.mozilla.javascript.internal.Node;

public class Server {
	Node nodeList;
	ServerSocket server;
	ArrayList all_nodes = new ArrayList<NodeData>();
	LinkedList primary_space = new LinkedList<Node>();
	LinkedList child1_space = new LinkedList<Node>();
	LinkedList child2_space = new LinkedList<Node>();
	
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
