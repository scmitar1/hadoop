package util;

/**
 * @FileName : CommonSeparator.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : MapReduce의 경우에 각 단계마다 정의할 수 있는 항목들이 
 * 오직 한 쌍의 key, value로 정의되기 때문에 이에 대응하기 위한 
 * value에 대한 구분자가 필요하다 
 */
public class CommonSeparator {
	public static final String TAB = "\t";
	public static final String COLON = ":";
	public static final String SPACE = " ";
	
	public static enum Separator {
		TAB, COLON, SPACE
	}
}
