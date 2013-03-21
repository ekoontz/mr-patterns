###################################################################
#                                                                 #
# Chapter 2 of MapReduce Design Patterns example code             #
#   by Scott Forman                                               #
# Makefile by Vic Hargrave and Eugene Koontz                      #
#                                                                 #
# 1. Set $(HADOOP_HOME) in your environment                       #
# 2. make clean run-all                                           #
#                                                                 #
###################################################################

HADOOP = ${HADOOP_HOME}/bin/hadoop
OUTPUT_PATH = file://`pwd`/output
INPUT_PATH =  file://`pwd`/input

.PHONY: clean run-all inverted-index counters average

run-all: inverted-index counters average

inverted-index: chapter2.jar
	$(HADOOP) fs -cat $(INPUT_PATH)/InvertedIndex/*
	-$(HADOOP) fs -rm -r $(OUTPUT_PATH)
	$(HADOOP) jar $< com.trendmicro.InvertedIndex $(INPUT_PATH)/InvertedIndex $(OUTPUT_PATH)
	$(HADOOP) fs -cat $(OUTPUT_PATH)/part-r-00000

counters: chapter2.jar
	$(HADOOP) fs -cat $(INPUT_PATH)/Counter/*
	-$(HADOOP) fs -rm -r $(OUTPUT_PATH)
	$(HADOOP) jar $< com.trendmicro.Counter $(INPUT_PATH)/Counter $(OUTPUT_PATH)

average: chapter2.jar
	$(HADOOP) fs -cat $(INPUT_PATH)/Average/*
	-$(HADOOP) fs -rm -r $(OUTPUT_PATH)
	$(HADOOP) jar $< com.trendmicro.Average $(INPUT_PATH)/Average $(OUTPUT_PATH)
	$(HADOOP) fs -cat $(OUTPUT_PATH)/part-r-00000

chapter2.jar: src/*.java
	mkdir -p out
	javac -classpath `$(HADOOP) classpath`:. -d out $^
	jar -cvf $@ -C out .

clean:
	-rm -rf out output *.jar 



