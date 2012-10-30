package chapter4;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CountByIPType extends Configured implements Tool{
	
	public static class Map extends MapReduceBase 
		implements Mapper<LongWritable, Text, Text, IntWritable> {

		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String[] fields = value.toString().split(" ");
			String valueString = value.toString();
			String ip = fields[0];
			String type = "";
			
			if ( value.toString().contains("Type:") ) {
				int st = valueString.indexOf("Type:")+"Type:".length();
				int end = valueString.indexOf(",", st);
				
				if ( end < 0) {
					type = valueString.substring(st);
				} else {
					type = valueString.substring(st, end);
				}
				output.collect(new Text(ip+"["+type+"]"), new IntWritable(1));
			}
			
		}
	}
	
	public static class Reduce extends MapReduceBase
		implements Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			int count = 0;
			
			while (values.hasNext()) {
				values.next();
				count ++; 
			}
			output.collect(key, new IntWritable(count));
		}
	}
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		JobConf job = new JobConf(conf, getClass());
		
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJobName("CountByIPType.scmitar1");
		job.setInputFormat(TextInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);
		ChainMapper.addMapper(job, chapter4.CountByIPType.Map.class, 
								LongWritable.class, 
								Text.class, 
								Text.class, 
								IntWritable.class, true, new JobConf(false));
		// job.setMapperClass(chapter4.CountByIPType.Map.class);
		ChainReducer.setReducer(job, chapter4.CountByIPType.Reduce.class, 
								Text.class, 
								IntWritable.class, 
								Text.class,
								IntWritable.class, true, new JobConf(false));
		//job.setReducerClass(chapter4.CountByIPType.Reduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		Job runJob = new Job(job);
		JobControl jc = new JobControl("CountByIPType.scmitar1");
		jc.addJob(runJob);
		jc.run();
		//JobClient.runJob(job);
		return 0;
	}
	
	public static void main(String args[]) throws Exception {
		int run = ToolRunner.run(new Configuration(), new CountByIPType(), args);
		System.exit(run);
	}
}
