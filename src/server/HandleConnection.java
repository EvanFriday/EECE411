package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tools.*;

public class HandleConnection implements Runnable {
		public Thread thread;
		public Server server;
		public Socket client;
		public boolean debug = true;
		public HandleConnection(Server server, Thread t, Socket client){
			this.server = new Server(server);
			this.client = client;
			this.thread = t;
		}

		public void run() {
			try {
				onAccept();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void accept(){
			this.thread.start();
		}
		public void onAccept(){
			Message message = new Message();
//			byte[] message = new byte[1+32+1024];
//			byte[] reply = new byte[1+1024];
			ErrorCode replyerr = null;
			Message local_reply = null;
			InputStream in = null;
			OutputStream out = null;
			Boolean is_local = true; //TODO:once getNodeIndex() returns an actual value set this to false
			Node correct_node_for_key;
			
			try {
				in = client.getInputStream();
				out = client.getOutputStream();
			} catch (IOException e) {
				Tools.print("Failed to open input or output stream");
			}
			try {
//				in.read(message);
				if(debug) System.out.println("[debug] SERVER: onAccept - Calling getFrom");
				message.getFrom(in);
			} catch (IOException e) {
				Tools.print("failed to read message");
			}
//			message = new Message(message);
			Command c = null; 
			Key k = new Key(message.getMessageKey());
			Value v = new Value(message.getMessageValue());
//			byte[] replyv = new byte[1024];
			EVpair pair = new EVpair(null,null);
//			for(int i=0;i<message.length;i++){
//				if(i==0)
//					c = Command.getCommand(message[i]);
//				else if(1<=i && i<33)
//					k.setValue(message[i], i-1);
//				else
//					v.setValue(message[i], i-1-32);
//			}
			
			c = (Command) message.getLeadByte();
			List<Node> propagate_to_list = new ArrayList<Node>();
			Map<String,Message> replies = new ConcurrentHashMap<String,Message>();
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

			if(is_local) {
				switch(c) {
				case PUT:
					Tools.print("SERVER: Receiving = PUT");
//					replyerr = this.server.getNode().addToKvpairs(k, v);
					local_reply = new Message(this.server.getNode().addToKvpairs(k, v));
					break;
				case GET:
					Tools.print("SERVER: Receiving = GET");
					pair = server.getNode().getValueFromKvpairs(k);
//					replyerr = pair.getError();
					local_reply = new Message(pair.getError(), pair.getValue());
					//local_reply.setLeadByte(pair.getError());
//					if(pair.getValue()!= null){
//						replyv = pair.getValue().value;
						//local_reply.setMessageValue(pair.getValue());
//					}
					break;
				case REMOVE:
					Tools.print("SERVER: Receiving = RM");
					ErrorCode ec = this.server.getNode().removeKeyFromKvpairs(k);
					byte[] tempBA = new byte[1];
					tempBA[0] = ec.getByte();
					local_reply = new Message(this.server.getNode().removeKeyFromKvpairs(k));
					//replyerr = this.server.getNode().removeKeyFromKvpairs(k);
					break;
				default:
					//local_reply.setLeadByte(ErrorCode.BAD_COMMAND);
					local_reply = new Message(ErrorCode.BAD_COMMAND);
					break;
				}
			}
			Tools.printByte(k.key);
			if(v != null)
				Tools.printByte(v.value);
//			for(int i=0;i<reply.length;i++){
//				if(i==0)
//					reply[i] = replyerr.getByte();
//				else if(replyv!=null)
//					reply[i] = replyv[i-1];
//			}
//			Tools.print("message value = ");
			//Tools.printByte(local_reply.getMessageValue().value);
			try {
				//out.write(reply);
				if(debug) Tools.print("[debug] SERVER: onAccept - Sending reply: "+local_reply.getLeadByte());
				//if(debug) Tools.print("[debug] SERVER: onAccept - Sending reply: "+local_reply.getFullMessageKey().key);
				//if(debug) Tools.print("[debug] SERVER: onAccept - Sending reply: "+local_reply.getFullMessageValue().value);
				local_reply.sendReplyTo(out);
			} catch (Exception e) {
				Tools.print("failed to write reply");
			}
			
			
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
