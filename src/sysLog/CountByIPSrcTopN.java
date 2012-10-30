package sysLog;

import newMapper.IPSrcToCounterMapper;
import newMapper.IPToSrcCounterMapper;
import newReducer.IPSrcToCounterReducer;
import newReducer.IPToSrcCountReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.hadoop.mapreduce.LzoTextInputFormat;

public class CountByIPSrcTopN extends Configured implements Tool{

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		
		Job firstJob = new Job(conf);
		Job secondJob = new Job(conf);
		
		firstJob.setJarByClass(getClass());
		secondJob.setJarByClass(getClass());
		
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		Path tmp = new Path("/scmitar1/intermediate");
		
		firstJob.setMapperClass(IPSrcToCounterMapper.class);
		firstJob.setReducerClass(IPSrcToCounterReducer.class);
		firstJob.setOutputKeyClass(Text.class);
		firstJob.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(firstJob, in);
		FileOutputFormat.setOutputPath(firstJob, tmp);
		
		firstJob.setJobName("IP/SrcTopN.firstMapReduce");
		firstJob.setInputFormatClass(LzoTextInputFormat.class);
		firstJob.setOutputFormatClass(TextOutputFormat.class);
		
		ControlledJob cJob1 = new ControlledJob(conf);
		cJob1.setJob(firstJob);
		
		secondJob.setMapperClass(IPToSrcCounterMapper.class);
		secondJob.setReducerClass(IPToSrcCountReducer.class);
		secondJob.setOutputKeyClass(Text.class);
		secondJob.setOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(secondJob, tmp);
		FileOutputFormat.setOutputPath(secondJob, out);
		
		secondJob.setJobName("IP/SrcTopN.secondMapReduce");
		secondJob.setInputFormatClass(KeyValueTextInputFormat.class);
		secondJob.setOutputFormatClass(TextOutputFormat.class);
		
		ControlledJob cJob2 = new ControlledJob(conf);
		cJob2.setJob(secondJob);
	
		cJob2.addDependingJob(cJob1);
		
		JobControl jc = new JobControl("CountByIPSrcTopN");
		jc.addJob(cJob1);
		jc.addJob(cJob2);
		
		Thread thread = new Thread(jc);
		thread.start();
		
		while (!jc.allFinished()) {		}
		FileSystem fs = FileSystem.get(conf);
		boolean deleted = fs.deleteOnExit(tmp);
		
		if (deleted) {
			fs.close();
		}
		
		System.out.println("temp is deleted: " + (deleted ? "true" : "false"));
		
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new Configuration(), new CountByIPSrcTopN(), args);
		System.exit(run);
	}
}
