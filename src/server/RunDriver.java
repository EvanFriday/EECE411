package server;

import java.io.IOException;

public class RunDriver {

	public RunDriver() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.AcceptConnections();
	}

}
