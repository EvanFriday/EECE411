package main;

import java.io.IOException;

import main.server.Server;

/**
 * Entry point for the application.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public abstract class RunDriver {
	private static final int port = 9999;
	
	public static void main(String[] args) {
		Server server;
		try {
			server = new Server(port);
			server.run();
		} catch (IOException e) {
			System.out.println("Error creating server.");
		}
		
	}
}