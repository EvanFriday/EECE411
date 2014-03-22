/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.server;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import clientserver.Propagate;
import clientserver.ip.NodeList;
import clientserver.message.Command;
import clientserver.message.Message;

public class GossipServer extends AbstractServer {
	public GossipServer(int port, String fileName) throws IOException {
		super(port);
		this.ipList = new NodeList(new FileReader(fileName));
	}

	protected Command action() throws IOException {
		Message toPropogate = this.acceptUpdate();
		
		if (toPropogate.getLeadByte() != Command.SHUTDOWN) {
			this.propagateUpdate(toPropogate);
		}
		
		return (Command) toPropogate.getLeadByte();
	}
	
	private void propagateUpdate(Message toSend) {
		List<String> propList = this.ipList.getKeySpace(toSend.getKey());
		Map<String, Message> replyList = new ConcurrentHashMap<String, Message>();
				
		for (String node : propList) {
			if (node != this.myAddress) {
				Message reply = new Propagate(toSend, node, this.port).propagate();
				replyList.put(node, reply);
			}
		}
		
		// TODO: Analyze replies
	}

	protected void shutdown() {
		// TODO Add shutdown code
	}
}
