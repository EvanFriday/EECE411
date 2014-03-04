package clientserver;
import java.io.IOException;

import clientserver.Server;


public class RunDriver {
	
	final static int WAITING_FOR_CONNECTION = 0;
	final static int ACCEPTING_DATA = 1;
	final static int PROPAGATING_DATA = 2;
	public static int STATUS = WAITING_FOR_CONNECTION;
	
	
	public static void main(String[] args) throws OutOfMemoryError, IOException {
		while(true){
			switch(STATUS){
			case WAITING_FOR_CONNECTION:
				//wait for connection
				//connection ready ->
				STATUS=ACCEPTING_DATA;
				
			case ACCEPTING_DATA:
				//read in new data
				Server.acceptUpdate();
				STATUS=PROPAGATING_DATA;
				
			case PROPAGATING_DATA:
				//Connect to other nodes, and send data.
				Server.propagateUpdate();
				STATUS=WAITING_FOR_CONNECTION;
				
			default:
				System.out.println("somehow we are no in the state machine...\n");
			}
	}
	}

}
