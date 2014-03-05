package clientserver;
import java.io.IOException;

import clientserver.Server;


public class RunDriver {
	
	final static int ACCEPTING_DATA = 0;
	final static int PROPAGATING_DATA = 1;
	
	
	public static int STATUS = ACCEPTING_DATA;
	
	
	public static void main(String[] args) throws OutOfMemoryError, IOException {
		while(true){
			switch(STATUS){
			case ACCEPTING_DATA:
				//read in new data
				Server.acceptUpdate();
				STATUS=PROPAGATING_DATA;
				break;
			case PROPAGATING_DATA:
				//Connect to other nodes, and send data.
				Server.propagate();
				STATUS=ACCEPTING_DATA;
				break;
			default:
				System.out.println("somehow we are no in the state machine...\n");
			}
		}
	}

}
