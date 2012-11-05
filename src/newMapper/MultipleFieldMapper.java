package newMapper;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class MultipleFieldMapper 
	extends Mapper<LongWritable, Text, Text, Text>{
	
	public void map(LongWritable key, Text value, Context context) 
		throws IOException, InterruptedException {
		String line = value.toString();
		
		writtingMap(line, context, "Src:", "Dst:");
	}
	
	private void writtingMap(String line, Context context, String... keys) 
		throws IOException, InterruptedException {
		String ip = line.split(" ")[0];
		StringBuffer conVal = new StringBuffer("");
		
		for ( String key : keys ) {
			String field = key.toLowerCase().substring(0, key.length() -1);
			String val = "";
			
			if ( line.contains(key) ) {
				int st = line.indexOf(key) + key.length();
				int end = line.indexOf(",", st);
				
				if ( end < 0 ) {
					val = line.substring(st);
				} else {
					val = line.substring(st, end);
				}
				
				if ( !conVal.equals("") ) {
					conVal.append("\t");
				}
				conVal.append( field + ":" + val );
			}
		}
		context.write(new Text(ip), new Text(conVal.toString()));
	}
}
