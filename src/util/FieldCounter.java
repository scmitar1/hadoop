package util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @FileName : FieldCounter.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : Map/Reduce�ܰ迡�� line�� �����ϴ� Ư�� �ʵ��� ���� ���� Counter 
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
	 * @Method Dscr : Ư�� �ʵ��� ���� ���� Counter�� ������Ų��. 
	 * @param field : �߰��� Ư�� �ʵ��� ��
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
	 * @Method Dscr : Ư�� �ʵ��� ������ Counter�� �������� �����Ͽ� ��ȯ�Ѵ�. 
	 * @return : ���ĵ� FieldCounterMap
	 */
	public Map<String,Integer> getSortedMap() {
		sortedMap.putAll(map);
		return sortedMap;		
	}
	
	/**
	 * @Method Name : getSize
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : Ư�� �ʵ忡 ���� ���� �������� �ִ��� ��ȯ�Ѵ�.
	 * @return : Ư�� �ʵ忡 ���� ���� ���� ����
	 */
	public int getSize() {
		return map.entrySet().size();
	}
	

}
