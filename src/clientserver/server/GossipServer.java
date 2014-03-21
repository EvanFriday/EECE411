/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.server;

import java.io.IOException;

import clientserver.message.Command;
import clientserver.message.Message;

public class GossipServer extends AbstractServer {
	protected GossipServer(int port, String filename) throws IOException {
		super(port);
	}

	protected Command action() throws IOException {
		Message toPropogate = this.acceptUpdate();
		
		if (toPropogate.getLeadByte() != Command.SHUTDOWN) {
			this.propagateMessage(toPropogate);
		}
		
		return (Command) toPropogate.getLeadByte();
	}
	
	private void propagateMessage(Message toSend) {
		// TODO: Implement propagation
	}

	protected void shutdown() {
		// TODO Add shutdown code
	}
}
