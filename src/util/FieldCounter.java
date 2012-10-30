package util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FieldCounter {
	private static Map<String, Integer> map;
	private static FieldComparator com;
	private static Map<String, Integer> sortedMap;
	
	public FieldCounter() {
		map = new HashMap<String, Integer> ();
		com = new FieldComparator(map);
		sortedMap = new TreeMap<String, Integer>(com);
	}
	
	public void add(String field) {
		if ( map.containsKey(field) ) {
			int count = map.get(field);
			map.put(field, ++count);
		} else {
			map.put(field, 1);
		}		
	}
	
	public Map<String,Integer> getSortedMap() {
		sortedMap.putAll(map);
		return sortedMap;		
	}
	
	public int getSize() {
		return map.entrySet().size();
	}
	

}
