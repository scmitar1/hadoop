package util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @FileName : FieldCounter.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : Map/Reduce단계에서 line에 등장하는 특정 필드의 값에 대한 Counter 
 */
public class FieldCounter {
	private static Map<String, Integer> map;
	private static FieldComparator com;
	private static Map<String, Integer> sortedMap;
	
	public FieldCounter() {
		map = new HashMap<String, Integer> ();
		com = new FieldComparator(map);
		sortedMap = new TreeMap<String, Integer>(com);
	}
	
	/**
	 * @Method Name : add
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : 특정 필드의 값에 대해 Counter를 증가시킨다. 
	 * @param field : 추가할 특정 필드의 값
	 */
	public void add(String field) {
		if ( map.containsKey(field) ) {
			int count = map.get(field);
			map.put(field, ++count);
		} else {
			map.put(field, 1);
		}		
	}
	
	/**
	 * @Method Name : getSortedMap
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : 특정 필드의 값들을 Counter를 기준으로 정렬하여 반환한다. 
	 * @return : 정렬된 FieldCounterMap
	 */
	public Map<String,Integer> getSortedMap() {
		sortedMap.putAll(map);
		return sortedMap;		
	}
	
	/**
	 * @Method Name : getSize
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : 특정 필드에 대한 값이 몇종류나 있는지 반환한다.
	 * @return : 특정 필드에 대한 값의 종류 갯수
	 */
	public int getSize() {
		return map.entrySet().size();
	}
	

}
