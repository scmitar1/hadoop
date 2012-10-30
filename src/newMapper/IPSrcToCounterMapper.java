package newMapper;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class IPSrcToCounterMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	public void map(LongWritable key, Text value, Context context) 
		throws IOException, InterruptedException{
		String[] fields = value.toString().split(" ");
		String valueString = value.toString();
		String ip = fields[0];
		String src = "";
		
		if ( value.toString().contains("Src:") ) {
			int st = valueString.indexOf("Src:") + "Src:".length();
			int end = valueString.indexOf(",",st);
			
			if ( end < 0 ) {
				src = valueString.substring(st);
			} else {
				src = valueString.substring(st, end);
			}
			context.write(new Text(ip+":"+src), new IntWritable(1));
		}
	}
}
