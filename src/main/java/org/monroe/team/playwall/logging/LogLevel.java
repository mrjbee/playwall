package org.monroe.team.playwall.logging;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 2:59 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public enum LogLevel {

    DEBUG(0),INFO(1),WARN(2),ERROR(3);

    private final int value;

    private LogLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean include(LogLevel logLevel) {
        return value <= logLevel.value;
    }
}
