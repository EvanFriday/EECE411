/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver;

import clientserver.message.Message;

public class Propagate implements Runnable {
	private Message message, reply;
	private String address;
	private int port;
	private Thread thread;
	
	public Propagate(Message m, String address, int port) {
		this.address = address;
		this.port = port;
		this.thread = new Thread(this);
	}

	public void run() {
		try {
			this.reply = this.message.sendTo(address, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Message propagate() {
		System.out.println("Propagating Changes to: " + address);
		this.thread.start();
		return reply;
	}
}