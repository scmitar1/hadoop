package newReducer.extendDB;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import util.FieldCounter;
import writable.CountByIPTableWritable;

public class MultipleFieldReducerDB 
	extends Reducer<Text, Text, CountByIPTableWritable, NullWritable>{
	private static FieldCounter srcCounter;
	private static FieldCounter dstCounter;
	
	
	private Entry<String, Integer> write( FieldCounter counter, String field) {
		Map<String, Integer> map = counter.getSortedMap();
		
		if ( !map.entrySet().isEmpty() ) {
			Entry<String, Integer> topMap = (Entry<String, Integer>) 
												map.entrySet().iterator().next();
			return topMap;
		}
		return null;
	}
	
	public void reduce(Text key, Iterable<Text> values, Context context)
		throws InterruptedException {
		NullWritable none = NullWritable.get();
		srcCounter = new FieldCounter();
		dstCounter = new FieldCounter();
		
		for (Text value : values) {
			String[] record = value.toString().split("\t");
			
			for ( String semiRecord : record ) {
				if ( semiRecord.startsWith("src") ) {
					srcCounter.add(semiRecord.split(":")[1]);
				} else if ( semiRecord.startsWith("dst") ) {
					dstCounter.add(semiRecord.split(":")[1]);
				}
			}
		}
		
		Entry<String, Integer> src = write(srcCounter, "Src");
		Entry<String, Integer> dst = write(dstCounter, "Dst");
				
		if ( src != null && dst != null ) {
			String ip = key.toString();
			try {
				context.write(new CountByIPTableWritable(ip, src.getKey(), 
						dst.getKey(), src.getValue(), dst.getValue()),
						none);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
	}
}
