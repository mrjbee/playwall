package org.monroe.team.playwall.integration;

import java.awt.*;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 4:25 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class DesktopManager {
    public int[] getScreenSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        return new int[]{(int)width,(int)height};
        //return new int[]{200,200};
    }
}
