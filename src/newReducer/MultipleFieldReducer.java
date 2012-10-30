package newReducer;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import util.FieldCounter;

public class MultipleFieldReducer extends Reducer<Text, Text, Text, Text>{
	private static FieldCounter srcCounter;
	private static FieldCounter dstCounter;
	private static Logger log;
	
	public void setup(Context context) {
		log = Logger.getLogger(getClass().getName());
	}
	
	private void write(StringBuffer value, FieldCounter counter, String field) {
		Map<String, Integer> map = counter.getSortedMap();
		
		if ( !map.entrySet().isEmpty() ) {
			Entry<String, Integer> topMap = (Entry<String, Integer>) 
												map.entrySet().iterator().next();
			if ( !value.toString().equals("") ) {
				value.append("\t");
			}
			
			value.append( "Max_" + field + ":" + topMap.getKey() +   
								 "(" + topMap.getValue() + ")");
		}
	}
	
	public void reduce(Text key, Iterable<Text> values, Context context)
		throws IOException, InterruptedException {

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
		
		StringBuffer contextValue = new StringBuffer("");
		
		write(contextValue, srcCounter, "Src");
		write(contextValue, dstCounter, "Dst");
				
		log.info(contextValue.toString());
		if ( !contextValue.toString().equals("") ) {
			context.write(key, new Text(contextValue.toString()));
		}
	}
}
