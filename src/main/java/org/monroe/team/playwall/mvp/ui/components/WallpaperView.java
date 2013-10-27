package org.monroe.team.playwall.mvp.ui.components;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.monroe.team.playwall.common.AnimationUtils;
import org.monroe.team.playwall.logging.Logs;
import org.monroe.team.playwall.mvp.ui.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: MisterJBee
 * Date: 10/18/13 Time: 11:20 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class WallpaperView extends JPanel {

    private final BackgroundDrawPanel leftPanel;
    private final BackgroundDrawPanel rightPanel;
    private final ShadowDrawPanel shadowPanel;
    private int maxOpenSize = 0;
    private float openProgress = 0f;
    private Animator openAnimator;

    public WallpaperView() {
        setOpaque(false);
        leftPanel = new BackgroundDrawPanel();
        rightPanel = new BackgroundDrawPanel();
        shadowPanel = new ShadowDrawPanel();
        shadowPanel.srcLeftShadow = ImageLoader.loadImage("wall-left-shadow");
        shadowPanel.srcRightShadow = ImageLoader.loadImage("wall-right-shadow");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(leftPanel);
        add(shadowPanel);
        add(rightPanel);
    }

    public BackgroundDrawPanel getLeftPanel() {
        return leftPanel;
    }

    public void open(){

        if (openAnimator != null &&
                openAnimator.getStartDirection() != Animator.Direction.BACKWARD){
            return;
        }

        float startFraction = 0;
        if (openAnimator!=null && openAnimator.isRunning()){
            openAnimator.stop();
            startFraction = openAnimator.getTimingFraction();
        }
        openAnimator = new Animator(600);
        openAnimator.setAcceleration(0.5f);
        openAnimator.setDeceleration(0.4f);
        openAnimator.setStartFraction(startFraction);
        openAnimator.addTarget(new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                onTimerTick(fraction);
            }
        });
        openAnimator.start();
    }

    private void onTimerTick(float fraction) {
        fraction = AnimationUtils.calculateOverheadFraction(fraction, 1.2f, 0.6f);
        setOpenProgress(fraction);
    }



    public void close(){

        if (openAnimator == null ||
                openAnimator.getStartDirection() == Animator.Direction.BACKWARD){
            return;
        }

        float startFraction = 1f;
        if (openAnimator.isRunning()){
            openAnimator.stop();
            startFraction = openAnimator.getTimingFraction();
        }
        openAnimator = new Animator(400);
        openAnimator.setAcceleration(0.5f);
        openAnimator.setDeceleration(0.4f);
        openAnimator.setStartFraction(startFraction);
        openAnimator.setStartDirection(Animator.Direction.BACKWARD);
        openAnimator.addTarget(new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                onTimerTick(fraction);
            }
        });
        openAnimator.start();
    }

    private void setOpenProgress(float openProgress) {
        this.openProgress = openProgress;
        int currentOpenSize = Math.round(maxOpenSize * openProgress);
        setShadowWidth(currentOpenSize);
        leftPanel.xOffset = currentOpenSize/2;
        leftPanel.repaint();
        updateUI();
    }

    private void setShadowWidth(int shadowWidth) {
        if (shadowWidth == 0){
            shadowPanel.setVisible(false);
        } else {
            shadowPanel.setPreferredSize(new Dimension(shadowWidth, 0));
            shadowPanel.setMaximumSize(new Dimension(shadowWidth, Integer.MAX_VALUE));
            shadowPanel.setVisible(true);
            shadowPanel.updateUI();
            shadowPanel.repaint();
        }
    }

    public void initialize(BufferedImage background, Dimension screenSize, int maxOpenSize) {
        this.maxOpenSize = maxOpenSize;
        int bitmapWidth = background.getWidth();
        int bitmapHeight = background.getHeight();
        shadowPanel.initForHeight(screenSize.height);
        float scaleFactor = Math.max(
                (float)screenSize.width/(float)bitmapWidth,
                (float)screenSize.height/(float)bitmapHeight
                );

        Image scaledBackground = background.getScaledInstance(
                (int)Math.round(bitmapWidth*scaleFactor),
                (int)Math.round(bitmapHeight*scaleFactor),
                BufferedImage.SCALE_SMOOTH);

        BufferedImage leftSideImage = new BufferedImage(screenSize.width/2, screenSize.height, BufferedImage.TYPE_INT_RGB);
        leftSideImage.getGraphics().drawImage(scaledBackground,
                -(int)Math.round((scaledBackground.getWidth(null)-screenSize.width)/2),
                -(int)Math.round((scaledBackground.getHeight(null)-screenSize.height)/2),
                null);
        leftSideImage.getGraphics().dispose();
        leftPanel.image = leftSideImage;

        BufferedImage rightSideImage = new BufferedImage(screenSize.width/2, screenSize.height, BufferedImage.TYPE_INT_RGB);
        rightSideImage.getGraphics().drawImage(scaledBackground,
                -(int)Math.round((scaledBackground.getWidth(null)-screenSize.width)/2) - Math.round(screenSize.width/2),
                -(int)Math.round((scaledBackground.getHeight(null)-screenSize.height)/2),
                null);
        rightSideImage.getGraphics().dispose();
        rightPanel.image = rightSideImage;
        setOpenProgress(openProgress);
        /*new Thread(){
            @Override
            public void run() {
                sleeps(2000);
                while (openProgress < 1){
                    sleeps(50);
                    setOpenProgress(openProgress+0.01f);
                }
            }

            private void sleeps(int i) {
                try {
                    sleep(i);
                } catch (InterruptedException e) {

                }
            }

        }.start(); */
    }


    private final static class ShadowDrawPanel extends JPanel{

        private Image srcLeftShadow;
        private Image srcRightShadow;
        private Image leftShadow;
        private Image rightShadow;


        public void initForHeight(int height){
            leftShadow = getLeftShadowImage(height, leftShadow, srcLeftShadow);
            rightShadow = getLeftShadowImage(height, rightShadow, srcRightShadow);
            leftShadow.getWidth(null);
            rightShadow.getWidth(null);
        }

        @Override
        public void paint(Graphics g) {
            if (leftShadow == null || rightShadow == null)
                return;
            g.drawImage(leftShadow,0,0,null);
            g.drawImage(rightShadow,getWidth()-rightShadow.getWidth(null),0,null);
            g.dispose();
        }

        private Image getLeftShadowImage(int height, Image lastUsedImage, Image origImage) {
            if (lastUsedImage != null && height == lastUsedImage.getHeight(null)){
                return lastUsedImage;
            }
            return origImage.getScaledInstance(origImage.getWidth(null),height,Image.SCALE_SMOOTH);
        }
    };

    private final static class BackgroundDrawPanel extends JPanel{

        private Image image;
        private int xOffset = 0;

        @Override
        protected void paintComponent(Graphics g) {
            if (image != null){
                g.drawImage(image, -xOffset, 0, null);
            }
        }
    };


}
