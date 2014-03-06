package clientserver;

import java.io.IOException;

public class Propagate implements Runnable{
	private int port;
	private String address;
	Thread t;
	public Propagate(String address,int port,String threadname){
		this.address = address;
		this.port = port;
		t = new Thread(this, threadname);
		t.start();

	}
	
	public void run() {
		try {
			Server.propagateUpdate(address, port);
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
