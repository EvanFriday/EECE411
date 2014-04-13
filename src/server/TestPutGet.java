package server;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import tools.*;

public class TestPutGet {
	static Key k;
	static Value v;
	private static ConcurrentHashMap<Key, Value> map;
	
	public TestPutGet() throws IOException {
		int i;
		byte val1 = 0x01;
		byte val2 = 0x02;
		k = new Key();
		v = new Value();
		map = new ConcurrentHashMap<Key, Value>();
		
		// Set k to all ones
		for(i=0; i<Key.SIZE; i++) {
			k.setValue(val1, i);
		}
		// Set v to all twos
		for(i=0; i<Value.SIZE; i++) {
			v.setValue(val2, i);
		}
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		byte[] b = new byte[1024];
		int i;
		TestPutGet test = new TestPutGet();
		System.out.println("Putting Key: " + k.toString() + ", Value: " + v.toString());
		map.put(k, v);
		Value returned_value = map.get(k);
		System.out.println("Returned Value: " + returned_value.toString());
	}

}
