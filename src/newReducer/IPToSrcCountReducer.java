package newReducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import util.SrcCountComparator;

public class IPToSrcCountReducer extends Reducer
	<Text, Text, Text, Text>{
	
	public void reduce(Text key, Iterable<Text> values, Context context)
		throws IOException, InterruptedException {
		int totalCount =0;
		List<String> compareList = new ArrayList<String>();
		
		for (Text value : values) {
			String line = value.toString();
			int count = Integer.parseInt(line.split(":")[1]);
			totalCount = totalCount + count;
			compareList.add(line);
		}
		
		Object[] lines = compareList.toArray();
		Arrays.sort( lines, new SrcCountComparator());
		String val = "\r";
		
		for ( int i = 0 ; i < 5 ; i++ ) {
			if ( lines.length > i ) {
				String l = lines[i].toString();
				String src = l.split(":")[0];
				int srcCount = Integer.parseInt(l.split(":")[1]);
				float percent = srcCount * 100 /totalCount;
				val += "["+(i+1)+ "]Src: " + src 
					+" (" + srcCount + "/" + percent + "%) \r";
			}
		}		
		context.write(key, new Text(val));
	}
}
