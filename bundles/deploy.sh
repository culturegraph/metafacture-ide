#!/bin/sh
export JAR=target/lodmill-rd-0.1.0-SNAPSHOT-jar-with-dependencies.jar
export RES=src/main/resources
cd ../..
git clone git://github.com/lobid/lodmill.git lodmill-dependency
cd lodmill-dependency/lodmill-rd
git pull origin master
sh install-dependencies.sh
mvn clean assembly:assembly -q --settings ../settings.xml
jar uf $JAR -C $RES flux-commands.properties
cp $JAR ../../metafacture-ide/bundles/org.culturegraph.mf.ide/
cp $JAR ../../metafacture-ide/bundles/org.culturegraph.mf.ide.tests/
