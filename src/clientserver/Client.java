package clientserver;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket clientSocket;
	public Client(int port, String serverName) throws UnknownHostException, IOException
	{
		clientSocket = new Socket(serverName, port);
	}
	public static void propagateUpdate() throws IOException, OutOfMemoryError{
		//TODO: Implement Pushing features
	}


}

