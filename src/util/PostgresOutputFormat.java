package util;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;

public class PostgresOutputFormat {
	private static final String tableNamePrefix = "public.\"";
	private static final String prefix = "\"";
	private static final String postfix = "\"";
	
	public static void setOutput (Job job, String tableName, String... fields ) throws IOException {
		DBOutputFormat.setOutput(job, getTableName(tableName), getFieldsName(fields) );
	}
	
	private static String getTableName( String tableName ) {
		return tableNamePrefix + tableName + postfix;
	}
	
	private static String[] getFieldsName( String[] fields ) {
		int index = 0;
		
		for ( String field : fields ) {
			fields[index] = prefix + field + postfix;
			index++;
		}
		return fields;
	}
}
