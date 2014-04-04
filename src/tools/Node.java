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
		this();
		this.position = position;
		this.address = address;
		this.alive = alive;
		this.children = children;
		this.kvpairs = kvpairs;
	}
	public Node(int position,InetAddress address,Boolean alive){
		this();
		this.position = position;
		this.address = address;
		this.alive = alive;
	}	
	public Node(Node node){
		this(node.getPosition(),node.getAddress(),node.getAlive(),node.getChildren(),node.getKvpairs());
	}
	public Node(){
		this.children = new ArrayList<Node>();
		this.kvpairs = new ConcurrentHashMap<Key,Value>();
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
	public ErrorCode addToKvpairs(Key k, Value v) {
		if(this.kvpairs.size()<40000){
			if(this.kvpairs.put(k, v) != null)
			return ErrorCode.OK;
			else
			return ErrorCode.KVSTORE_FAIL;
		}
		else
			return ErrorCode.OUT_OF_SPACE;
	}
	public EVpair getValueFromKvpairs(Key k) {
		EVpair ret;
		Value value = this.kvpairs.get(k);
		if(value==null) 
			ret = new EVpair(ErrorCode.KEY_DNE,value);
		else
			ret = new EVpair(ErrorCode.OK,value);
;//		byte[] b = null;
//		
//		if(v == null) {// Key does not exist
//			// return 0x01; // Error code: DNE
//			b[0] = ErrorCode.KEY_DNE.getByte();
//		}
//		else {
//			b[0] = ErrorCode.OK.getByte();
//			for(int i=0; i<Value.SIZE; i++) 
//				b[i+1] = v.getValue(i);
//		}
		return ret;
	}
	public LeadByte removeKeyFromKvpairs(Key k) {
		ErrorCode error;
		if(this.kvpairs.remove(k)==null)
			error = ErrorCode.KEY_DNE;
		else if(!this.kvpairs.containsKey(k))
			error = ErrorCode.OK;
		else
			error = ErrorCode.KVSTORE_FAIL;
		return error;
	}
}

