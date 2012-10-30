package util;

import java.util.Comparator;
import java.util.Map;

public class FieldComparator implements Comparator<String>{
	private Map<String, Integer> field;
	
	public FieldComparator( Map<String, Integer> field ) {
		this.field = field;
	}
	
	@Override
	public int compare(String a, String b) {
		if ( field.get(a) < field.get(b) ) {
			return 1;
		} else {
			return -1;
		}
	}
}
