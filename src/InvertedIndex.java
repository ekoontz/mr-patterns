package com.trendmicro;
    
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndex {

    public static class LinkExtractor extends Mapper<Object, Text, Text, Text> {
        private Text link = new Text();
        private Text id = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] idAndLink = value.toString().split(":");
            id.set(idAndLink[0]);
            link.set(idAndLink[1]);
            context.write(link, id);
        }
    }
    
    public static class Concatenator extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Text id : values) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(" ");
                }
                sb.append(id.toString());
            }

            result.set(sb.toString());
            context.write(key, result);
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: InvertedIndex <input path> <output path>");
            System.exit(-1);
        }

        Job job = Job.getInstance(new Configuration(), "InvertedIndex");
        job.setJarByClass(InvertedIndex.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(LinkExtractor.class);
        job.setCombinerClass(Concatenator.class);
        job.setReducerClass(Concatenator.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}