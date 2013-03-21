###################################################################
#                                                                 #
# Generic Makefile for Hadoop applications.                       #
#                                                                 #
# To adapt for other applications:                                #
# 1. Create a directory for your application.                     #
# 2. Set $(SRC) to the list of source files for your project.     #
# 3. Set $(HADOOP) to point to your HADOOP distribution home      #
#    directory.                                                   #
# 4. To make your app type 'make app=<your app name>'             #
#                                                                 #
###################################################################

# Assumes you have ${HADOOP_INSTALL} defined in your .bash_profile
HADOOP	= ${HADOOP_INSTALL}/bin/hadoop

SRC	= src/$(app).java
OUT	= out

.PHONY: clean run-ii hdfs-clean hdfs-clean-input hdfs-clean-output hdfs-upload-input

$(app): $(SRC)
	mkdir -p $(OUT)
	javac -classpath `$(HADOOP) classpath`:./out -d $(OUT) $(SRC)
	jar -cvf $(app).jar -C $(OUT) .

%.jar: src/%.java
	mkdir -p $(OUT)
	javac -classpath `$(HADOOP) classpath`:./out -d $(OUT) $^
	jar -cvf $@ -C $(OUT) .

clean:
	rm -rf  $(OUT)  *.jar 

hdfs-clean-output:
	-hadoop fs -rm -r /user/`whoami`/output

hdfs-clean-input:
	-hadoop fs -rm -r /user/`whoami`/input

hdfs-upload-input: hdfs-clean-input
	hadoop fs -mkdir /user/`whoami`/input
	hadoop fs -copyFromLocal input/* /user/`whoami`/input

run-ii: InvertedIndex.jar hdfs-clean-output hdfs-clean-input hdfs-upload-input
	hadoop fs -cat /user/ekoontz/input/InvertedIndex/*
	hadoop jar InvertedIndex.jar com.trendmicro.InvertedIndex input/InvertedIndex output
	hadoop fs -cat /user/ekoontz/input/InvertedIndex/*
