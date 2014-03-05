package clientserver;
import java.io.IOException;

import clientserver.Server;


public class RunDriver {
	
	final static int WAITING_FOR_CONNECTION = 0;
	final static int ACCEPTING_DATA = 1;
	final static int PROPAGATING_DATA = 2;
	final static int port = 9999;
	
	public static int STATUS = WAITING_FOR_CONNECTION;
	public static int address1,address2,address3;
	
	public static void main(String[] args) throws OutOfMemoryError, IOException {
		while(true){
			switch(STATUS){
			case WAITING_FOR_CONNECTION:
				//wait for connection
				//connection ready ->
				STATUS=ACCEPTING_DATA;
				break;
			case ACCEPTING_DATA:
				//read in new data
				Server.acceptUpdate();
				STATUS=PROPAGATING_DATA;
				break;
			case PROPAGATING_DATA:
				//Connect to other nodes, and send data.
				
				Thread t1 = new Thread(new Runnable()
				{
					public void run(){
						Server.propagateUpdate(address1, port);
					}
				
				});
				Thread t2 = new Thread(new Runnable()
				{
					public void run(){
						Server.propagateUpdate(address2, port);
					}
				
				});
				Thread t3 = new Thread(new Runnable()
				{
					public void run(){
						Server.propagateUpdate(address3, port);
					}
				
				});
				t1.start();
				t2.start();
				t3.start();
				STATUS=WAITING_FOR_CONNECTION;
				break;
			default:
				System.out.println("somehow we are no in the state machine...\n");
			}
		}
	}

}
