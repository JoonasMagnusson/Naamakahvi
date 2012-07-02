#!/bin/bash
mvn install:install-file -Dfile=opencv-2.4.1.jar -DgroupId=org.opencv \
    -DartifactId=opencv -Dversion=2.4.1 -Dpackaging=jar -DgeneratePom=true
