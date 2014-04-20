package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tools.*;

public class HandleConnection implements Runnable {
		public Thread thread;
		public Server server;
		public boolean debug_mode = true;
		public HandleConnection(Server server, Thread t){
			this.server = new Server(server);
			this.server.setNode(server.getNode());
			this.thread = t;
		}

		public void run() {
			try {
				while(true) {
				if(debug_mode) System.out.println("[debug] SERVER: Calling onAccept");
				onAccept();
				}
				//if(debug_mode) System.out.println("[debug] SERVER: Calling accept");
				//accept();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void accept(){
			this.thread.start();
		}
		public void onAccept() throws Exception{
			//Message message = new Message();
			//Message reply = new Message();
			byte[] message = new byte[1+32+1024];
			byte[] reply = new byte[1+32+1024];
			ErrorCode replyerr = null;
			Message local_reply = new Message();
			InputStream in = server.getClient().getInputStream();
			OutputStream out = server.getClient().getOutputStream();
		
			List<Node> propagate_to_list = new ArrayList<Node>();
			Map<String,Message> replies = new ConcurrentHashMap<String,Message>();
			
			Boolean is_local = true; //TODO:once getNodeIndex() returns an actual value set this to false
			Node correct_node_for_key;
			
			if(debug_mode) System.out.println("[debug] SERVER: About to read message from IS");
			in.read(message); // When receiving the GET command it's not getting past this line
			if(debug_mode) System.out.println("[debug] SERVER: Done reading message from IS");
			Command c = null; 
			Key k = new Key();
			Value v = new Value();
			Value replyvalue = new Value();
			byte[] replyv = new byte[1024];
			EVpair pair = new EVpair(null,null);
			for(int i=0;i<message.length;i++){
				if(i==0)
					c = Command.getCommand(message[i]);
				else if(1<=i && i<33)
					k.setValue(message[i], i-1);
				else
					v.setValue(message[i], i-1-32);
			}

			//correct_node_for_key = getCorrectNode(k); //USE THIS FOR NOMAL USE
			correct_node_for_key = this.server.getNode(); //USE THIS FOR SINGLE NODE DEBUG
			if(correct_node_for_key.getAddress() == this.server.getNode().getAddress()){
				is_local = true;
				propagate_to_list.addAll(this.server.getNode().getChildren());
			}
			else{
				propagate_to_list.add(correct_node_for_key);
				propagate_to_list.addAll(correct_node_for_key.getChildren());
			}
			
			Tools.print("SERVER: Receiving = ");
			if(is_local) { // Check if this key belongs in this node's keyspace
				switch(c) {
				case PUT:
					Tools.print("PUT");
					if(this.server.getNode().kvpairs.size() > 40000) {
						replyerr = ErrorCode.OUT_OF_SPACE;
						break;
					}
					else {
						this.server.getNode().kvpairs.put(k, v);
						if(this.server.getNode().kvpairs.get(k) != v)
							replyerr = ErrorCode.KVSTORE_FAIL;
						else
							replyerr = ErrorCode.OK;
					}
					//Tools.print("Pair Added:");
					//Tools.printByte(server.getNode().getValueFromKvpairs(k).getValue().value);
					pair = new EVpair(replyerr, null);
					break;
				case GET:
					Tools.print("GET");
					replyvalue = this.server.getNode().kvpairs.get(k);
					if(replyvalue == null)
						replyerr = ErrorCode.KEY_DNE;
					else
						replyerr = ErrorCode.OK;
					pair = new EVpair(replyerr, replyvalue);
					break;
				case REMOVE:
					Tools.print("RM");
					Value tempval = this.server.getNode().kvpairs.remove(k);
					if(tempval == null)
						replyerr = ErrorCode.KEY_DNE;
					else if(this.server.getNode().kvpairs.containsKey(k))
						replyerr = ErrorCode.KVSTORE_FAIL;
					else
						replyerr = ErrorCode.OK;
					pair = new EVpair(replyerr, null);
					break;
				default:
					replyerr = ErrorCode.BAD_COMMAND;
					pair = new EVpair(replyerr, null);
					break;
				}
			}
			Tools.printByte(k.key);
			Tools.printByte(v.value);
			for(int i=0;i<reply.length;i++){
				if(i==0)
					reply[i] = replyerr.getByte();
				else if(1<=i && i<33)
					reply[i] = k.getValue(i-1);
				else
					reply[i] = replyv[i-32-1];
			}
			
			Tools.print("SERVER: Sending = ");
			Tools.printEVpair(pair);
			out.write(reply);
			//replies.add(local_reply);			
			/*
			 * TODO: propagate to the nodes contained in propagate_to_list
			 * new Propagate();
			 */
//			if(is_local){
//				/*
//				Propagate p1 = new Propagate(this.server,this.server.getThreadpool().get(1),propagate_to_list.get(0).getAddress().toString(),message);
//				Propagate p2 = new Propagate(this.server,this.server.getThreadpool().get(2),propagate_to_list.get(1).getAddress().toString(),message);
//				replies.put(propagate_to_list.get(0).getAddress().getHostName().toString(), p1.propagate());
//				replies.put(propagate_to_list.get(1).getAddress().getHostName().toString(), p2.propagate());
//				*/
//			}
//			else{
//				Propagate p0 = new Propagate(this.server,this.server.getThreadpool().get(1),propagate_to_list.get(0).getAddress().toString(),message);
//				Propagate p1 = new Propagate(this.server,this.server.getThreadpool().get(2),propagate_to_list.get(1).getAddress().toString(),message);
//				Propagate p2 = new Propagate(this.server,this.server.getThreadpool().get(3),propagate_to_list.get(2).getAddress().toString(),message);
//				replies.put(propagate_to_list.get(0).getAddress().getHostName().toString(), p0.propagate());
//				replies.put(propagate_to_list.get(1).getAddress().getHostName().toString(), p1.propagate());
//				replies.put(propagate_to_list.get(2).getAddress().getHostName().toString(), p2.propagate());
//			}
//			for(Entry<String, Message> replymsg : replies.entrySet() ){
//				int retrycount = 0;
//				while(replymsg.getValue().getErrorByte() != ErrorCode.OK || retrycount > 3){
//					Propagate predo = new Propagate(this.server,this.server.getThreadpool().get(0),propagate_to_list.get(0).getAddress().toString(),message);
//					replymsg.setValue(predo.propagate());
//					retrycount++;
//				}
				
//			}
			
			
//			reply = local_reply;
//			reply.sendReplyTo(out);
			// Send reply to output stream
			//o_out.writeObject(local_reply.getRaw());
			//out.flush();
			if(debug_mode) System.out.println("[debug] SERVER: Exiting onAccept()");
		}

		private Node getCorrectNode(Key k) {
			// DONE: make getNode Index return node which should hold key
			byte[] b = new byte[Key.SIZE];
			for(int i=0; i<Key.SIZE; i++) {
				b[i] = k.getValue(i);
			}
			int a = b.hashCode();
			int position = a % this.server.getNodeList().size();
			return this.server.getNodeList().get(position);
		}
	}
