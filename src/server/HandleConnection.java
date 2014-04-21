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

import tools.Command;

public class HandleConnection implements Runnable {
		public Thread thread;
		public Server server;
		public Socket client;
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
			Message prop_message = new Message();
			Message reply = new Message();
			Message local_reply = new Message();
			ErrorCode replyerr = null;
			InputStream in = null;
			OutputStream out = null;
			Node correct_node_for_key;
			Boolean propagate = false;
			
			try {
				in = client.getInputStream();
				out = client.getOutputStream();
			} catch (IOException e) {
				Tools.print("Failed to open input or output stream");
			}
			try {
				message.getFrom(in);
			} catch (IOException e) {
				Tools.print("failed to read message");
			}
			
			Command c = (Command) message.getLeadByte(); 
			Key k = new Key(message.getMessageKey());
			Value v = new Value(message.getMessageValue());
			EVpair pair = new EVpair(null,null);
			List<Node> propagate_to_list = new ArrayList<Node>();
			Map<String,Message> replies = new ConcurrentHashMap<String,Message>();
			//correct_node_for_key = getCorrectNode(k); //USE THIS FOR NORMAL USE
			//Boolean is_local = false; //USE THIS FOR NORMAL USE
			correct_node_for_key = this.server.getNode(); //USE THIS FOR SINGLE NODE DEBUG
			Boolean is_local = false; //USE THIS FOR SINGLE NODE DEBUG
			if(correct_node_for_key.getAddress() == this.server.getNode().getAddress()){
				is_local = true;
				propagate_to_list.addAll(this.server.getNode().getChildren());
			}
			else{
				propagate_to_list.add(correct_node_for_key);
				propagate_to_list.addAll(correct_node_for_key.getChildren());
			}

			
			if(is_local) {
				Tools.print("SERVER: Receiving = ");
				switch(c) {
				case PUT:
					Tools.print("PUT");
					local_reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
					break;
				case GET:
					Tools.print("GET");
					pair = server.getNode().getValueFromKvpairs(k);
					local_reply.setLeadByte(pair.getError());
					local_reply.setMessageValue(pair.getValue());
					break;
				case REMOVE:
					Tools.print("RM");
					replyerr = this.server.getNode().removeKeyFromKvpairs(k);
					break;
				default:
					local_reply.setLeadByte(ErrorCode.BAD_COMMAND);
					break;
				}
				Tools.printByte(k.key);
				if(v != null)
					Tools.printByte(v.value);
			}			
			//Set Correct Propagation Command
			switch(c){
				case PUT:
					prop_message.setLeadByte(Command.PROP_PUT);
					replies.put(this.server.getNode().getAddress().getHostName(),local_reply);	
					propagate = true;
					break;
				case GET:
					propagate = false;
					reply = local_reply;
					break;
				case REMOVE:
					prop_message.setLeadByte(Command.PROP_REMOVE);
					replies.put(this.server.getNode().getAddress().getHostName(),local_reply);	
					propagate = true;
				case SHUTDOWN:
					prop_message.setLeadByte(Command.PROP_SHUTDOWN);
					replies.put(this.server.getNode().getAddress().getHostName(),local_reply);	
					propagate = true;
				default:
					propagate = false;
					break;	
			}
			
			if(propagate){
				if(is_local){
					int i = 0;
					for(Node n : propagate_to_list){
						Propagate p = new Propagate(this.server.getThreadpool().get(i),n.getAddress().toString(),prop_message);
					}
					
					
				}
				else{
					
					
					
					
				}
			}
					
			/*
			 * TODO: propagate to the nodes contained in propagate_to_list
			 * new Propagate();
			 */
			if(is_local){
				
				Propagate p1 = new Propagate(this.server,this.server.getThreadpool().get(1),propagate_to_list.get(0).getAddress().toString(),prop_message);
				Propagate p2 = new Propagate(this.server,this.server.getThreadpool().get(2),propagate_to_list.get(1).getAddress().toString(),prop_message);
				p1.run();
				p2.run();
				replies.put(propagate_to_list.get(0).getAddress().getHostName().toString(),p1.getReply());
				replies.put(propagate_to_list.get(1).getAddress().getHostName().toString(), p2.getReply());
				
			}
			else{
				Propagate p0 = new Propagate(this.server,this.server.getThreadpool().get(1),propagate_to_list.get(0).getAddress().toString(),prop_message);
				Propagate p1 = new Propagate(this.server,this.server.getThreadpool().get(2),propagate_to_list.get(1).getAddress().toString(),prop_message);
				Propagate p2 = new Propagate(this.server,this.server.getThreadpool().get(3),propagate_to_list.get(2).getAddress().toString(),prop_message);
				p0.run();
				p1.run();
				p2.run();
				replies.put(propagate_to_list.get(0).getAddress().getHostName().toString(), p0.getReply());
				replies.put(propagate_to_list.get(1).getAddress().getHostName().toString(),p1.getReply());
				replies.put(propagate_to_list.get(2).getAddress().getHostName().toString(), p2.getReply());
			}
			
			for(Entry<String, Message> replymsg : replies.entrySet() ){
				int retrycount = 0;
				while(retrycount < 3){
					Propagate predo = new Propagate(this.server,this.server.getThreadpool().get(0),propagate_to_list.get(0).getAddress().toString(),message);
					predo.run();
					replymsg.setValue(predo.getReply());
					if (replymsg.getValue().getErrorByte() == ErrorCode.OK)
						break;
					retrycount++;
				}
			}
			
			
			try {
				reply.sendReplyTo(out);
			} catch (Exception e) {
				Tools.print("failed to write reply");
			}
		}

		private Node getCorrectNode(Key k) {
			// DONE: make getNode Index return node which should hold key
			int a = k.key.hashCode();
			int position = a % this.server.getNodeList().size();
			return this.server.getNodeList().get(position);
		}
	}
