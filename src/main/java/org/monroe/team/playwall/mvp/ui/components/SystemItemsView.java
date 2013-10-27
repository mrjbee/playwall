package org.monroe.team.playwall.mvp.ui.components;

import com.alee.utils.ninepatch.NinePatchIcon;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.monroe.team.playwall.common.AnimationUtils;
import org.monroe.team.playwall.mvp.ui.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/20/13 Time: 2:44 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class SystemItemsView extends NinePatchView {

    private int xPosition = 0;
    private float openProgress = 0f;
    private Animator openAnimator;
    private List<Item> model;
    private Item selectedItem;
    private Map<Item, ItemView> itemViewMap = new HashMap<Item, ItemView>();
    private JPanel rootContainerPanel;

    public SystemItemsView() {
        super(ImageLoader.loadBufImage("popup.9"));
    }

    public void initialize(int desireWidth, int desireHeight, int positionX){
        //initialize();
        xPosition = positionX;
        setPreferredSize(new Dimension(desireWidth, desireHeight));
        setLayout(new BorderLayout(0, 0));
        add(getPopupContent(), BorderLayout.CENTER);
        setOpenProgress(0f);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void setOpenProgress(float progress) {
        openProgress = progress;
        int maxHeight = Math.round(getPreferredSize().height);
        int shownArea = Math.round(maxHeight*0.8f*progress);
        setBounds(xPosition, 0-maxHeight+shownArea, getPreferredSize().width, maxHeight);
        updateUI();
    }

    public void installItems(List<Item> itemList) {
        JPanel rootContainer = (JPanel) getPopupContent();
        rootContainer.removeAll();
        //TODO: may require previous view that they are not actual any more
        itemViewMap.clear();
        model = Collections.unmodifiableList(itemList);

        int imageViewHeight = 50;
        int topmostViewHeight = 20;

        rootContainer.add(Box.createVerticalStrut(30));
        rootContainer.add(Box.createVerticalStrut(topmostViewHeight));
        int rootContainerWidth = 30 + model.size() * imageViewHeight + topmostViewHeight + (model.size()-1)*2;
        rootContainer.setPreferredSize(new Dimension(rootContainerWidth,0));
        NinePatchView separatorView = new NinePatchView(ImageLoader.loadBufImage("hor-separ.2.9"));
        separatorView.setPreferredSize(new Dimension(0,6));
        separatorView.setMaximumSize(new Dimension(Integer.MAX_VALUE,6));
        rootContainer.add(separatorView);
        rootContainer.add(Box.createVerticalStrut(6));
        for (Item item : model) {
            ItemView itemView = new ItemView(item.title);
            itemView.setHorizontalAlignment(SwingConstants.CENTER);
            itemView.setFont(new Font("Serif", Font.PLAIN, 34));
            itemView.setPreferredSize(new Dimension(rootContainerWidth, imageViewHeight));
            itemView.setMaximumSize(new Dimension(Integer.MAX_VALUE, imageViewHeight));
            separatorView = new NinePatchView(ImageLoader.loadBufImage("hor-separ.9"));
            separatorView.setPreferredSize(new Dimension(0,2));
            separatorView.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            separatorView.setMinimumSize(new Dimension(0, 2));
            itemViewMap.put(item, itemView);
            rootContainer.add(itemView);
            rootContainer.add(Box.createVerticalStrut(6));
            rootContainer.add(separatorView);
        }
        setPreferredSize(new Dimension(getPreferredSize().width, rootContainerWidth + 20 + 40));
        itemViewMap.get(model.get(0)).setSelected(true);
        selectedItem = model.get(0);
    }

    private JPanel getPopupContent() {
        if (rootContainerPanel == null){
            rootContainerPanel = new JPanel();
            rootContainerPanel.setOpaque(false);
            rootContainerPanel.setLayout(new BoxLayout(rootContainerPanel,BoxLayout.Y_AXIS));
            rootContainerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        }
        return rootContainerPanel;
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
        openAnimator = new Animator(400);
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
        fraction = AnimationUtils.calculateOverheadFraction(fraction,1.2f,0.6f);
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
        openAnimator = new Animator(200);
        openAnimator.setAcceleration(0.5f);
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

    public synchronized void selectNext() {
        int selectIndx = model.indexOf(selectedItem);
        selectIndx++;
        if(selectIndx>=model.size()){
           selectIndx = 0;
        }
        Item newSelectItem = model.get(selectIndx);
        ItemView newSelectView = itemViewMap.get(newSelectItem);
        ItemView selectView = itemViewMap.get(selectedItem);
        selectView.setSelected(false);
        newSelectView.setSelected(true);
        selectedItem = newSelectItem;
    }

    public synchronized void selectPrev() {
        int selectIndx = model.indexOf(selectedItem);
        selectIndx--;
        if(selectIndx < 0){
            selectIndx = model.size()-1;
        }
        Item newSelectItem = model.get(selectIndx);
        ItemView newSelectView = itemViewMap.get(newSelectItem);
        ItemView selectView = itemViewMap.get(selectedItem);
        selectView.setPresed(false);
        selectView.setSelected(false);
        newSelectView.setSelected(true);
        selectedItem = newSelectItem;
    }

    public void pressSelected() {
        ItemView selectView = itemViewMap.get(selectedItem);
        selectView.setPresed(true);
    }

    public void releaseSelected() {
        ItemView selectView = itemViewMap.get(selectedItem);
        selectView.setPresed(false);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }


    public static class ItemView extends JLabel {

        private final Color color;
        private final Color shadowColor;
        private final Color selectedColor;
        private final Color seletedShadowColor;

        private BufferedImage originImage = null;
        private BufferedImage deSelectedStateImage = null;
        private BufferedImage selectedStateImage = null;
        private BufferedImage presedStateImage = null;
        private boolean selected = false;
        private boolean presed = false;
        private final NinePatchIcon selectedBackground;
        private final NinePatchIcon pressedBackground;

        private float progress = 1f;
        private Animator animator = null;

        public ItemView(String text) {
            super(text);
            color = new Color(180,180,180);
            shadowColor = new Color(50,50,50);
            selectedColor = new Color(45,45,45);
            seletedShadowColor = new Color(220,220,220);
            setForeground(Color.WHITE);
            selectedBackground = new NinePatchIcon(ImageLoader.loadImage("btn.9"));
            pressedBackground = new NinePatchIcon(ImageLoader.loadImage("btn.pressed.9"));
        }

        @Override
        public void paint(Graphics g) {
            if (progress > 0.9f || presed){
                singleBitmpaDraw(g);
            } else {
                Graphics2D g2D = (Graphics2D) g;
                BufferedImage wasImage,newImage;
                if (selected){
                    wasImage = getUsualImage();
                    newImage = getSelectedImage();
                } else {
                    wasImage = getSelectedImage();
                    newImage = getUsualImage();
                }
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f-progress));
                g2D.drawImage(wasImage,0,0,null);
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,progress));
                g2D.drawImage(newImage,0,0,null);

            }
        }

        private void singleBitmpaDraw(Graphics g) {
            Image imageToDraw = null;
            if (selected){
                if(!presed){
                    imageToDraw = getSelectedImage();
                } else {
                    imageToDraw = getPressedImage();
                }
            } else {
                imageToDraw = getUsualImage();
            }
            g.drawImage(imageToDraw,0,0,null);
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

        private BufferedImage getPressedImage() {
            if (presedStateImage == null){
                presedStateImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                pressedBackground.paintIcon(presedStateImage.createGraphics(),
                        0,0,
                        presedStateImage.getWidth(),presedStateImage.getHeight());
                Graphics2D imageG2 = getOrigImage().createGraphics();
                imageG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1f));
                drawImageWithColor(1, 1, seletedShadowColor,presedStateImage.getGraphics(), imageG2);
                drawImageWithColor(0, 0, selectedColor, presedStateImage.getGraphics(), imageG2);
            }
            return presedStateImage;
        }

        private BufferedImage getSelectedImage() {
            if (selectedStateImage == null){
                selectedStateImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                selectedBackground.paintIcon(selectedStateImage.createGraphics(),
                        0,0,
                        selectedStateImage.getWidth(),selectedStateImage.getHeight());
                Graphics2D imageG2 = getOrigImage().createGraphics();
                imageG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1f));
                drawImageWithColor(1, 1, seletedShadowColor, selectedStateImage.getGraphics(), imageG2);
                drawImageWithColor(0, 0, selectedColor, selectedStateImage.getGraphics(), imageG2);
            }
            return selectedStateImage;
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

        public void setSelected(boolean selected) {
            boolean wasValue = this.selected;
            this.selected = selected;
            if (wasValue != selected){
               if (animator != null && animator.isRunning()){
                   animator.stop();
               }
                //Lets animation beggins
               animator = new Animator(300);
               animator.setStartFraction(1f - progress);
               animator.addTarget(new TimingTargetAdapter(){
                   @Override
                   public void timingEvent(float fraction) {
                       progress = fraction;
                       ItemView.this.repaint();
                   }
               });
               animator.start();
            }
        }

        public void setPresed(boolean presed) {
            this.presed = presed;
            if (animator != null){
                animator.stop();
            }
            progress = 1f;
            ItemView.this.repaint();
        }

        public boolean isSelected() {
            return selected;
        }
    }

    public static class Item {

        public final String id;
        public final String title;
        public final String imageId;

        public Item(String id, String title, String imageId) {
            this.id = id;
            this.title = title;
            this.imageId = imageId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (id != null ? !id.equals(item.id) : item.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", imageId='" + imageId + '\'' +
                    '}';
        }
    }
}
