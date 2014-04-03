package node;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeData {
	String PublicIP;
	boolean is_alive;
	
	public NodeData(String IP, boolean status) {
		this.PublicIP = IP;
		this.is_alive = status;
	}
	
	public NodeData(String IP) {
		new NodeData(IP, true);
	}

	public NodeData() throws UnknownHostException {
		new NodeData(InetAddress.getLocalHost().toString(), true);
	}
}
