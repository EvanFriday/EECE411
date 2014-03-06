package clientserver;

import java.io.IOException;

public class Propagate implements Runnable{
	private int port;
	private String address;
	private Server server;
	Thread t;
	public Propagate(String address,int port,String threadname, Server server){
		this.address = address;
		this.port = port;
		this.server = server;
		t = new Thread(this, threadname);
	}

	public void run() {
		try {
			server.propagateUpdate(address, port);
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void propagate(){
		System.out.println("Propagating Changes to: " + address);
		t.start();
	}

	

}
