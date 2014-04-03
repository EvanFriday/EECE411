package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import tools.Node;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	private List<Node> nodeList;
	private ServerSocket server;
	private Socket client;
	private String file_location;
	private Node node;
	//CONSTRUCTOR
	public Server() throws IOException {
			server = new ServerSocket();
			nodeList = new ArrayList<Node>();
			setNode(new Node());
	}
	
	public void AcceptConnections() throws IOException{
		client = server.accept();
		/*
		 * TODO: Assign handling of incoming message in a thread from thread pool
		 * 
		 * 
		 * 
		 */
	}
	
	public void PopulateNodeList() throws UnknownHostException, IOException{
		FileReader file = new FileReader(file_location);
		BufferedReader in = new BufferedReader(file);
		String line;
		int index=0;
		while((line = in.readLine()) != null){
			InetAddress address = InetAddress.getByName(line);
			if(address.isReachable(1000)) // Ping with a one second timeout
				nodeList.add(new Node(index,address,true));
			else
				nodeList.add(new Node(index,address,false)); // Dead node
		}

		file.close();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
