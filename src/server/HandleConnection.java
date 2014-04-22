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
		public boolean debug = true;
		
		public HandleConnection(Server server, ExecutorService executor, Socket client){
			this.server = new Server(server);
			this.client = client;
			this.executor = executor;
		}

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
			List<Node> propagate_to_list = new ArrayList<Node>();
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
				Tools.print("failed to read message");
			}
			
			c = (Command) message.getLeadByte(); 
			k = new Key(message.getMessageKey());
			v = new Value(message.getMessageValue());
			
			correct_node_for_key = getCorrectNode(k); //USE THIS FOR NORMAL USE
			//correct_node_for_key = this.server.getNode(); //USE THIS FOR SINGLE NODE DEBUG
			if(correct_node_for_key.getAddress() == this.server.getNode().getAddress()){
				is_local = true;
				//propagate_to_list.addAll(this.server.getNode().getChildren());
			}
			else{
				propagate_to_list.add(correct_node_for_key);
				//propagate_to_list.addAll(correct_node_for_key.getChildren());
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
						reply.setMessageValue(pair.getValue());
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
					//TODO: Handle node shutdown
					propagate = false;
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
					reply.setMessageValue(pair.getValue());
					propagate = false;
					break;
				case PROP_REMOVE:
					Tools.print("PROP_REMOVE");
					reply.setLeadByte(this.server.getNode().removeKeyFromKvpairs(k));
					propagate = false;
					break;
				default:
					Tools.print("ERROR");
					reply.setLeadByte(ErrorCode.BAD_COMMAND);
					break;
				}
				Tools.printByte(message.getMessageKey().key);
				if(c == Command.PUT || c == Command.PROP_PUT)
				Tools.printByte(message.getMessageValue().value);
//				Tools.printByte(k.key);
//				if(v != null)
//					Tools.printByte(v.value);					
			
			if(propagate){
				//Propagate message
				HandlePropagate hp = new HandlePropagate(prop_message,correct_node_for_key.getAddress().getHostName());
				FutureTask<Message> ft = new FutureTask<Message>(hp);
				executor.execute(ft);
				while(true){
					try{
						if(ft.isDone()){
							reply = ft.get();
							break;
						}
					}catch(InterruptedException | ExecutionException e){
						e.printStackTrace();
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

			try {
				Tools.print("Writing Reply");
				reply.sendReplyTo(out);
				Tools.print("Closing Socket");
				this.client.close();

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
