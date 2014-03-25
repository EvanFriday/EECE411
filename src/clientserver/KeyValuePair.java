/* Authors: Evan Friday, Cameron Johnston, Kevin Petersen
 * Date: 2014-03-02
 * EECE 411 Project Phase 2 Server:
 */

package clientserver;

import java.util.Map;

//@Deprecated
public class KeyValuePair {

	private Map<Keyy, Valuee> hm;
	
	// Constructors
	KeyValuePair(byte[] kk, byte[] vv)
	{
		Keyy k = new Keyy(kk);
		Valuee v = new Valuee(vv);
		if(this.hm.containsKey(k))
			this.hm.remove(k);
		this.hm.put(k, v);
	}
	
	KeyValuePair(Keyy k, Valuee v) {
		if(this.hm.containsKey(k))
			this.hm.remove(k);
		this.hm.put(k, v);
	}

	KeyValuePair(){
		Keyy k = new Keyy();
		Valuee v = new Valuee();
		this.hm.put(k,  v);
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
