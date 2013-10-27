package org.monroe.team.playwall.logging;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 3:09 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class Logs {
    public static final Logger DEBUG = new ConsoleLogger("app.MAIN",LogLevel.DEBUG);
    public static final Logger MAIN = new ConsoleLogger("app.MAIN",LogLevel.DEBUG);
    public static final Logger JINPUT = new ConsoleLogger("app.JINPUT",LogLevel.INFO);
    public static final Logger GAME_DETAILS = new ConsoleLogger("app.GAME_DETAILS",LogLevel.INFO);
}
