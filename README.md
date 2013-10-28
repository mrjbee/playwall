Play Wall Game Launcher
========
Desktop launcher designed to be controlled from GamePad. Dedicated for keyboard- and mouseless station which using
as game station.

* ___Current development state___ Under BETA testing
* ___Supported OS___ Linux, Windows

######Download [Beta Release] (https://github.com/mrjbee/playwall/releases/tag/Beta)

Check video at Vimeo: https://vimeo.com/78017321

![alt tag](https://raw.github.com/mrjbee/playwall/master/screenshots/Title.png)
![alt tag](https://raw.github.com/mrjbee/playwall/master/screenshots/GameMenu.png)

> * All game related data taken from TheGameDb site (http://thegamesdb.net)
> * Wallpaper - created by rymae (http://rymae.deviantart.com/art/Wallpaper-Game-July-2012-for-FTJ-No-text-337101533)

###Launchers and Launchers format
Launcher - property file which specify items available in PlayWall for execution. Some of the lauchers treated as games 
and available from game menu, some - as system and available in system menu. Launcher specify
which command to execute when particalar item was choosen.

```properties
# Game Title or System Item Name
title = Tomb Raider: Underworld
# [Optional] Id of game in http://thegamesdb.net data base (http://thegamesdb.net/game/67)
id=67 
# Values: game, system. Dedicated for separating game items from system items
type=game
# Command to execute
exec=...
# [Optional] directory from which execute command
dir=...
# Processes to monitor (using to follow process stat and stop)
proc=...
```
###How to Build
Project representet as Maven2+ project. In order to build project artifact, few steps required:

* Install third party dependenices which doesn`t exists in global maven dependecies
 * Go to ___./3dparty___ folder
 * On Linux: Execute `./install.sh` script 
 * On Windows: copy and execute line by line from ___install.sh___ script, which represent mvn commands

* Create build artifact using mvn
 * Go to ___./___ project folder
 * Execute `mvn clean install`

In a target folder find ___play-wall-{version}-jar-with-dependencies.jar___ file. This is all-in-one jar wich could be executed with `java -jar playwall.jar` instruction.

###How to Run
In order to launch application you should use following java (or javaw) syntax:
```
java -jar -Dhome={home-folder} -Djava.library.path={native-libs-folder} {executable.jar}
```
where (without breakets), 
* {home-folder} -  application home folder (should conatin ___background.jpg___ and launchers folder). For example, ___{project root}/example___
* {native-libs-folder} - folder where native libs are placed (see ___3dparty/native___ folder). For example, ___{project root}/3dparty/native___
* {executable.jar} -all-in-one playwall jar. For example, ___play-wall-{version}-jar-with-dependencies.jar___

> You could use Java VM options for run configuration right from your IDE

###Third Party Libraries
* __JInput__ - Java Game Controller API (https://java.net/projects/jinput)
* __Timing Framework__ - a library for making Java animation and timing-based control easier (https://java.net/projects/timingframework) 
* __Web Look and Feel__ - Java Look and Feel for cross-platform Swing applications (http://weblookandfeel.com)
* __Guava Libraries__ - The Guava project contains several of Google's core libraries that we rely on in our Java-based projects: collections, caching, primitives support, concurrency libraries, common annotations, string processing, I/O, and so forth (http://code.google.com/p/guava-libraries)

