package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import tools.Node;

public class Server {
	private List<Node> nodeList;
	private ServerSocket server;
	private Socket client;
	private String file_location;
	private Node node;
	
	//CONSTRUCTOR
	public Server() throws IOException {
			this.server = new ServerSocket();
			this.nodeList = new ArrayList<Node>();
			this.node = new Node();
	}
	
	public void AcceptConnections() throws IOException{
		this.client = server.accept();
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
		
		for(Node n : nodeList){
			
		}
		
	}

	public List<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
