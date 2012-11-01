package sysLog;

import java.util.Properties;
import java.util.logging.Logger;

import newMapper.MultipleFieldMapper;
import newReducer.MultipleFieldReducer;
import newReducer.extendDB.MultipleFieldReducerDB;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.hadoop.mapreduce.LzoTextInputFormat;


public class MultipleFieldCounter extends Configured implements Tool{
	
	private static final String PROP_PATH = "/properties/DBProperties.properties";
	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Override
	public int run(String[] args) throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getResourceAsStream(PROP_PATH));
		
		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJarByClass(getClass());
		job.setJobName("multipleTest");
		
		Path in = new Path (args[0]);
		Path out = new Path (args[1]);
		
		job.setInputFormatClass(LzoTextInputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		if ( isDBOutput( args[2] ) ) {
			logger.info("driver: " + prop.getProperty("driver"));
			logger.info("url: " + prop.getProperty("url"));
			logger.info("user: " + prop.getProperty("user"));
			logger.info("password: " + prop.getProperty("password"));
			
			job.setJobName("MaxSrcDstCountByIPToDB");
			job.setOutputFormatClass(DBOutputFormat.class);
			DBConfiguration.configureDB(conf, prop.getProperty("driver"), 
					prop.getProperty("url"), prop.getProperty("user"),
					prop.getProperty("password"));
			DBOutputFormat.setOutput(job, "day_countByIP", "ip", 
					"MaxSrc", "MaxDst", "MaxSrcCount", "MaxDstCount");
		} else {
			job.setOutputFormatClass(TextOutputFormat.class);
		}
		
		
		job.setMapperClass(MultipleFieldMapper.class);
		if ( isDBOutput( args[2] )) {
			logger.info("is DBOutput Reducer");
			job.setReducerClass(MultipleFieldReducerDB.class);
		} else {
			logger.info("is FileOutput Reducer");
			job.setReducerClass(MultipleFieldReducer.class);
		}
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		System.exit(job.waitForCompletion(true)?0:1);
		
		return 0;
	}
	
	private boolean isDBOutput(String param) {
		if ( param != null && param.equals("db") ) {
			logger.info("OuputFormat is DB_Ouput");
			return true;
		} else {
			logger.info("OutputFormat is File_Output");
			return false;
		}
	}
	
	public static void main (String[] args) throws Exception {

		
		int res = ToolRunner.run(new Configuration(), new MultipleFieldCounter(), args);
		
		System.exit(res);
	}
}
