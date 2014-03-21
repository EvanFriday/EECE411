/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.util.List;

import clientserver.message.Message;

public class Propagate implements Runnable {
	private List<String> addressList;
	private Server server;
	private Message message;
	private Thread t;
	
	public Propagate(String threadname, Server server,List<String> addressList ,Message message) {
		this.addressList = addressList;
		this.server = server;
		this.message = message;
		this.t = new Thread(this, threadname);
	}

	public void run() {
		try {
			server.propagateMessage(this.message, this.addressList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void propagate() {
		System.out.println("Propagating Changes to: " + addressList.toString());
		t.start();
	}
}
