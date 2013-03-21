package com.trendmicro;
    
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Counter {

    enum Words {
        HELLO,
        GOODBYE,
        WORLD,
        HADOOP,
        OTHER
    }

    public static class Map extends Mapper<Object, Text, Text, Text> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString();
            System.out.println("Map called for line " + line);
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                word.set(tokenizer.nextToken());
                output.collect(word, one);
            }
        }
    }

    public static class LinkExtractor extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
                if (word.equalsIgnoreCase("hello")) {
                    context.getCounter(Words.HELLO).increment(1);
                }
                else if (word.equalsIgnoreCase("goodbye")) {
                    context.getCounter(Words.GOODBYE).increment(1);
                }
                else if (word.equalsIgnoreCase("world")) {
                    context.getCounter(Words.WORLD).increment(1);
                }
                else if (word.equalsIgnoreCase("hadoop")) {
                    context.getCounter(Words.HADOOP).increment(1);
                }
                else {
                    context.getCounter(Words.OTHER).increment(1);
                }
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Counter <input path> <output path>");
            System.exit(-1);
        }
        JobConf conf = new JobConf(Counter.class);
        Job job = Job.getInstance(new Configuration(), "Counter");
        job.setJarByClass(Counter.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(LinkExtractor.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        int code = job.waitForCompletion(true) ? 0 : 1;

        FileSystem.get(conf).delete(new Path(args[1]), true);
    }
}
