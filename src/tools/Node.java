package tools;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node{
	private InetAddress address;
	private int position;
	private Boolean alive;
	private List<Node> children;
	private List<Node> parents;
	private Map<Key,Value> kvpairs;
	/*
	 * Constructors:
	 */
	public Node(int position,InetAddress address,Boolean alive, List<Node> children, List<Node> parents, Map<Key,Value> kvpairs){
		this();
		this.position = position;
		this.address = address;
		this.alive = alive;
		this.children = children;
		this.parents = parents;
		this.kvpairs = kvpairs;
	}
	public Node(int position,InetAddress address,Boolean alive){
		this();
		this.position = position;
		this.address = address;
		this.alive = alive;
	}	
	public Node(Node node){
		this(node.getPosition(),node.getAddress(),node.getAlive(),node.getChildren(),node.getParents(),node.getKvpairs());
	}
	public Node(){
		this.children = new ArrayList<Node>();
		this.parents = new ArrayList<Node>();
		this.kvpairs = new ConcurrentHashMap<Key,Value>();
		this.address = IpTools.getInet();
		this.alive = true;
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
	public void addParent(Node node){
		this.parents.add(node);
	}
	public void removeParent(Node node){
		this.parents.remove(node);
	}
	public List<Node> getParents() {
		return parents;
	}
	public void setParents(List<Node> parents) {
		this.parents = parents;
	}
	public Node getParent(int index){
		return this.parents.get(index);
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
			for(Key ks : this.kvpairs.keySet()){
				if(Arrays.equals(k.key, ks.key)){ // Match found. Delete that entry
					this.kvpairs.remove(ks);
					this.kvpairs.put(k,  v);
					if(this.kvpairs.get(k) == v)
						return ErrorCode.OK;
					else 
						return ErrorCode.KVSTORE_FAIL;
				}
			}
			// If code reaches here, no match found. Add the new pair.
			this.kvpairs.put(k,  v);
			if(this.kvpairs.get(k) == v) {
				return ErrorCode.OK;
			}
			else {
				return ErrorCode.KVSTORE_FAIL;
			}
		}
		else
			return ErrorCode.OUT_OF_SPACE;
	}
	public EVpair getValueFromKvpairs(Key k) {
		EVpair pair;
		Boolean matchfound = false;
		Value retval = new Value();
		ErrorCode err = ErrorCode.KEY_DNE;
		for(Key ks : this.kvpairs.keySet()){

			if(Arrays.equals(k.key, ks.key)){
				retval = this.kvpairs.get(ks);
				matchfound = true;
				break;
			}
		}
		if(!matchfound){
			err = ErrorCode.KEY_DNE;
			pair = new EVpair(err,null);
		}
		else{
			err = ErrorCode.OK;
			pair = new EVpair(err,retval);
		}
		return pair;
	}
	public ErrorCode removeKeyFromKvpairs(Key k) {
		Boolean matchfound = false;
		for(Key ks : this.kvpairs.keySet()){
			if(Arrays.equals(k.key, ks.key)){
				this.kvpairs.remove(ks);
				matchfound = true;
				break;
			}
		}
		
		if(!matchfound)
			return ErrorCode.KEY_DNE;
		else if(!this.kvpairs.containsKey(k))
			return ErrorCode.OK;
		else
			return ErrorCode.KVSTORE_FAIL;
	}
}
