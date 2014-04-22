package tests;

import java.util.Random;

import tools.*;
import server.*;

public class TestGetCorrectNode {
	
	public final static int NUM_KEYS = 10;
	public final static int NUM_NODES = 100;
	
	public Key k;
	public Value v;
	public Message m;
	public static Random rn = new Random();
	public static Key[] keys = new Key[NUM_KEYS];
	public static int[] positions = new int[NUM_KEYS];
	
	public static void main(String[] args) {
		for(int i=0; i<NUM_KEYS; i++) {
			keys[i] = new Key();
			rn.nextBytes(keys[i].key);
			positions[i] = getPosition(keys[i]);
			Tools.print("Key: "+keys[i]+", Position: "+positions[i]);
		}
		for(int i=0; i<NUM_KEYS; i++) {
			Key kk = new Key(keys[i]);
			Tools.print("Key: "+kk+", Position: "+getPosition(kk));
		}
	}
	
	private static int getPosition(Key k) {
		// DONE: make getNode Index return node which should hold key
		int a = k.key.hashCode();
		int position = a % NUM_NODES;
		Tools.print("a: "+a+", pos: "+position);
		return position;
	}

}
