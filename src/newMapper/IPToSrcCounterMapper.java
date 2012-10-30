package newMapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class IPToSrcCounterMapper extends Mapper<Text, Text, Text, Text>{
	public void map (Text key, Text value, Context context) 
		throws IOException, InterruptedException {
		String ip = key.toString().split(":")[0];
		String src = key.toString().split(":")[1];
		String val = src + ":" + value.toString();
		
		context.write(new Text(ip), new Text(val));	
	}
}
