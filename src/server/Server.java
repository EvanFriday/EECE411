package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tools.IpTools;
import tools.Key;
import tools.Node;
import tools.Value;

public class Server {
	private List<Node> nodeList;
	private ServerSocket server;
	private Socket client;
	private String file_location = "Test.txt";
	private Node node;
	private ArrayList<Thread> threadpool;
	private int port = 9999;
	public Map<Key, Value> testMap;
	//CONSTRUCTOR
	public Server() throws IOException {
			this.server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(this.port));
			this.nodeList = new ArrayList<Node>(1);
			this.threadpool = new ArrayList<Thread>();
			this.node = new Node();
			this.client = new Socket();
			addThread();
			addThread();
			addThread();
			addThread();
			PopulateNodeList();
	}
	public Server(Server server){
		this.server = server.getServer();
		this.nodeList=server.getNodeList();
		this.threadpool = server.getThreadpool();
		this.node = server.getNode();
		this.client=server.getClient();
	}
	public Server(boolean inTestMode) throws IOException {
		this.server = new ServerSocket();
		server.setSoTimeout(10000);
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(this.port));
		this.nodeList = new ArrayList<Node>(1);
		this.threadpool = new ArrayList<Thread>();
		this.node = new Node();
		this.client = new Socket();
		if(!inTestMode) {
			addThread();
			addThread();
			addThread();
			addThread();
			PopulateNodeList();
		}
}
	
	public void AcceptConnections() throws Exception{
		System.out.println("SERVER: Now Accepting connections on port: "+this.port);
		this.client = server.accept();
		System.out.println("SERVER: Handling connection from: "+ client.getInetAddress().getHostName().toString());
		HandleConnection h = new HandleConnection(this,threadpool.get(0));
		//h.accept();
		h.run();
	}
	public void addThread(){
		this.threadpool.add(new Thread());
		System.out.println("SERVER: Thread "+this.threadpool.size()+" created.");
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
		System.out.println("SERVER: Node List Populated");
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
	public ArrayList<Thread> getThreadpool() {
		return threadpool;
	}
	public void setThreadpool(ArrayList<Thread> threadpool) {
		this.threadpool = threadpool;
	}

}
