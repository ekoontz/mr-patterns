package com.trendmicro;
    
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Average {

    public static class AverageMapper extends Mapper<Object, Text, IntWritable, CountAverageTuple> {
        private IntWritable outHour = new IntWritable();
        private CountAverageTuple outCountAverage = new CountAverageTuple();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            outCountAverage.setCount(1);
            String[] hourAndAverage = value.toString().split(":");
            outHour.set(Integer.valueOf(hourAndAverage[0]));
            outCountAverage.setAverage(Float.valueOf(hourAndAverage[1]));
            context.write(outHour, outCountAverage);
        }
    }
    
    public static class AverageReducer extends Reducer<IntWritable, CountAverageTuple, IntWritable, CountAverageTuple> {

        private CountAverageTuple result = new CountAverageTuple();

        public void reduce(IntWritable key, Iterable<CountAverageTuple> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            float count = 0;

            // Iterate through all input values for this key
            for (CountAverageTuple val : values) {
                sum += val.getCount() * val.getAverage();
                count += val.getCount();
            }

            result.setCount(count);
            result.setAverage(sum / count);
            context.write(key, result);
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Average <input path> <output path>");
            System.exit(-1);
        }

        Job job = Job.getInstance(new Configuration(), "Average");
        job.setJarByClass(Average.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(AverageMapper.class);
        job.setCombinerClass(AverageReducer.class);
        job.setReducerClass(AverageReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(CountAverageTuple.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
