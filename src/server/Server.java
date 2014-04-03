package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import tools.NodeObject;

public class Server {
	ArrayList<NodeObject> nodeList;
	ServerSocket server;
	
	//CONSTRUCTOR
	public Server() throws IOException {
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
	
	public void PopulateNodeList(){
		FileReader file = new FileReader(file_location);
		BufferedReader in = new BufferedReader(file);
		nodeList = new ArrayList<NodeObject>();
		String line;
		while((line = in.readLine()) != null){
			nodeList.add(0,line);
		}
			
		
		file.close();
	}
	

}
