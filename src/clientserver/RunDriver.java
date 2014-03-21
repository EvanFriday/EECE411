/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.io.IOException;

public class RunDriver {
	private static final int ACCEPTING_DATA = 0;
	private static final int PROPAGATING_DATA = 1;
	private static final int RECEIVE_ONLY = 0;
	private static final int GOSSIP = 1;

	private static final String file_location = "NODE_IP.txt";
	
	public static void main(String[] args) throws OutOfMemoryError, Exception{
		Server server = new Server(9999);
		int status = ACCEPTING_DATA;
		int mode;
		
		server.fileRead(file_location);
		
		//TODO: Call fileReader(), to populate addressList
		//server.fileReader(file_name);
		
		if (args.length != 0) {
			mode = (Integer) Integer.parseInt(args[0]);
		} else {
			mode = RECEIVE_ONLY;
		}
		
		while(mode == RECEIVE_ONLY || mode == GOSSIP) {
			switch (status) {
			case ACCEPTING_DATA:
				// Read in new data
				System.out.println("Waiting for update...");
				server.acceptUpdate();
				
				// Depending on mode specified, either continuously accept data, or propagate as well
				switch (mode) {
				case RECEIVE_ONLY:
					status = ACCEPTING_DATA;
					break;
				case GOSSIP:
					status=PROPAGATING_DATA;
					break;
				default:
					System.out.println("Invalid Mode.");
					break;
				}
				break;
			case PROPAGATING_DATA:
				// Connect to other nodes, and send data.
				server.propagate();
				status=ACCEPTING_DATA;
				break;
			default:
				System.out.println("No longer in the State Machine.");
				break;
			}
		}
	}
	
	

}
