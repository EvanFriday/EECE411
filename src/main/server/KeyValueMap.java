package main.server;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.message.Key;
import main.message.Value;

/**
 * Custom implementation of Map to abstract away the details of the use of the HashMap.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
public class KeyValueMap implements Map<Key, Value> {
	private HashMap<String, String> storage;
	
	public KeyValueMap() {
		this.storage = new HashMap<String, String>();
	}
	
	@Override
	public int size() {
		return this.storage.size();
	}

	@Override
	public boolean isEmpty() {
		return this.storage.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.storage.containsKey(key.toString());
	}

	@Override
	public boolean containsValue(Object value) {
		return this.storage.containsValue(value.toString());
	}

	@Override
	public Value get(Object key) {
		String value = this.storage.get(key.toString());
		return (value != null ? new Value(value.getBytes()) : null);
	}
	
	@Override
	public Value put(Key key, Value value) {
		String old = this.storage.put(key.toString(), value.toString());
		return (old != null ? new Value(old.getBytes()) : null);
	}

	@Override
	public Value remove(Object key) {
		String old = this.storage.remove(key.toString());
		return (old != null ? new Value(old.getBytes()) : null);
	}

	@Override
	public void putAll(Map<? extends Key, ? extends Value> m) {
		for (java.util.Map.Entry<? extends Key, ? extends Value> e : m.entrySet()) {
			this.storage.put(e.getKey().toString(), e.getValue().toString());
		}
	}

	@Override
	public void clear() {
		this.storage.clear();
	}

	@Override
	public Set<Key> keySet() {
		Set<Key> keySet = new HashSet<Key>();
		Set<String> stringSet = this.storage.keySet();
		
		for (String s : stringSet) {
			keySet.add(new Key(s.getBytes()));
		}
		
		return keySet;
	}

	@Override
	public Collection<Value> values() {
		Collection<Value> valueCol = new ArrayList<Value>();
		Collection<String> stringCol = this.storage.values();
		
		for (String s : stringCol) {
			valueCol.add(new Value(s.getBytes()));
		}
		
		return valueCol;
	}

	@Override
	public Set<Entry<Key, Value>> entrySet() {
		Set<Entry<Key, Value>> entrySet = new HashSet<Entry<Key, Value>>();
		Set<Entry<String, String>> stringSet = this.storage.entrySet();
		
		for (Entry<String, String> s : stringSet) {
			Key k = new Key(s.getKey().getBytes());
			Value v = new Value(s.getValue().getBytes());
			Entry<Key, Value> e = new SimpleEntry<Key, Value>(k, v);
			entrySet.add(e);
		}
		
		return entrySet;
	}
}
