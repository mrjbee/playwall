package org.monroe.team.playwall.mvp.ui.components;

import com.alee.utils.ninepatch.NinePatchIcon;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.monroe.team.playwall.common.AnimationUtils;
import org.monroe.team.playwall.mvp.ui.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/**
 * User: MisterJBee
 * Date: 10/25/13 Time: 3:21 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class SystemApprovalView extends NinePatchView {

    private final SystemItemsView systemItemsView;
    private Rectangle parentRectangle;
    private final int prefWidth = 350;
    private final int prefHeight = 120;
    private Animator openAnimator;
    private float openProgress;

    private SystemItemsView.ItemView yesView;
    private SystemItemsView.ItemView noView;

    public SystemApprovalView(SystemItemsView systemItemsView, int offset) {
        super(ImageLoader.loadBufImage("popup.9"));
        this.systemItemsView = systemItemsView;
        setVisible(false);
        parentRectangle = systemItemsView.getBounds();
        systemItemsView.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                parentRectangle = e.getComponent().getBounds();
            }
        });
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        updateBounds();
        setLayout(new BorderLayout(5, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridLayout(0, 2, 5, 5));
        yesView = new SystemItemsView.ItemView("Yes");
        noView = new SystemItemsView.ItemView("No");
        yesView.setHorizontalAlignment(SwingConstants.CENTER);
        yesView.setFont(new Font("Serif", Font.PLAIN, 20));
        noView.setHorizontalAlignment(SwingConstants.CENTER);
        noView.setFont(new Font("Serif", Font.PLAIN, 20));
        contentPanel.add(yesView);
        contentPanel.add(noView);
        add(contentPanel, BorderLayout.CENTER);

        MessageView messageView = new MessageView("Are you sure want to continue ?");
        messageView.setFont(new Font("Serif", Font.BOLD, 14));
        messageView.setHorizontalAlignment(SwingConstants.CENTER);

        add(messageView, BorderLayout.NORTH);
        setBorder(new EmptyBorder(30,20,20,20));
    }

    public void updateBounds() {
        setBounds(calculateX(),
                parentRectangle.y+parentRectangle.height-prefHeight,
                prefWidth,prefHeight);
    }

    public void open(){
        if (openAnimator == null) updateBounds();
        if (openAnimator != null &&
                openAnimator.getStartDirection() != Animator.Direction.BACKWARD){
            return;
        }
        setVisible(true);
        setSelected(noView);

        float startFraction = 0;
        if (openAnimator!=null && openAnimator.isRunning()){
            openAnimator.stop();
            startFraction = openAnimator.getTimingFraction();
        }
        openAnimator = new Animator(200);
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

    private void setSelected(SystemItemsView.ItemView selectView) {
        if(selectView.isSelected()) return;

        if (selectView == yesView){
            yesView.setSelected(true);
            noView.setSelected(false);
        } else {
            yesView.setSelected(false);
            noView.setSelected(true);
        }
    }

    private void onTimerTick(float fraction) {
        fraction = AnimationUtils.calculateOverheadFraction(fraction, 1.2f, 0.6f);
        setOpenProgress(fraction);
    }

    public void close(final Runnable closeDecisionMenuCallback){
        if (openAnimator == null ||
                openAnimator.getStartDirection() == Animator.Direction.BACKWARD){
            return;
        }

        float startFraction = 1f;
        if (openAnimator.isRunning()){
            openAnimator.stop();
            startFraction = openAnimator.getTimingFraction();
        }
        openAnimator = new Animator(200);
        openAnimator.setAcceleration(0.5f);
        openAnimator.setStartFraction(startFraction);
        openAnimator.setStartDirection(Animator.Direction.BACKWARD);
        openAnimator.addTarget(new TimingTargetAdapter(){
            Animator owner = openAnimator;
            @Override
            public void timingEvent(float fraction) {
                onTimerTick(fraction);
            }


            @Override
            public void end() {
                if(owner.getTimingFraction() == 0f){
                   if(closeDecisionMenuCallback != null)
                        closeDecisionMenuCallback.run();
                   SystemApprovalView.this.setVisible(false);
                };
            }
        });
        openAnimator.start();
    }

    private void setOpenProgress(float progress) {
        openProgress = progress;
        int maxHeight = getPreferredSize().height;
        int shownArea = Math.round(maxHeight*0.8f*progress);
        int y = (parentRectangle.y+parentRectangle.height)-maxHeight+shownArea;
        setBounds(calculateX(), y, getPreferredSize().width, getPreferredSize().height);
        updateUI();
    }

    private int calculateX() {
        return parentRectangle.x+(parentRectangle.width-prefWidth)/2;
    }

    @Override
    public void paint(Graphics g) {
        int clipX=0;
        int clipY=parentRectangle.y+parentRectangle.height-getBounds().y-10;
        g.setClip(clipX,clipY,getWidth(),getHeight());
        super.paint(g);
    }

    public void pushSelected() {
        if (yesView.isSelected()){
            yesView.setPresed(true);
        } else {
            noView.setPresed(true);
        }
    }

    public boolean isYes() {
        return yesView.isSelected();
    }

    public void releaseSelected() {
        if (yesView.isSelected()){
            yesView.setPresed(false);
        } else {
            noView.setPresed(false);
        }
    }

    public void changeSelection() {
        if (yesView.isSelected()){
           setSelected(noView);
        } else {
            setSelected(yesView);
        }
    }

    //TODO: replace with TextView
    public static class MessageView extends JLabel{

        private final Color color;
        private final Color shadowColor;

        private BufferedImage originImage = null;
        private BufferedImage deSelectedStateImage = null;

        public MessageView(String text) {
            super(text);
            color = new Color(200,200,200);
            shadowColor = new Color(50,50,50);
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

    }


}
