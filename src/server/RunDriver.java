package server;

import java.io.IOException;

public class RunDriver {

	public RunDriver() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args){
		Server server = null;
		try {
			server = new Server();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try{
			while(true){
			server.AcceptConnections();
			}
		}catch (Exception e){
			try {
				server.getServer().close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally{
			try {
				server.getServer().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
