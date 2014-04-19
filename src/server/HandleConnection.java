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

import tools.Command;

public class HandleConnection implements Runnable {
		public Thread thread;
		public Server server;
		public HandleConnection(Server s, Thread t){
			this.server = s;
			this.server.setNode(s.getNode());
			this.thread = t;
		}

		public void run() {
			try {
				onAccept();
				accept();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void accept(){
			this.thread.start();
		}
		public void onAccept() throws Exception{
			Message message = new Message();
			Message reply = new Message();
			Message local_reply = new Message();
			
			InputStream in = server.getClient().getInputStream();
			OutputStream out = server.getClient().getOutputStream();
		
			List<Node> propagate_to_list = new ArrayList<Node>();
			Map<String,Message> replies = new ConcurrentHashMap<String,Message>();
			
			Boolean is_local = true; //TODO:once getNodeIndex() returns an actual value set this to false
			Node correct_node_for_key;
			//get the message
			message = Message.getFrom(in);
			Command c = (Command) message.getLeadByte();
			Key k = message.getFullMessageKey();
			Value v = message.getFullMessageValue();
			
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
			
			System.out.println("SERVER: Handleconnection receiving message values = " + c + ", "+ k.hashCode()+ ", "+v.hashCode());
			System.out.flush();
			if(is_local) { // Check if this key belongs in this node's keyspace
				switch(c) {
				case PUT:
					System.out.println("SERVER: Receiving PUT command");
					local_reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
					break;
				case GET:
					System.out.println("SERVER: Receiving GET command");
					local_reply.setEVpair(this.server.getNode().getValueFromKvpairs(k));
					break;
				case REMOVE:
					System.out.println("SERVER: Receiving REMOVE command");
					local_reply.setLeadByte(this.server.getNode().removeKeyFromKvpairs(k));
					break;
				default:
					local_reply.setLeadByte(ErrorCode.BAD_COMMAND);
					break;
				}
			}
			//replies.add(local_reply);			
			/*
			 * TODO: propagate to the nodes contained in propagate_to_list
			 * new Propagate();
			 */
			if(is_local){
				/*
				Propagate p1 = new Propagate(this.server,this.server.getThreadpool().get(1),propagate_to_list.get(0).getAddress().toString(),message);
				Propagate p2 = new Propagate(this.server,this.server.getThreadpool().get(2),propagate_to_list.get(1).getAddress().toString(),message);
				replies.put(propagate_to_list.get(0).getAddress().getHostName().toString(), p1.propagate());
				replies.put(propagate_to_list.get(1).getAddress().getHostName().toString(), p2.propagate());
				*/
			}
			else{
				Propagate p0 = new Propagate(this.server,this.server.getThreadpool().get(1),propagate_to_list.get(0).getAddress().toString(),message);
				Propagate p1 = new Propagate(this.server,this.server.getThreadpool().get(2),propagate_to_list.get(1).getAddress().toString(),message);
				Propagate p2 = new Propagate(this.server,this.server.getThreadpool().get(3),propagate_to_list.get(2).getAddress().toString(),message);
				replies.put(propagate_to_list.get(0).getAddress().getHostName().toString(), p0.propagate());
				replies.put(propagate_to_list.get(1).getAddress().getHostName().toString(), p1.propagate());
				replies.put(propagate_to_list.get(2).getAddress().getHostName().toString(), p2.propagate());
			}
			for(Entry<String, Message> replymsg : replies.entrySet() ){
				int retrycount = 0;
				while(replymsg.getValue().getErrorByte() != ErrorCode.OK || retrycount > 3){
					Propagate predo = new Propagate(this.server,this.server.getThreadpool().get(0),propagate_to_list.get(0).getAddress().toString(),message);
					replymsg.setValue(predo.propagate());
					retrycount++;
				}
				
			}
			
			
			reply = local_reply;
			local_reply.sendReplyTo(out);
			// Send reply to output stream
			//o_out.writeObject(local_reply.getRaw());
			out.flush();
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
