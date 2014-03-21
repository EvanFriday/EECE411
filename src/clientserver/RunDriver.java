/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

public class RunDriver {

	private static final String file_location = "NODE_IP.txt";
	
	public static void main(String[] args) throws Exception{
		//Create new Server Object
		Server server = new Server(9999);		
		//Read in node list
		server.fileRead(file_location);
		//Begin accepting client connections
		
		while(true){
		if(server.getShutdownStatus()){
			Thread.sleep(5*60*1000);
			server.setShutdownStatus(false);
		}
		else
			server.acceptUpdate();
		}
	}
}
