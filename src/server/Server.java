package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import message.Key;
import message.Value;
import node.NodeData;
import sun.org.mozilla.javascript.internal.Node;

public class Server {
	Node nodeList;
	ServerSocket server;
	ArrayList all_nodes = new ArrayList<NodeData>();
	ConcurrentHashMap primary_space = new ConcurrentHashMap<Key, Value>();
	ConcurrentHashMap replica1_space = new ConcurrentHashMap<Key, Value>();
	ConcurrentHashMap replica2_space = new ConcurrentHashMap<Key, Value>();
	
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
	
	public void fileRead(String file_location) throws Exception{
		FileReader file = new FileReader(file_location);
		BufferedReader in = new BufferedReader(file);
		String line = in.readLine();
		NodeData nd;
		while(line != null) {
			InetAddress addr = InetAddress.getByName(line);
			if(addr.isReachable(5000)) // Ping with 5-second timeout
				nd = new NodeData(line, true); // Alive node
			else
				nd = new NodeData(line, false); // Dead node
			all_nodes.add(nd);
		}
		in.close();
	}

}
