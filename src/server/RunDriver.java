package server;

import java.io.IOException;

public class RunDriver {

	public RunDriver() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		while(true){
		server.AcceptConnections();
		}
	}
}
