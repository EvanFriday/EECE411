/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver;

import clientserver.message.Command;
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
		
		switch ((Command) m.getLeadByte()) {
		case PUT:
			m.setLeadByte(Command.PROP_PUT);
			break;
		case GET:
			m.setLeadByte(Command.PROP_GET);
			break;
		case REMOVE:
			m.setLeadByte(Command.PROP_REMOVE);
			break;
		default:
			break;
		}
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