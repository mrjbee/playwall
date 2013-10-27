package org.monroe.team.playwall.mvp.ui.components;

import org.monroe.team.playwall.mvp.ui.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: MisterJBee
 * Date: 10/26/13 Time: 3:33 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class LogoView extends JLabel {
    public LogoView(JPanel panel) {
        super(ImageLoader.loadImageAsIcon("logo"));
        setPreferredSize(new Dimension(151,151));
        setBounds(0,0,151,151);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                JPanel owner = (JPanel) e.getComponent();
                LogoView view = LogoView.this;
                int width = owner.getWidth();
                if (width > view.getWidth()){
                    view.setBounds(0,0,151,151);
                } else {
                    view.setBounds(0-151+width, 0, 151, 151);
                }
                view.updateUI();
            }
        });
    }
}
