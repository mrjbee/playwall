package org.monroe.team.playwall.mvp.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: MisterJBee
 * Date: 10/26/13 Time: 11:48 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class TextView extends JLabel{
    private final Color color;
    private final Color shadowColor;

    private BufferedImage originImage = null;
    private BufferedImage deSelectedStateImage = null;

    public TextView(String text) {
        this(text, new Color(200, 200, 200), new Color(50, 50, 50));

    }

    public TextView(String text, Color color, Color shadowColor){
        super(text);
        this.color = color;
        this.shadowColor = shadowColor;
        setForeground(Color.WHITE);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(getUsualImage(), 0, 0, null);
    }

    private BufferedImage getUsualImage() {
        if (deSelectedStateImage == null){
            deSelectedStateImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D imageG2 = getOrigImage().createGraphics();
            imageG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1f));
            drawImageWithColor(2, 2, shadowColor, deSelectedStateImage.getGraphics(), imageG2);
            drawImageWithColor(0, 0, color, deSelectedStateImage.getGraphics(), imageG2);
        }
        return deSelectedStateImage;
    }


    private BufferedImage getOrigImage() {
        if (originImage == null){
            originImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D imageG2 = (Graphics2D) originImage.getGraphics();
            imageG2.setComposite(AlphaComposite.SrcAtop);
            super.paint(originImage.getGraphics());
        }
        return originImage;
    }

    private void drawImageWithColor(int xOffset, int yOffset, Color color,
                                    Graphics targetCanvas, Graphics2D imageCanvas) {
        imageCanvas.setColor(color);
        imageCanvas.fillRect(0, 0, originImage.getWidth(), originImage.getHeight());
        targetCanvas.drawImage(originImage, xOffset, yOffset, null);
    }

    @Override
    public void setText(String text) {
        originImage = null;
        deSelectedStateImage = null;
        super.setText(text);
    }
}
