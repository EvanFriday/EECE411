/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.io.IOException;

public class Propagate implements Runnable{
	
	private String address;
	private Server server;
	Thread t;
	public Propagate(String address,String threadname, Server server){
		this.address = address;
		this.server = server;
		t = new Thread(this, threadname);
	}

	public void run() {
		try {
			server.propagateUpdate(address);
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void propagate(){
		System.out.println("Propagating Changes to: " + address);
		t.start();
	}

	

}
