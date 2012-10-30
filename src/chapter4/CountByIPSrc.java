package chapter4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


import util.SrcCountComparator;

public class CountByIPSrc extends Configured implements Tool{
	
	public static class Map extends MapReduceBase
		implements Mapper<LongWritable, Text, Text, IntWritable> {

		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String[] fields = value.toString().split(" ");
			String valueString = value.toString();
			String ip = fields[0];
			String src = "";
			
			if ( value.toString().contains("Src:") ) {
				int st = valueString.indexOf("Src:")+"Src:".length();
				int end = valueString.indexOf(",", st);
				
				if ( end < 0) {
					src = valueString.substring(st);
				} else {
					src = valueString.substring(st, end);
				}
				output.collect(new Text(ip+":"+src), new IntWritable(1));
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
	
	public static class Map1 extends MapReduceBase
		implements Mapper<Text, Text, Text, Text> {

		@Override
		public void map(Text key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			String ip = key.toString().split(":")[0];
			String src = key.toString().split(":")[1];
			String val = src + ":" + value.toString();
			
			output.collect(new Text(ip), new Text(val));
		}
		
	}
	
	public static class Reduce1 extends MapReduceBase 
		implements Reducer<Text, Text, Text, Text> {
		
		@Override
		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
		
			int totalCount =0;
			List<String> compareList = new ArrayList<String>();
			
			while (values.hasNext()) {
				String line = values.next().toString();
				int count = Integer.parseInt(line.split(":")[1]);
				totalCount = totalCount + count;
				compareList.add(line);
			}
			
			Object[] lines = compareList.toArray();
			Arrays.sort( lines, new SrcCountComparator());
			String text = "\n";
			
			for ( int i = 0 ; i < 5 ; i++ ) {
				if ( lines.length > i ) {
					String l = lines[i].toString();
					String src = l.split(":")[0];
					int srcCount = Integer.parseInt(l.split(":")[1]);
					float percent = srcCount * 100 /totalCount;
					text += "["+(i+1)+ "]Src: " + src 
						+" (" + srcCount + "/" + percent + "%) \n";
				}
			}
			output.collect(key, new Text(text));
		}
	}
	
	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = getConf();

		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		Path temp = new Path("/scmitar1/intermediate");

		JobConf jobConf = new JobConf(conf, getClass());
		jobConf.setJobName("IP/MaxSrc.firstMapReduce");
		//jobConf.setBoolean("mapred.compress.map.output", true);
		//jobConf.setClass("mapred.map.output.compression.codec", LzoCodec.class, CompressionCodec.class);
		
		FileInputFormat.setInputPaths(jobConf, in);
		FileOutputFormat.setOutputPath(jobConf, temp);
		
		jobConf.setInputFormat(TextInputFormat.class); 
		jobConf.setOutputFormat(TextOutputFormat.class);

		jobConf.setMapperClass(Map.class);
		jobConf.setReducerClass(Reduce.class);
		
		jobConf.setOutputKeyClass(Text.class);
		jobConf.setOutputValueClass(IntWritable.class);
		Job runJob = new Job(jobConf);
		
		JobConf jobConf1 = new JobConf(getConf(), getClass());
		jobConf1.setJobName("IP/MaxSrc.secondMapReuduce");
		jobConf1.setOutputFormat(SequenceFileOutputFormat.class);
		
		FileInputFormat.setInputPaths(jobConf1, temp);
		FileOutputFormat.setOutputPath(jobConf1, out);
//			SequenceFileOutputFormat.setOutputCompressionType(jobConf1, CompressionType.BLOCK);
//			FileOutputFormat.setCompressOutput(jobConf1, true);
//			FileOutputFormat.setOutputCompressorClass(jobConf1, LzoCodec.class);
		
		jobConf1.setInputFormat(KeyValueTextInputFormat.class); 
		jobConf1.setOutputFormat(TextOutputFormat.class);
		
		jobConf1.setMapperClass(Map1.class);
		jobConf1.setReducerClass(Reduce1.class);
		
		jobConf1.setOutputKeyClass(Text.class);
		jobConf1.setOutputValueClass(Text.class);
		Job runJob1 = new Job(jobConf1);
		
		runJob1.addDependingJob(runJob);
		JobControl jc = new JobControl("CountByIPSrc.scmitar1");
		jc.addJob(runJob);
		jc.addJob(runJob1);
		jc.run();
		
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(temp, runJob.isCompleted());
		System.exit(jc.allFinished() ? 0 : 1);
		return 0;
	}
	
	public static void main(String args[]) throws Exception {
		int run = ToolRunner.run(new Configuration(), new CountByIPSrc(), args);
		System.exit(run);
	}
}
