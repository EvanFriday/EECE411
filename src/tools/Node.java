package tools;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node{
	private InetAddress address;
	private int position;
	private Boolean alive;
	private List<Node> children;
	private Map<Key,Value> kvpairs;
	
	/*
	 * Constructors:
	 */
	public Node(int position,InetAddress address,Boolean alive, List<Node> children, Map<Key,Value> kvpairs){
		this.position = position;
		this.address = address;
		this.alive = alive;
		this.children = new ArrayList<Node>(children);
		this.kvpairs = new ConcurrentHashMap<Key,Value>(kvpairs);
	}
	public Node(int position,InetAddress address,Boolean alive){
		this(position,address,alive,null,null);
	}	
	public Node(Node node){
		this(node.getPosition(),node.getAddress(),node.getAlive(),node.getChildren(),node.getKvpairs());
	}
	public Node(){
		this((Integer) null,null,null,null,null);
	}
	
	/*
	 * Mutators
	 */	
	public InetAddress getAddress(){
		return this.address;
	}
	public InetAddress getAddressAt(int index){
		return this.children.get(index).getAddress();
	}
	public void addChild(Node node){
		this.children.add(node);
	}
	public void removeChild(Node node){
		this.children.remove(node);
	}
	public Node getChild(int index){
		return this.children.get(index);
	}
	public void setChildren(List<Node> children){
		this.children = children;
	}
	public List<Node> getChildren(){
		return this.children;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public Boolean getAlive() {
		return alive;
	}
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	public Map<Key,Value> getKvpairs() {
		return kvpairs;
	}
	public void setKvpairs(Map<Key,Value> kvpairs) {
		this.kvpairs = kvpairs;
	}
	public byte addToKvpairs(Key k, Value v) {
		this.kvpairs.put(k, v);
		return 0x00; // Error code: OK
	}
	public byte[] getValueFromKvpairs(Key k) {
		Value v = this.kvpairs.get(k);
		byte[] b = null;
		if(v == null) {// Key does not exist
			// return 0x01; // Error code: DNE
			b[0] = 0x01;
		}
		else {
			b[0] = 0x00; // Error code: OK
			for(int i=0; i<Value.SIZE; i++) 
				b[i+1] = v.getValue(i);
		}
		return b;
	}
	public byte removeKeyFromKvpairs(Key k) {
		if(this.kvpairs.remove(k) == null)
			return 0x01; // Error code: DNE
		else
			return 0x00; // Error code: OK
	}
}
