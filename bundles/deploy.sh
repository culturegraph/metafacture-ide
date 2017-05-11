#!/bin/bash
set -eu

VERSION=4.0.0
JAR1=target/metafacture-core-$VERSION-jar-with-dependencies.jar
JAR2=target/metafacture-runner-$VERSION-jar-with-dependencies.jar

cd ../..
git clone git://github.com/culturegraph/metafacture-core.git metafacture-core-$VERSION || true
cd metafacture-core-$VERSION
git checkout tags/metafacture-core-$VERSION
mvn clean assembly:assembly -q -DskipTests

cp $JAR1 ../metafacture-ide/bundles/org.culturegraph.mf.ide/
cp $JAR1 ../metafacture-ide/bundles/org.culturegraph.mf.ide.tests/

cd ..
git clone git://github.com/culturegraph/metafacture-runner.git metafacture-runner-$VERSION || true
cd metafacture-runner-$VERSION
git checkout tags/metafacture-runner-$VERSION
mvn clean assembly:assembly -q -DskipTests

cp $JAR2 ../metafacture-ide/bundles/org.culturegraph.mf.ide/
