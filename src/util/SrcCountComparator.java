package util;

import java.util.Comparator;

public class SrcCountComparator implements Comparator<Object>{

	@Override
	public int compare(Object t1, Object t2) {
		String line1 = t1.toString();
		String line2 = t2.toString();
		int srcCount1 = Integer.parseInt(line1.split(":")[1]);
		int srcCount2 = Integer.parseInt(line2.split(":")[1]);
		
		if ( srcCount1 < srcCount2 ) {
			return 1;
		} else {
			return -1;
		}
	}

}
