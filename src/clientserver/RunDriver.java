/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.io.IOException;

public class RunDriver {

	private static final String file_location = "NODE_IP.txt";
	
	public static void main(String[] args) throws Exception{
		Server server = new Server(9999);		
		server.fileRead(file_location);
		server.acceptUpdate();
		
		
	}
}
