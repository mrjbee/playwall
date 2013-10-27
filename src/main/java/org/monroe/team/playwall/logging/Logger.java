package org.monroe.team.playwall.logging;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 2:52 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public interface Logger {
    public void i(String msgFormat, Object... args);
    public void d(String msgFormat, Object... args);
    public void w(Throwable th, String msgFormat, Object... args);
    public void e(Throwable th, String msgFormat, Object... args);
}
