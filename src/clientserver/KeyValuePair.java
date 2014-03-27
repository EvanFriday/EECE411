/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.util.Map;

import clientserver.message.Value;

//@Deprecated
public class KeyValuePair {

	private Map<Keyy, Valuee> hm;
	
	// Constructors
	KeyValuePair(byte[] kk, byte[] vv)
	{
		Keyy k = new Keyy(kk);
		Valuee v = new Valuee(vv);
		this.hm.put(k, v);
	}
	
	KeyValuePair(Keyy k, Valuee v) {
		this.hm.put(k, v);
	}

	KeyValuePair(){
		Keyy k = new Keyy();
		Valuee v = new Valuee();
		this.hm.put(k,  v);
	}

	public int size() {
		// TODO Auto-generated method stub
		return this.hm.size();
	}

	public void add(Keyy kk, Valuee vv) {
		// TODO Auto-generated method stub
		this.hm.put(kk, vv);
	}

	public boolean containsKey(Keyy kk) {
		// TODO Auto-generated method stub
		return this.hm.containsKey(kk);
	}

	public Valuee get(Keyy kk) {
		// TODO Auto-generated method stub
		return this.hm.get(kk);
	}

	public void remove(Keyy kk) {
		// TODO Auto-generated method stub
		this.hm.remove(kk);
	}
	
	/*
	@Deprecated
	public byte getKey(int index) {
		return key[index];
	}
	
	@Deprecated
	public void setKey(byte k, int index) {
		key[index] = k;
	}
	
	@Deprecated
	public byte getValue(int index) {
		return value[index];
	}
	
	@Deprecated
	public void setValue(byte v, int index) {
		value[index] = v;
	}
	*/	
}
