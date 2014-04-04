package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import tools.*;

import tools.Command;

public class HandleConnection implements Runnable {
	
		private Server server;
		public HandleConnection(Server server, Thread t){
			this.server = server;
			t.start();
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
			Message message = new Message();
			Message reply = new Message();
			
			List<Message> replies = new ArrayList<Message>();
			InputStream in = server.getClient().getInputStream();
			OutputStream out = server.getClient().getOutputStream();
			ObjectOutputStream o_out = new ObjectOutputStream(server.getClient().getOutputStream());
			ObjectInputStream i_in = new ObjectInputStream(server.getClient().getInputStream());
			List<Node> propagate_to_list = new ArrayList<Node>();
			
			Boolean is_local = true; //TODO:once getNodeIndex() returns an actual value set this to false
			Node correct_node_for_key;
			//get the message
			message = Message.getFrom(in);
			Command c = (Command) message.getLeadByte();
			Key k = new Key(message.getMessageKey());
			Value v = new Value(message.getMessageValue());
			
			
			Message local_reply = new Message();
			
			// Find which node this key belongs on
			correct_node_for_key = getCorrectNode(k);
			
			if(correct_node_for_key.getAddress() == server.getNode().getAddress())
				is_local = true;
			else
				propagate_to_list.add(correct_node_for_key);
			propagate_to_list.addAll(correct_node_for_key.getChildren());

			if(is_local) { // Check if this key belongs in this node's keyspace
				switch(c) {
				case PUT:
					local_reply.setLeadByte(this.server.getNode().addToKvpairs(k, v));
					break;
				case GET:
					local_reply.setEVpair(this.server.getNode().getValueFromKvpairs(k));
					break;
				case REMOVE:
					local_reply.setLeadByte(this.server.getNode().removeKeyFromKvpairs(k));
					break;
				default:
					local_reply.setLeadByte(ErrorCode.BAD_COMMAND);
					break;
				}
			}
			replies.add(local_reply);			
			/*
			 * TODO: propagate to the nodes contained in propagate_to_list
			 * new Propagate(); //TODO: Complete propagate class
			 */
			reply = local_reply;
			// Send reply to output stream
			o_out.writeObject(reply.getRaw());
			out.flush();
		}

		private Node getCorrectNode(Key k) { //TODO: Maybe adjust this function hoping for a more even distribution of Keys?
			// DONE: make getNode Index return node which should hold key
			int b = Key.SIZE;
			int position = b % this.server.getNodeList().size();
			return this.server.getNodeList().get(position);
		}
	}
