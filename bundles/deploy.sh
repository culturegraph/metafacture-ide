#!/bin/sh
export JAR=target/lodmill-rd-0.1.0-SNAPSHOT-jar-with-dependencies.jar
export RES=src/main/resources
cd ../..
git clone git://github.com/lobid/lodmill.git lodmill-dependency
cd lodmill-dependency/lodmill-rd
mvn clean assembly:assembly -q --settings ../settings.xml
jar uf $JAR -C $RES metaflow-pipe.properties -C $RES metastream-encoders.properties
cp $JAR ../../metafacture-ide/metatext/org.lobid.metatext/