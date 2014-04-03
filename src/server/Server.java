package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import tools.IpTools;
import tools.Node;

public class Server {
	private List<Node> nodeList;
	private ServerSocket server;
	private Socket client;
	private String file_location = "NODE_IP.txt";
	private Node node;
	
	//CONSTRUCTOR
	public Server() throws IOException {
			this.server = new ServerSocket();
			this.nodeList = new ArrayList<Node>(1);
			PopulateNodeList();
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
		int index=1;
		String line;
		//Read in node addresses, and if they are dead already, set their status to dead
		while((line = in.readLine())!= null){
			
			InetAddress address = InetAddress.getByName(line);
			Node n;
			if(address.isReachable(1000)){ // Ping with a one second timeout
				n = new Node(index,address,true);
			}
			else{
				n = new Node(index,address,false); // Dead node
			}
			this.nodeList.add(n);
			index++;
		}
		file.close();
		
		//Get the last two nodes in the list (for circular roll around)
		int last = this.nodeList.size()-1;
		int second_last = this.nodeList.size()-2;
		
		//Give each node two children, who will hold hold replicas
		for(Node n : nodeList){
			if(nodeList.indexOf(n) == last){
				n.addChild(this.nodeList.get(0));
				n.addChild(this.nodeList.get(1));				
			}
			else if(nodeList.indexOf(n) == second_last){
				n.addChild(this.nodeList.get(nodeList.indexOf(n)+1));
				n.addChild(this.nodeList.get(0));
			}
			else{
				n.addChild(this.nodeList.get(nodeList.indexOf(n)+1));
				n.addChild(this.nodeList.get(nodeList.indexOf(n)+2));
			}
			
			if(n.getAddress() == IpTools.getInet()){
				this.node = new Node(n);
			}
			//System.out.println("Node number: "+n.getPosition()+" Address: "+n.getAddress().toString()+" Has children: "+n.getChild(0).getAddress().toString()+", "+n.getChild(1).getAddress().toString());
			
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
