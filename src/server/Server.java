package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tools.Node;

public class Server {
	List<Node> nodeList;
	ServerSocket server;
	
	//CONSTRUCTOR
	public Server() throws IOException {
			server = new ServerSocket();
			nodeList = new ArrayList<Node>();
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
	
	public void PopulateNodeList(){
		FileReader file = new FileReader(file_location);
		BufferedReader in = new BufferedReader(file);
		String line;
		while((line = in.readLine()) != null){
			nodeList.add(new Node(0,line));
		}
			
		
		file.close();
	}
	

}
