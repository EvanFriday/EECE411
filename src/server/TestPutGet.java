package server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import tools.*;

public class TestPutGet {
	static Key k;
	static Value v;
	static Server s;
	private static ConcurrentHashMap<Key, Value> map;
	
	public TestPutGet() throws IOException {
		int i;
		byte val1 = 0x01;
		byte val2 = 0x02;
		k = new Key();
		v = new Value();
		s = new Server(true);
		Message m = new Message();
		Message reply = new Message();
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
		byte[] b = new byte[1024];
		int i;
		TestPutGet test = new TestPutGet();
		System.out.println("Putting Key: " + k.toString() + ", Value: " + v.toString());
		test.s.testMap.put(k, v);
		Value returned_value = test.s.testMap.get(k);
		System.out.println("Returned Value: " + returned_value.toString());
	}

}
