/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver;

public class Propagate implements Runnable {
	private String address;
	private Server server;
	private Thread t;
	
	public Propagate(String address, String threadname, Server server) {
		this.address = address;
		this.server = server;
		this.t = new Thread(this, threadname);
	}

	public void run() {
		try {
			server.propagateUpdate(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void propagate() {
		System.out.println("Propagating Changes to: " + address);
		t.start();
	}
}