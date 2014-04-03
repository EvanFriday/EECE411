package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import tools.*;

import tools.Command;

public class HandleConnection implements Runnable {
	
	private final static byte PUT = 0x01;
	private final static byte GET = 0x02;
	private final static byte REMOVE = 0x03;
	private final static byte OK = 0x00;
	private final static byte KEY_DNE = 0x01;
	private final static byte OUT_OF_SPACE = 0x02;
	private final static byte SYSTEM_OVERLOAD= 0x03;
	private final static byte INTERNAL_FAILURE = 0x04;
	private final static byte BAD_COMMAND = 0x05;
	
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
			
			byte[] original = new byte[Command.SIZE + Key.SIZE + Value.SIZE];
			byte[] reply = new byte[Command.SIZE + Key.SIZE + Value.SIZE];
			InputStream in = server.getClient().getInputStream();
			OutputStream out = server.getClient().getOutputStream();
			ObjectOutputStream o_out = new ObjectOutputStream(server.getClient().getOutputStream());
			ObjectInputStream i_in = new ObjectInputStream(server.getClient().getInputStream());
			Command command;
			in.read();
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
			
			// Receive byte stream
			in.read(original, 0, Command.SIZE + Key.SIZE + Value.SIZE);
			byte c = original[0];
			Key k = new Key(original, Command.SIZE);
			Value v = new Value(original, Command.SIZE +Key.SIZE);
			
			// Find the correct keyspace for this Key
			int corresponding_node_index = getNodeIndex(k);
			Node temp_node, temp_node2;
			List<Node> temp_children;
			if(corresponding_node_index == server.getNode().getPosition()) { // Check if this key belongs in this node's keyspace
				temp_node = this.server.getNode();
				switch(c) {
				case PUT:
					reply[0] = temp_node.addToKvpairs(k, v);
					break;
				case GET:
					reply = temp_node.getValueFromKvpairs(k); // Either 1 byte or 1025 bytes, depending on whether a match is found
					break;
				case REMOVE:
					reply[0] = temp_node.removeKeyFromKvpairs(k);
					break;
				default:
					reply[0] = BAD_COMMAND;
					break;
				}
				this.server.setNode(temp_node);
			}
			else {
				for(int i=0; i<server.getNode().getChildren().size(); i++) { // Iterate through this node's children
					if(corresponding_node_index == server.getNode().getChildren().get(i).getPosition()) { // This Key belongs in a child's keyspace
						temp_node = server.getNode().getChildren().get(i);
						switch(c) {
						case PUT:
							reply[0] = temp_node.addToKvpairs(k, v);
							break;
						case GET:
							reply = temp_node.getValueFromKvpairs(k); // Either 1 byte or 1025 bytes, depending on whether a match is found
							break;
						case REMOVE:
							reply[0] = temp_node.removeKeyFromKvpairs(k);
							break;
						default:
							reply[0] = BAD_COMMAND;
							break;
						}
						// Copy the change to temp_node, then eventually to this.Node via temp variables
						temp_children = this.server.getNode().getChildren();
						temp_children.set(i, temp_node);
						temp_node2 = this.server.getNode();
						temp_node2.setChildren(temp_children);
						this.server.setNode(temp_node2);
						break;
					}
				}
			}
			
			new Propagate(); //TODO: Complete propagate class
			
			// Send reply to output stream
			out.write(reply);
			out.flush();
		}

		private int getNodeIndex(Key k) { //TODO: Maybe adjust this function hoping for a more even distribution of Keys?
			int b =(int) k.getValue(0);
			return b % this.server.getNodeList().size();
		}
	}
