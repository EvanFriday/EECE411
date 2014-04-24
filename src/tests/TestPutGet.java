package tests;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import server.Server;
import tools.*;

public class TestPutGet {
	static Key k;
	static Value v;
	static Server s;
	public TestPutGet() throws IOException {
		int i;
		byte val1 = 0x01;
		byte val2 = 0x02;
		k = new Key();
		v = new Value();
		s = new Server(true);
		new Message();
		new Message();
		s.testMap = new ConcurrentHashMap<Key, Value>();
		
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
		System.out.println("Putting Key: " + k.toString() + ", Value: " + v.toString());
		TestPutGet.s.testMap.put(k, v);
		Value returned_value = TestPutGet.s.testMap.get(k);
		System.out.println("Returned Value: " + returned_value.toString());
	}

}
