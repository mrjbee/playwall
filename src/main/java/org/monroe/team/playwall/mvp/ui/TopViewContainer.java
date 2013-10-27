package org.monroe.team.playwall.mvp.ui;

import org.monroe.team.playwall.mvp.model.PublicModel;

import java.awt.*;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 3:53 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public interface TopViewContainer {
    public void open();
    public void close();
    public void setContent(Container container);
    public void addLayer(Container container, int level, boolean fitToContainer);
    public Dimension getSize();

}
