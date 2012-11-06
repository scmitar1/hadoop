package util;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;

/**
 * @FileName : PostgresOutputFormat.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : Postgresql에서 insert문의 경우 테이블명이나 컬럼에 대한 
 * 지정이 일단 sql문과 다르다 그러므로 Driver에서 config할때 유용한 함수들을 
 * static으로 묶어둔다.   
 */
public class PostgresOutputFormat {
	private static final String tableNamePrefix = "public.\"";
	private static final String prefix = "\"";
	private static final String postfix = "\"";
	
	/**
	 * @Method Name : setOutput
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : Driver에서 사용할 DBOutputFormat.setOutput함수를 
	 * postgresql에 맞게 대체한다 
	 * @param job : 설정을 저장할 job
	 * @param tableName : MapReduce의 Output을 저장할 table
	 * @param fields : MapReduce의 Output을 저장할 column
	 * @throws IOException
	 */
	public static void setOutput (Job job, String tableName, String... fields ) throws IOException {
		DBOutputFormat.setOutput(job, getTableName(tableName), getFieldsName(fields) );
	}
	
	/**
	 * @Method Name : getTableName
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : 테이블명을 변환하는 함수 
	 * @param tableName : tableName
	 * @return tableName : postresql에 맞는 tableName
	 */
	private static String getTableName( String tableName ) {
		return tableNamePrefix + tableName + postfix;
	}
	
	/**
	 * @Method Name : getFieldsName
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr :
	 * @param fields : column
	 * @return fieldNames : postgresql에 맞는 columnName들
	 */
	private static String[] getFieldsName( String[] fields ) {
		int index = 0;
		
		for ( String field : fields ) {
			fields[index] = prefix + field + postfix;
			index++;
		}
		return fields;
	}
}
