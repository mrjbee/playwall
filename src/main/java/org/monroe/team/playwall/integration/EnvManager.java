package org.monroe.team.playwall.integration;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 7:45 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class EnvManager {

    public String getAppFolder(){
        return System.getProperty("home",".");
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}
