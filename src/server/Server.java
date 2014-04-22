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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import tools.IpTools;
import tools.Key;
import tools.Node;
import tools.Tools;
import tools.Value;

public class Server {
	private List<Node> nodeList;
	private ServerSocket server;
	public Socket client;
	private String file_location = "Test.txt";
	private Node node;
	private ExecutorService executor;
	private int NumThreads = 5;
	private int port = 9999;
	public Map<Key, Value> testMap;
	//CONSTRUCTOR
	public Server() throws IOException {
		this.server = new ServerSocket();
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(this.port));
		this.nodeList = new ArrayList<Node>(1);
		this.executor = Executors.newFixedThreadPool(NumThreads);
		this.node = new Node();
		this.client = new Socket();
		PopulateNodeList();
	}
	public Server(Server server){
		this.server = server.getServer();
		this.nodeList=server.getNodeList();
		
		this.node = server.getNode();
		this.client=server.getClient();
	}
	public Server(boolean inTestMode) throws IOException {
		this.server = new ServerSocket();
		server.setSoTimeout(10000);
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(this.port));
		this.nodeList = new ArrayList<Node>(1);
		
		this.node = new Node();
		this.client = new Socket();
		if(!inTestMode) {
			PopulateNodeList();
		}
	}
	
	public void AcceptConnections(){
		System.out.println("SERVER: Now Accepting connections on port: "+this.port);
		try {
			this.client = server.accept();
			//System.out.println("SERVER: Handling connection from: "+ client.getInetAddress().getHostName().toString());
			HandleConnection hc = new HandleConnection(this,this.executor,this.client);
			FutureTask<Integer> ft = new FutureTask<Integer>(hc,null);
			executor.submit(ft);
		} catch (IOException e) {
			Tools.print("SERVER: Failed to accept connection from: "+client.getInetAddress().getHostName().toString());
		}

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

	public ExecutorService getExecutor() {
		return executor;
	}
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
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
		return this.node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
