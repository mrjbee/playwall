#!/bin/bash
mvn install:install-file -Dfile=lib/jinput.jar -DgroupId=jinput  -DartifactId=jinput -Dversion=nightly_20131016 -Dpackaging=jar
mvn install:install-file -Dfile=lib/timingframework-classic-1.1.jar -DgroupId=net.java.dev.timingframework  -DartifactId=timingframework -Dversion=1.1 -Dpackaging=jar
mvn install:install-file -Dfile=lib/weblaf-1.24.jar -DgroupId=weblaf -DartifactId=weblaf -Dversion=1.24 -Dpackaging=jar
