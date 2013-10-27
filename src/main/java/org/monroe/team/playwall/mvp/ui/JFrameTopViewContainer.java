package org.monroe.team.playwall.mvp.ui;

import org.monroe.team.playwall.logging.Logs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 4:07 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class JFrameTopViewContainer extends JFrame implements TopViewContainer {

    public JFrameTopViewContainer(final int width, final int height) {
        setTitle("PlayWall");
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setResizable(false);
        //Works on Windows but not on Linux
        //setExtendedState(Frame.MAXIMIZED_BOTH);
        //setState(Frame.MAXIMIZED_BOTH);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Logs.MAIN.w(null, "Component resized but shouldn`t. Event = %s", e);
                setSize(width, height);
                setPreferredSize(new Dimension(width, height));
                setMaximumSize(new Dimension(width, height));
                setMinimumSize(new Dimension(width, height));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                Logs.MAIN.w(null, "Component moved but shouldn`t. Event = %s", e);
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    @Override
    public void open() {
        setVisible(true);
    }

    @Override
    public void close() {
        setVisible(false);
    }

    @Override
    public void setContent(Container container) {
        setContentPane(container);
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the JFrame.
        getContentPane().setCursor(blankCursor);
        getLayeredPane().setCursor(blankCursor);
    }

    @Override
    public void addLayer(Container container, int level, boolean fitToContainer) {
        if (fitToContainer){
            container.setBounds(0, 0, this.getWidth(), this.getHeight());
        }
        getLayeredPane().add(container, 50 + level, 0);
    }


}
