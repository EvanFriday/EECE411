package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import tools.*;

public class HandleConnection implements Runnable {
		private ExecutorService executor;
		public Server server;
		public Socket client;

		public HandleConnection(Server server, ExecutorService executor, Socket client){
			this.server = new Server(server);
			this.client = client;
			this.executor = executor;
		}
		@Override
		public void run() {
			try {
				onAccept();	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		public void onAccept() throws IOException{
			Tools.print("Handling Accept");
			Message message = new Message();
			Message prop_message = new Message();
			Message prop_child_message = new Message();
			Message reply = new Message();
			Message local_reply = new Message();
			ErrorCode replyerr = null;
			InputStream in = null;
			OutputStream out = null;
			Node correct_node_for_key;
			Boolean propagate = false;
			boolean debug = true;

			Boolean propagate_ch = false;
			Key k;
			Value v;
			Command c;
			EVpair pair = new EVpair(null,null);
			List<Node> propagate_list = new ArrayList<Node>();
			List<Node> propagate_children = new ArrayList<Node>();
			Map<String,Message> replies = new ConcurrentHashMap<String,Message>();
			Boolean is_local = true; //USE THIS FOR NORMAL USE
			try {
				in = client.getInputStream();
				out = client.getOutputStream();
			} catch (IOException e) {
				Tools.print("Failed to open input or output stream");
			}
			try {
				message.getFrom(in);
			} catch (IOException e) {
				Tools.print("Failed to read message");
			}
			
			c = (Command) message.getLeadByte(); 
			k = new Key(message.getMessageKey());
			v = new Value(message.getMessageValue());
			
			correct_node_for_key = getCorrectNode(k);//USE THIS FOR NORMAL USE
			//correct_node_for_key = this.server.getNode(); //USE THIS FOR SINGLE NODE DEBUG
			if(this.server.getNode().getAlive()){
				if(correct_node_for_key.getAddress() == this.server.getNode().getAddress()){
					is_local = true;
					Tools.print("SERVER: Handling Locally");
					propagate_children.addAll(this.server.getNode().getChildren());
				}
				else{
					propagate_list.add(correct_node_for_key);
					propagate_children.addAll(correct_node_for_key.getChildren());
				}
			}

				Tools.print("SERVER: Receiving = ");
				switch(c) {
				case PUT:
					Tools.print("PUT");
					if(is_local){
						if(this.server.getNode().getKvpairs().size()< 40000){
							this.server.getNode().removeKeyFromKvpairs(k);
							reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
						}	
						propagate = false;
						propagate_ch = true;
					}else{
						prop_message.setLeadByte(Command.PROP_PUT);	
						prop_message.setMessageKey(k);
						prop_message.setMessageValue(v);
						propagate = true;
//						replies.put(this.server.getNode().getAddress().getHostName(),local_reply);
					}
					break;
				case GET:
					Tools.print("GET");
					if(is_local){
						pair = this.server.getNode().getValueFromKvpairs(k);
						reply.setLeadByte(pair.getError());
						if(pair.getError() == ErrorCode.OK){
							reply.setMessageValue(pair.getValue().value);
						}
						propagate = false;
					}
					else{
						prop_message.setLeadByte(Command.PROP_GET);
						prop_message.setMessageKey(k);
						propagate = true;
					}
					break;
				case REMOVE:
					Tools.print("REMOVE");
					if(is_local){
						reply.setLeadByte(this.server.getNode().removeKeyFromKvpairs(k));
						propagate = false;
						propagate_ch = true;
					}
					else{
						prop_message.setLeadByte(Command.PROP_REMOVE);
						prop_message.setMessageKey(k);
						propagate = true;
//						replies.put(this.server.getNode().getAddress().getHostName(),local_reply);
					}
					break;
				case SHUTDOWN:
					Tools.print("SHUTDOWN");
					reply.setLeadByte(ErrorCode.OK);
					this.server.getNode().setAlive(false);
					//Broadcast Death
					propagate = true;
					prop_message.setLeadByte(Command.DEATH);
					propagate_list.addAll(this.server.getNodeList());
					//Don't propagate to Self
					propagate_list.remove(this.server.getNode().getPosition());
					break;
				case PROP_PUT:
					Tools.print("PROP_PUT");
					if(this.server.getNode().getKvpairs().size()< 40000){
						this.server.getNode().removeKeyFromKvpairs(k);
						reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
					}
						
					propagate = false;
					propagate_ch = true;
					break;
				case PROP_GET:
					Tools.print("PROP_GET");
					pair = server.getNode().getValueFromKvpairs(k);
					reply.setLeadByte(pair.getError());
					if(pair.getError() == ErrorCode.OK)
						reply.setMessageValue(pair.getValue());
					propagate = false;
					break;
				case PROP_REMOVE:
					Tools.print("PROP_REMOVE");
					reply.setLeadByte(this.server.getNode().removeKeyFromKvpairs(k));
					propagate = false;
					propagate_ch = true;
					break;
				case REPLICA_PUT:
					for(Node nd : this.server.getNode().getParents()){
						if(this.client.getInetAddress() == nd.getAddress()){
							reply.setLeadByte(nd.addToKvpairs(k, v));
						}
					}
					break;
				case REPLICA_REMOVE:
					for(Node nd : this.server.getNode().getParents()){
						if(this.client.getInetAddress() == nd.getAddress()){
							reply.setLeadByte(nd.removeKeyFromKvpairs(k));
						}
					}
					break;
				case DEATH:
					Tools.print("DEATH");
					reply.setLeadByte(ErrorCode.BAD_COMMAND);
					for(Node n : this.server.getNodeList()){
						if(n.getAddress() == client.getInetAddress()){
							Tools.print(n.getAddress().getHostName()+" is now dead");
							handleDeath(n);
							reply.setLeadByte(ErrorCode.OK);
							break;
						}
					}
				default:
					Tools.print("ERROR");
					reply.setLeadByte(ErrorCode.BAD_COMMAND);
					break;
				}
				if(message.getMessageKey() != null)
					Tools.printByte(message.getMessageKey().key);
				if(c == Command.PUT || c == Command.PROP_PUT)
					Tools.printByte(message.getMessageValue().value);
//				Tools.printByte(k.key);
//				if(v != null)
//					Tools.printByte(v.value);					
			
			if(propagate){
				//Propagate message
				for(Node n : propagate_list){
					if(n.getAlive()){ //Only propagate to a node if it is alive.
						HandlePropagate hp = new HandlePropagate(prop_message,n.getAddress().getHostName());
						FutureTask<Message> ft = new FutureTask<Message>(hp);
						executor.execute(ft);
						int attempt = 0;
						while(attempt<15){
							try{
								if(ft.isDone()){
									reply = ft.get();
									break;
								}
							}catch(InterruptedException | ExecutionException e){
								Tools.print("Exception in Propagation");
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							attempt++;
							if(attempt == 15)
								reply.setLeadByte(ErrorCode.KVSTORE_FAIL); //Timeout
						}
					}
				}
			}
			if(propagate_ch){
					if(c == Command.PUT || c == Command.PROP_PUT){
						prop_child_message.setLeadByte(Command.REPLICA_PUT);
						prop_child_message.setFullMessageKey(k);
						prop_child_message.setFullMessageValue(v);
					}
					else if(c == Command.REMOVE || c == Command.PROP_REMOVE){
						prop_child_message.setLeadByte(Command.REPLICA_REMOVE);
						prop_child_message.setFullMessageKey(k);
					}
				//Propagate message
				for(Node n : propagate_children){
					if(n.getAlive()){ //Only propagate to a node if it is alive.
						Message child_reply = new Message();
						HandlePropagate hp = new HandlePropagate(prop_child_message,n.getAddress().getHostName());
						FutureTask<Message> ft = new FutureTask<Message>(hp);
						executor.execute(ft);
						int attempt = 0;
						while(attempt<15){
							try{
								if(ft.isDone()){
									child_reply = ft.get();
									break;
								}
							}catch(InterruptedException | ExecutionException e){
								Tools.print("Exception in Propagation");
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							attempt++;
							if(attempt == 15)
								child_reply.setLeadByte(ErrorCode.KVSTORE_FAIL); //Timeout
							
							replies.put(n.getAddress().getHostName(), child_reply);
						}
					}
				}
			}

			
				Tools.print("SERVER: Writing Reply");
				Tools.print(reply.getLeadByte().toString());
				if((c == Command.GET || c == Command.PROP_GET) && (reply.getLeadByte() == ErrorCode.OK))
					Tools.printByte(reply.getMessageValue().value);
				reply.sendReplyTo(out);
				Tools.print("SERVER: Closing Socket");
				try {
					this.client.close();
				} catch (IOException e) {
					Tools.print("SERVER: Failed to close Socket");
				}
				
				if(c == Command.SHUTDOWN)
					this.executor.shutdown();

		}

		
		private Node getCorrectNode(Key k){
			// DONE: make getNode Index return node which should hold key
			int a = Arrays.hashCode(k.key);
			a = (a & 0x7FFFFFFF);
			//a = Math.abs(a);
			int position = a % this.server.getNodeList().size();
			Node n = this.server.getNodeList().get(position);
				while(!n.getAlive()){
					n = n.getChild(0);
				}
			return n;
		}
	
		public void sendParentData(){
			for(Map.Entry<Key,Value> pair : this.server.getNode().getKvpairs().entrySet()){
					Message update_message = new Message(Command.REPLICA_PUT,pair.getKey(),pair.getValue());
					Message child_reply = new Message();
					HandlePropagate hp = new HandlePropagate(update_message,this.server.getNode().getChild(2).getAddress().getHostName());
					FutureTask<Message> ft = new FutureTask<Message>(hp);
					executor.execute(ft);
					int attempt = 0;			
					while(attempt <= 3){
						do{
							try{
							if(ft.isDone()){
								child_reply = ft.get();
								attempt++;
								break;
							}
							}catch(InterruptedException | ExecutionException e){
								Tools.print("Propagation Node Completed");
							}
						}
						while(child_reply.getLeadByte()!= ErrorCode.OK);
					}
				}
			}
		
		public void handleDeath(Node n){
			int count = 0;
			this.server.getNodeList().get(n.getPosition()).setAlive(false);
			for(Node nd: this.server.getNode().getParents()){
				if(nd.getAddress() == n.getAddress()){
					/*
					 * This is a parent that is dying, so make it's keys local if we are immediate successor, 
					 * get new parent.
					 * and remove it, 
					 */
					if(count == 0){ //if immediage parent,dies place its replicas keys into our local
						for(Map.Entry<Key,Value> pair : this.server.getNode().getParent(0).getKvpairs().entrySet()){
							this.server.getNode().addToKvpairs(pair.getKey(), pair.getValue());
						}
					}
					else{ //if a different parent dies, place it's replicas keys into the parents child
						for(Map.Entry<Key,Value> pair : this.server.getNode().getParent(count).getKvpairs().entrySet()){
							this.server.getNode().getParent(count-1).addToKvpairs(pair.getKey(), pair.getValue());
						}
					}
					this.server.getNode().addParent(nd.getParent(2-count));
					this.server.getNode().removeParent(nd);
					break;
				}
				count++;
			}
			count = 0;
			Boolean child_dead = false;
			for(Node nd : this.server.getNode().getChildren()){
				if(nd.getAddress() == n.getAddress()){
					//This is one of our children that is dying, so remove it, and get a new child
					this.server.getNode().addChild(nd.getChild(2-count));
					this.server.getNode().removeChild(nd);
					child_dead =true;
					break;
				}
				count++;
			}
			if(child_dead){ //If a child has died, we have to send data to our new child(2)
				try {
					Thread.sleep(100); //Wait, just to make sure that the death command was received and handled by all other nodes.
				} catch (InterruptedException e) {
					Tools.print("Thread sleep failed in handleDeath()");
				}
				sendParentData();
			}
		}
	}
