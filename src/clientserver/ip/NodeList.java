package clientserver.ip;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clientserver.message.Key;

public class NodeList {
	public static final int NUM_DIVS = 8;
	
	private List<List<String>> keySpaces;
	
	public NodeList() {
		this.keySpaces = new ArrayList<List<String>>();
		for (int i = 0; i < NUM_DIVS; i++) {
			this.keySpaces.add(new ArrayList<String>());
		}
	}
	
	public NodeList(String...nodes) {
		this(Arrays.asList(nodes));
	}
	
	public NodeList(FileReader file) throws IOException {
		this(getAllFromFile(file));
	}
	
	public NodeList(List<String> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			this.keySpaces.get(i%NUM_DIVS).add(nodes.get(i));
		}
	}
	
	public List<String> getKeySpace(Key k) {
		return keySpaces.get(getKeySpaceNumber(k));
	}
	
	private static List<String> getAllFromFile(FileReader file) throws IOException {
		BufferedReader in = new BufferedReader(file);
		List<String> allNodes = new ArrayList<String>();
		String next = in.readLine();
		
		while (next != null) {
			allNodes.add(next);
		}
		
		return allNodes;
	}
	
	private int getKeySpaceNumber(Key k) {
		byte lastByte = k.getValue()[0];
		
		return lastByte & 0x07;
	}
}
