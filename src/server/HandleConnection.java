package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HandleConnection implements Runnable {
		private Server server;
		public HandleConnection(Server server){
			this.server = server;
			new Thread().start();
		}

		public void run() {
			try {
				onAccept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void onAccept() throws IOException{
			InputStream in = server.getClient().getInputStream();
			OutputStream out = server.getClient().getOutputStream();
			/*
			 * TODO: Handle incoming request, send reply
			 * 
			 * - To propagate to the replicas (children) simply call propagate for now, and handle return values
			 * 	(ie. did the keys set properly? did the keys remove?)
			 * - Create getKey,getValue, setKey, setValue for kvpairs in Node 
			 * ^ Created put, get, and remove methods in Node
			 * 	(is this needed? would these functions need to use for loop iteration over each byte?)
			 * - Reply
			 * 
			 */
			new Propagate();
		}
	}
