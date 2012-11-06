package mapReduceFilter;

/**
 * @FileName : MapResCondition.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : Mapper할때 Mapping할 항목을 걸러내는 Filter
 */
public class MapResCondition {
	
	/**
	 * @Method Name : hasField
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Descr : Mapper의 line에서 특정필드 항목이 있는지 여부 확인 
	 * @param line : Mapper에서 value로 입력 받는 라인
	 * @param field : line에서 있는지 여부를 체크할 field
	 * @return result : line에서 특정 필드가 있는지 여부
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
	 * @Method Dscr : line에서 Start_Time, End_Time 필드가 있는지,
	 * "-"이 아닌지 여부 확인 
	 * @param line : Mapper에서 value로 입력 받는 라인
	 * @return result : line에서 Start_Time, End_Time 필드가 있는지 여부
	 */
	public static boolean hasTimeField ( String line ) {
		boolean result = false;
		
		result = hasField( line, "Start_Time:" ) && hasField( line, "End_Time:" );
		result = result && !hasField( line, "Start_Time:-" ) 
				&& !hasField( line, "End_Time:-" );
		return result;
	}
}
