package mapReduceFilter;

/**
 * @FileName : MapResCondition.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : Mapper�Ҷ� Mapping�� �׸��� �ɷ����� Filter
 */
public class MapResCondition {
	
	/**
	 * @Method Name : hasField
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Descr : Mapper�� line���� Ư���ʵ� �׸��� �ִ��� ���� Ȯ�� 
	 * @param line : Mapper���� value�� �Է� �޴� ����
	 * @param field : line���� �ִ��� ���θ� üũ�� field
	 * @return result : line���� Ư�� �ʵ尡 �ִ��� ����
	 */
	public static boolean hasField ( String line, String field ) {
		boolean result = false;
		
		if ( line.contains( field ) ) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}
	
	/**
	 * @Method Name : hasTimeField
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : line���� Start_Time, End_Time �ʵ尡 �ִ���,
	 * "-"�� �ƴ��� ���� Ȯ�� 
	 * @param line : Mapper���� value�� �Է� �޴� ����
	 * @return result : line���� Start_Time, End_Time �ʵ尡 �ִ��� ����
	 */
	public static boolean hasTimeField ( String line ) {
		boolean result = false;
		
		result = hasField( line, "Start_Time:" ) && hasField( line, "End_Time:" );
		result = result && !hasField( line, "Start_Time:-" ) 
				&& !hasField( line, "End_Time:-" );
		return result;
	}
}
