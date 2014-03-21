/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-21
 * EECE 411 Project Phase 3 Server
 */

package clientserver.server;

import java.io.IOException;

import clientserver.message.Command;

public class ReadOnlyServer extends AbstractServer {
	public ReadOnlyServer(int port) throws IOException {
		super(port);
	}

	protected Command action() throws IOException {
		return (Command) this.acceptUpdate().getLeadByte();
	}

	protected void shutdown() {
		// TODO Add shutdown code
	}
}
