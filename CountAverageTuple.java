package com.trendmicro;
    
import java.io.IOException;
import java.io.*;

import org.apache.hadoop.io.*;

public class CountAverageTuple implements Writable {
	private float count = 0;
	private float average = 0;

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}

	public float getAverage() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public void readFields(DataInput in) throws IOException {
		count = in.readFloat();
		average = in.readFloat();
	}

	public void write(DataOutput out) throws IOException {
		out.writeFloat(count);
		out.writeFloat(average);
	}

	public String toString() {
		return Float.toString(count) + "\t" + Float.toString(average);
	}
}

