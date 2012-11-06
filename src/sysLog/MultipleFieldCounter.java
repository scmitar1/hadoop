package sysLog;

import java.util.Properties;
import java.util.logging.Logger;

import newMapper.MultipleFieldMapper;
import newReducer.MultipleFieldReducer;
import newReducer.DBReducer.MultipleFieldReducerDB;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import util.PostgresOutputFormat;

import com.hadoop.mapreduce.LzoTextInputFormat;


public class MultipleFieldCounter extends Configured implements Tool{
	
	private static final String PROP_PATH = "/properties/DBProperties.properties";
	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Override
	public int run( String[] args ) throws Exception {
		Properties prop = new Properties();
		prop.load( getClass().getResourceAsStream(PROP_PATH) );
		
		Configuration conf = getConf();
		Path in = new Path( args[0] );
		Path out = new Path( args[1] );
		Job job;
		
		if ( isDBOutput( args ) ) {
			logger.info("Reducer is DBOutput Reducer");
			logger.info("driver: " + prop.getProperty("driver"));
			logger.info("url: " + prop.getProperty("url"));
			logger.info("user: " + prop.getProperty("user"));
			logger.info("password: " + prop.getProperty("password"));
			
			DBConfiguration.configureDB(conf, prop.getProperty("driver"), 
					prop.getProperty("url"), prop.getProperty("user"),
					prop.getProperty("password"));
			job = new Job(conf);
			job.setJarByClass(getClass());
			
			job.setInputFormatClass(LzoTextInputFormat.class);
			//job.setInputFormatClass(TextInputFormat.class);
			FileInputFormat.setInputPaths(job, in);

			job.setReducerClass(MultipleFieldReducerDB.class);
			job.setJobName("MaxSrcDstCountByIPToDB");
			job.setOutputFormatClass(DBOutputFormat.class);
			
			PostgresOutputFormat.setOutput(job, "DayCountByIP", "ip", 
					"MaxSrc", "MaxDst", "MaxSrcCount", "MaxDstCount");
		} else {
			logger.info("Output is FileOutput Reducer");
			
			job = new Job(conf);
			job.setJarByClass(getClass());
			job.setJobName("MaxSrcDstCountByIPToDB");
			
			job.setInputFormatClass(LzoTextInputFormat.class);
			//job.setInputFormatClass(TextInputFormat.class);
			FileInputFormat.setInputPaths(job, in);
			
			job.setReducerClass(MultipleFieldReducer.class);
			FileOutputFormat.setOutputPath(job, out);
			job.setOutputFormatClass(TextOutputFormat.class);
		}
		
		job.setMapperClass(MultipleFieldMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		System.exit(job.waitForCompletion(true)?0:1);
		
		return 0;
	}
	
	private boolean isDBOutput( String[] params ) {
		boolean isDBOut = false;
		
		if ( params.length == 3 ) {
			String dbParam = params[2];
			
			if ( params.length == 3 && dbParam.equals("db") ) {
				isDBOut = true;
			} else if ( params.length == 3 && dbParam.equals("file")) {
				isDBOut = false;
			}
		} else {
			isDBOut = false;
		}
		return isDBOut;
	}
	
	public static void main (String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new MultipleFieldCounter(), args);
		
		System.exit(res);
	}
}
