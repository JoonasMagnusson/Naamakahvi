#!/bin/bash
mvn install:install-file -e -Dfile=javacpp.jar -DgroupId=com.googlecode -DartifactId=javacpp -Dversion=0.1 -Dpackaging=jar
mvn install:install-file -e -Dfile=javacv.jar -DgroupId=com.googlecode -DartifactId=javacv -Dversion=0.1 -Dpackaging=jar

