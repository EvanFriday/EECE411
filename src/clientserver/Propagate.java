/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.util.ArrayList;
import java.util.List;

import clientserver.message.Message;

public class Propagate implements Runnable {
	private String address;
	private Message nodeReply;
	private Server server;
	private Message message;
	private Thread t;
	
	public Propagate(String threadname, Server server,String address ,Message message) {
		this.address = address;
		this.server = server;
		this.message = message;
		this.nodeReply = new Message();
		this.t = new Thread(this, threadname);
	}

	public void run() {
		try {
			nodeReply = server.propagateMessage(this.message, this.address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Message propagate() {
		System.out.println("Propagating Changes to: " + address.toString());
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.nodeReply;
	}
	
}
