#!/bin/sh

#setup paths and options
CURR_DIR=`pwd`
JVM_OPTS="-Dsun.lang.ClassLoader.allowArraySyntax=true "
POSTER_OPTS="-Dstarter.home=$CURR_DIR -Dninja.external.configuration=application.conf -Dninja.port=9000 -Dninja.mode=prod $@"
POSTER_JAR="demo-poster-store-1.0.0-SNAPSHOT-jar-with-dependencies.jar"

# run with all options
java $JVM_OPTS $POSTER_OPTS -jar $POSTER_JAR
