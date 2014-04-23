package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
			Key k;
			Value v;
			Command c;
			EVpair pair = new EVpair(null,null);
			List<Node> propagate_list = new ArrayList<Node>();
			Map<String,Message> replies = new ConcurrentHashMap<String,Message>();
			Boolean is_local = false; //USE THIS FOR NORMAL USE
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
			if(correct_node_for_key.getAddress() == this.server.getNode().getAddress()){
					is_local = true;
					Tools.print("SERVER: Handling Locally");
				//propagate_list.addAll(this.server.getNode().getChildren());
			}
			else{
				
					propagate_list.add(correct_node_for_key);
				
				
				//propagate_list.addAll(correct_node_for_key.getChildren());
			}

				Tools.print("SERVER: Receiving = ");
				switch(c) {
				case PUT:
					Tools.print("PUT");
					if(is_local){
						if(this.server.getNode().getKvpairs().size()< 40000)
						reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
						propagate = false;
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
						pair = server.getNode().getValueFromKvpairs(k);
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
					if(this.server.getNode().getKvpairs().size()< 40000)
						reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
					propagate = false;
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
					break;
				case DEATH:
					Tools.print("DEATH");
					reply.setLeadByte(ErrorCode.BAD_COMMAND);
					for(Node n : this.server.getNodeList()){
						if(n.getAddress() == client.getInetAddress()){
							Tools.print(n.getAddress().getHostName()+" is now dead");
							n.setAlive(false);
							reply.setLeadByte(ErrorCode.OK);
							break;
						}
						if(n.getAddress() == this.server.getNode().getAddress()){
							this.server.getNode().setAlive(false);
							reply.setLeadByte(ErrorCode.OK);
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
					else{
						//Broadcast to correct Node. Probably should be a recursive function
					}
				}
				
				//Retry Attempt Code
//				for(Entry<String, Message> replymsg : replies.entrySet() ){
//					int retrycount = 0;
//					while(retrycount < 3){
//						//Re-propagate command until OK, or 3 attempts
//						if (replymsg.getValue().getErrorByte() == ErrorCode.OK)
//							break;
//						retrycount++;
//					}
//				}
				
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
			Tools.print(a);
			Tools.print(this.server.getNodeList().size());
			int position = a % this.server.getNodeList().size();
			return this.server.getNodeList().get(position);
		}

		
	}
