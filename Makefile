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

$(app): $(SRC)
	mkdir -p $(OUT)
	javac -classpath `$(HADOOP) classpath`:./out -d $(OUT) $(SRC)
	jar -cvf $(app).jar -C $(OUT) .

clean:
	rm -rf  $(OUT)  *.jar 
