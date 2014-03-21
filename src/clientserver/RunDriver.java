/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver;

import java.io.IOException;

import clientserver.server.AbstractServer;
import clientserver.server.GossipServer;

public class RunDriver {
	private static final String file_location = "NODE_IP.txt";
	
	public static void main(String[] args) throws IOException {
		AbstractServer server = new GossipServer(9999, file_location);
		server.run();
	}
}