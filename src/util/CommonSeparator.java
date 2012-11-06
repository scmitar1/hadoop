package util;

/**
 * @FileName : CommonSeparator.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : MapReduce�� ��쿡 �� �ܰ踶�� ������ �� �ִ� �׸���� 
 * ���� �� ���� key, value�� ���ǵǱ� ������ �̿� �����ϱ� ���� 
 * value�� ���� �����ڰ� �ʿ��ϴ� 
 */
public class CommonSeparator {
	public static final String TAB = "\t";
	public static final String COLON = ":";
	public static final String SPACE = " ";
	
	public static enum Separator {
		TAB, COLON, SPACE
	}
}
