package chapter4;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.contrib.utils.join.DataJoinMapperBase;
import org.apache.hadoop.contrib.utils.join.DataJoinReducerBase;
import org.apache.hadoop.contrib.utils.join.TaggedMapOutput;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SkipBadRecords;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DataJoin extends Configured implements Tool{
	public static class MapClass extends DataJoinMapperBase{

		@Override
		protected Text generateGroupKey(TaggedMapOutput aRecord) {
			// TODO Auto-generated method stub
			String line = ((Text) aRecord.getData()).toString();
			String[] tokens = line.split(",");
			String groupKey = tokens[0];
			return new Text(groupKey);
		}

		@Override
		protected Text generateInputTag(String inputFile) {
			// TODO Auto-generated method stub
			String dataSource = inputFile.split("-")[0];
			return new Text(dataSource);
		}

		@Override
		protected TaggedMapOutput generateTaggedMapOutput(Object value) {
			// TODO Auto-generated method stub
			TaggedWritable retv = new TaggedWritable((Text)value);
			retv.setTag(this.inputTag);
			return retv;
		}
	}
	
	public static class Reduce extends DataJoinReducerBase {

		@Override
		protected TaggedMapOutput combine(Object[] tags, Object[] values) {
			// TODO Auto-generated method stub
			if (tags.length < 2) return null;
			String joinedStr = "";
			
			for (int i = 0 ; i < values.length ; i++) {
				if ( i > 0 ) joinedStr += ",";
				TaggedWritable tw = (TaggedWritable) values[i];
				String line = ((Text) tw.getData()).toString();
				String[] tokens = line.split("," , 2);
				joinedStr += tokens[1];
			}
			TaggedWritable retv = new TaggedWritable(new Text(joinedStr));
			retv.setTag((Text) tags[0]);
			return retv;
		}
	}
	
	public static class TaggedWritable extends TaggedMapOutput {
		private Writable data;
		
		public TaggedWritable() {
			this.tag = new Text();
		}
		
		public TaggedWritable(Writable data) {
			this.tag = new Text("");
			this.data = data;
		}
		
		@Override
		public void readFields(DataInput in) throws IOException {
			// TODO Auto-generated method stub
			this.tag.readFields(in);
			//http://stackoverflow.com/questions/10201500/hadoop-reduce-side-join-using-datajoin 참조
			String dataClz = in.readUTF();
			if (this.data == null 
					|| !this.data.getClass().getName().equals(dataClz)) {
				try {
					this.data = (Writable) ReflectionUtils.newInstance(
							Class.forName(dataClz), null);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			this.data.readFields(in);
		}

		@Override
		public void write(DataOutput out) throws IOException {
			// TODO Auto-generated method stub
			this.tag.write(out);
			// http://stackoverflow.com/questions/10201500/hadoop-reduce-side-join-using-datajoin 참조
			out.writeUTF(this.data.getClass().getName());
			this.data.write(out);
		}

		@Override
		public Writable getData() {
			// TODO Auto-generated method stub
			return data;
		}
		
		public void setData(Writable data) {
			this.data = data;
		}
	}
	
	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = getConf();
		SkipBadRecords.setReducerMaxSkipGroups(conf, 3);
		
		JobConf job = new JobConf(conf, getClass());
		
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		job.setJobName("DataJoin");
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		
		job.setMaxMapAttempts(2);
		job.setMaxReduceAttempts(2);
		
		job.setInputFormat(TextInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(TaggedWritable.class);
		job.set("mapred.textoutputformatter.separator", ",");
		
		JobClient.runJob(job);
		return 0;
	}
	
	public static void main (String args[]) throws Exception {
		int res = ToolRunner.run(new Configuration(), new DataJoin(), args);
		System.exit(res);
	}
}
