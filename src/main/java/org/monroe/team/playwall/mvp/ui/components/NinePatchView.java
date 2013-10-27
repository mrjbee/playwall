package org.monroe.team.playwall.mvp.ui.components;

import com.alee.utils.ninepatch.NinePatchIcon;
import org.monroe.team.playwall.mvp.ui.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: MisterJBee
 * Date: 10/20/13 Time: 2:45 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class NinePatchView extends JPanel{

    private NinePatchIcon icon;

    public NinePatchView(BufferedImage image) {
        icon = new NinePatchIcon(image);
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        icon.paintIcon(this, g);
        super.paint(g);
    }

}
