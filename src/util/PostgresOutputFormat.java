package util;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;

/**
 * @FileName : PostgresOutputFormat.java
 * @author : KimHy
 * @Modified Date : 2012. 11. 6.
 * @Descr : Postgresql���� insert���� ��� ���̺���̳� �÷��� ���� 
 * ������ �ϴ� sql���� �ٸ��� �׷��Ƿ� Driver���� config�Ҷ� ������ �Լ����� 
 * static���� ����д�.   
 */
public class PostgresOutputFormat {
	private static final String tableNamePrefix = "public.\"";
	private static final String prefix = "\"";
	private static final String postfix = "\"";
	
	/**
	 * @Method Name : setOutput
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : Driver���� ����� DBOutputFormat.setOutput�Լ��� 
	 * postgresql�� �°� ��ü�Ѵ� 
	 * @param job : ������ ������ job
	 * @param tableName : MapReduce�� Output�� ������ table
	 * @param fields : MapReduce�� Output�� ������ column
	 * @throws IOException
	 */
	public static void setOutput (Job job, String tableName, String... fields ) throws IOException {
		DBOutputFormat.setOutput(job, getTableName(tableName), getFieldsName(fields) );
	}
	
	/**
	 * @Method Name : getTableName
	 * @Modified Date : 2012. 11. 6.
	 * @Author : KimHy
	 * @Method Dscr : ���̺���� ��ȯ�ϴ� �Լ� 
	 * @param tableName : tableName
	 * @return tableName : postresql�� �´� tableName
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
	 * @return fieldNames : postgresql�� �´� columnName��
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
