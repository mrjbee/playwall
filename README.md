Play Wall Game Launcher
========
Desktop launcher designed to be controlled from GamePad. Dedicated for keyboard- and mouseless station which using
as game station.

* ___Current development state___ Under BETA testing
* ___Supported OS___ Linux, Windows


###Launchers and Launchers format
###How to build
Project representet as Maven2+ project. In order to build project artifact, few steps required:

* Install third party dependenices which doesn`t exists in global maven dependecies
 * Go to ___./3dparty___ folder
 * On Linux: Execute ___install.sh___ script 
 * On Windows: copy and execute line by line from ___install.sh___ script, whic represent mvn commands

* Create build artifact using mvn
 * Go to ___./___ project folder
 * Execute __mvn clean install__

In a target folder find ___play-wall-{version}-jar-with-dependencies.jar___ file. This is all-in-one jar wich could be executed with "___java -jar playwall.jar___" instruction.

###How to run

###Third Party Libraries
* __JInput__ - Java Game Controller API (https://java.net/projects/jinput)
* __Timing Framework__ - a library for making Java animation and timing-based control easier (https://java.net/projects/timingframework) 
* __Web Look and Feel__ - Java Look and Feel for cross-platform Swing applications (http://weblookandfeel.com)
* __Guava Libraries__ - The Guava project contains several of Google's core libraries that we rely on in our Java-based projects: collections, caching, primitives support, concurrency libraries, common annotations, string processing, I/O, and so forth (http://code.google.com/p/guava-libraries)

