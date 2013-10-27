package org.monroe.team.playwall.mvp.ui.components;

import org.monroe.team.playwall.mvp.ui.ImageLoader;

import javax.swing.*;
import java.awt.*;

/**
 * User: MisterJBee
 * Date: 10/20/13 Time: 2:45 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public abstract class AbstractPopupView extends JPanel{
    private final ImagePanel leftSidePanel;
    private final ImagePanel rightSidePanel;
    private final ImagePanel bottomSidePanel;
    private final ImagePanel bottomRightCornerPanel;
    private final ImagePanel bottomLeftCornerPanel;

    protected AbstractPopupView() {
        leftSidePanel = new ImagePanel();
        rightSidePanel = new ImagePanel();
        bottomLeftCornerPanel = new ImagePanel();
        bottomRightCornerPanel = new ImagePanel();
        bottomSidePanel = new ImagePanel();
    }

    protected void initialize(){
        setOpaque(false);
        setLayout(new BorderLayout(0,0));
        add(leftSidePanel, BorderLayout.LINE_START);
        add(rightSidePanel,BorderLayout.LINE_END);
        JPanel bottomBufPanel = new JPanel();
        bottomBufPanel.setOpaque(false);
        add(bottomBufPanel, BorderLayout.PAGE_END);
        bottomBufPanel.setLayout(new BorderLayout(0, 0));
        bottomBufPanel.add(bottomLeftCornerPanel, BorderLayout.LINE_START);
        bottomBufPanel.add(bottomRightCornerPanel, BorderLayout.LINE_END);
        bottomBufPanel.add(bottomSidePanel, BorderLayout.CENTER);

        bottomLeftCornerPanel.imageToDraw = ImageLoader.loadImage("popup-shadow-bottom-left");
        int bottomHeight = bottomLeftCornerPanel.imageToDraw.getHeight(null);
        bottomLeftCornerPanel.setPreferredSize(new Dimension(bottomLeftCornerPanel.imageToDraw.getWidth(null),bottomHeight));

        bottomRightCornerPanel.imageToDraw = ImageLoader.loadImage("popup-shadow-bottom-right");
        bottomLeftCornerPanel.setPreferredSize(new Dimension(bottomRightCornerPanel.imageToDraw.getWidth(null),bottomHeight));

        bottomSidePanel.imageToDraw = ImageLoader.loadImage("popup-shadow-bottom");
        bottomSidePanel.copyDirection = ImagePanel.CopyDirection.X;

        leftSidePanel.imageToDraw = ImageLoader.loadImage("popup-shadow-left");
        leftSidePanel.setPreferredSize(new Dimension(leftSidePanel.imageToDraw.getWidth(null),0));
        leftSidePanel.copyDirection = ImagePanel.CopyDirection.Y;

        rightSidePanel.imageToDraw = ImageLoader.loadImage("popup-shadow-right");
        rightSidePanel.setPreferredSize(new Dimension(rightSidePanel.imageToDraw.getWidth(null),0));
        rightSidePanel.copyDirection = ImagePanel.CopyDirection.Y;

        add(getPopupContent(),BorderLayout.CENTER);
    }

    protected abstract Component getPopupContent();


    private static final class ImagePanel extends JPanel{
        private Image imageToDraw;
        private CopyDirection copyDirection = CopyDirection.NONE;

        @Override
        protected void paintComponent(Graphics g) {
            if (imageToDraw == null) return;
            if (copyDirection == CopyDirection.NONE){
                g.drawImage(imageToDraw,0,0,null);
            } else if (copyDirection == CopyDirection.X){
                int width = getWidth();
                int positionToDraw = 0;
                while (positionToDraw < width){
                    g.drawImage(imageToDraw,positionToDraw,0,null);
                    positionToDraw=positionToDraw + imageToDraw.getWidth(null);
                }
            } else if (copyDirection == CopyDirection.Y){
                int height = getHeight();
                int positionToDraw = 0;
                while (positionToDraw < height){
                    g.drawImage(imageToDraw,0,positionToDraw,null);
                    positionToDraw=positionToDraw + imageToDraw.getHeight(null);
                }
            }
        }

        enum CopyDirection{
            NONE,X,Y;
        }
    }
}
