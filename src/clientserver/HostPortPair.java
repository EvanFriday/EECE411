package clientserver;

public class HostPortPair {
	String host;
	int port;
	
	// Constructor
	HostPortPair(String h, int p) {
		host = h;
		port = p;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
