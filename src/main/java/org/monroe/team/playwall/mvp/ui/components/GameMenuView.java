package org.monroe.team.playwall.mvp.ui.components;

import com.alee.utils.ninepatch.NinePatchIcon;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.monroe.team.playwall.common.CU;
import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.mvp.model.GameDetails;
import org.monroe.team.playwall.mvp.ui.ImageLoader;
import sun.awt.image.ToolkitImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: MisterJBee
 * Date: 10/26/13 Time: 11:12 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class GameMenuView extends JPanel {

    private JPanel contentPanel;
    private JPanel gameContentPanel;
    private NinePatchView coverContentPanel;

    private TextView gameTitleView;
    private SystemItemsView.ItemView launchGameView;
    private SystemItemsView.ItemView goHomeView;
    private GameDetailsView gameDetailsArea;
    private CoverItemView coverItemView;

    private Animator coverHideAnimator;
    private Animator coverShowAnimator;
    private Image noCoverImage = ImageLoader.loadBufImage("no-cover");
    private final ExecutorService fanArtLoader = Executors.newFixedThreadPool(2);
    private UUID lastFanArtLoadingId;
    private GameDetails lastGameDetails;

    public GameMenuView() {
        super();
        backgroundPartImage = ImageLoader.loadImage("game-background.9");
        ninePatchIcon = new NinePatchIcon(backgroundPartImage);
    }

    public void initialize(int screenWidth, int screenHeight){

        setLayout(new GridBagLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(CU.rs(screenWidth, 80), CU.rs(screenHeight, 100)));
        contentPanel.setBackground(Color.RED);
        contentPanel.setOpaque(false);

        add(contentPanel, new GridBagConstraints());

        coverContentPanel = new NinePatchView(ImageLoader.loadBufImage("game-title-back.9"));
        coverContentPanel.setPreferredSize(new Dimension(CU.rs(contentPanel.getPreferredSize().width, 40), CU.rs(screenHeight, 100)));
        coverContentPanel.setLayout(null);

        coverItemView = new CoverItemView(coverContentPanel.getPreferredSize().width, 30);
        coverItemView.moveTo(100);
        coverItemView.setCoverImage(ImageLoader.loadBufImage("no-cover"));
        coverContentPanel.add(coverItemView);


        gameContentPanel = new JPanel();
        gameContentPanel.setLayout(new BoxLayout(gameContentPanel, BoxLayout.Y_AXIS));
        gameContentPanel.setOpaque(false);
        gameContentPanel.setBorder(new EmptyBorder(0, 20, 0, 10));

        gameTitleView = new TextView("Undefined Game", new Color(240,240,240), new Color(0,0,0));
        gameTitleView.setFont(new Font("Serif", Font.BOLD, 25));
        gameTitleView.setHorizontalAlignment(SwingConstants.LEFT);
        gameTitleView.setHorizontalTextPosition(SwingConstants.LEFT);
        gameTitleView.setAlignmentX(0f);
        gameTitleView.setMinimumSize(new Dimension(400, 40));
        //gameTitleView.setOpaque(true);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setAlignmentX(0f);
        launchGameView = new SystemItemsView.ItemView("Play Game");
        launchGameView.setHorizontalAlignment(SwingConstants.CENTER);
        launchGameView.setMaximumSize(new Dimension(200, 50));
        launchGameView.setMinimumSize(new Dimension(200, 50));
        launchGameView.setFont(new Font("Serif", Font.PLAIN, 25));
        goHomeView = new SystemItemsView.ItemView("Back To Home");
        goHomeView.setMinimumSize(new Dimension(200,50));
        goHomeView.setMaximumSize(new Dimension(200, 50));
        goHomeView.setFont(new Font("Serif", Font.PLAIN, 25));
        goHomeView.setHorizontalAlignment(SwingConstants.CENTER);
        buttonsPanel.add(Box.createHorizontalStrut(20));
        buttonsPanel.add(launchGameView);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(goHomeView);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonsPanel.setPreferredSize(new Dimension(100,60));
        buttonsPanel.setMinimumSize(new Dimension(100, 60));
        launchGameView.setSelected(true);

        gameDetailsArea = new GameDetailsView();
        gameDetailsArea.setPreferredSize(new Dimension(100, 600));
        gameDetailsArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        gameDetailsArea.setMinimumSize(new Dimension(0, 600));
        gameDetailsArea.setAlignmentX(0f);
        gameDetailsArea.setEditable(false);
        gameDetailsArea.setOpaque(false);
        gameDetailsArea.setFont(new Font("Serif", Font.BOLD, 20));
        gameDetailsArea.setForeground(new Color(200,200,200));

        gameContentPanel.add(Box.createVerticalStrut(CU.rs(coverContentPanel.getPreferredSize().height,20)));
        gameContentPanel.add(gameTitleView);
        gameContentPanel.add(Box.createVerticalStrut(15));
        gameContentPanel.add(buttonsPanel);
        gameContentPanel.add(Box.createVerticalStrut(10));
        gameContentPanel.add(gameDetailsArea);
        gameContentPanel.add(Box.createVerticalGlue());

        contentPanel.add(coverContentPanel, BorderLayout.WEST);
        contentPanel.add(gameContentPanel, BorderLayout.CENTER);
        noCoverImage = scaleToFitCovert((BufferedImage) noCoverImage);
    }


    public void updateDetails(GameDetails details, Closure<Void, Void> callback, Animation animation) {
        if (animation == Animation.NO_ANIMATION){
            updateDetailsWithNoAnimation(details, callback);
            int y = (coverContentPanel.getHeight() -coverItemView.getHeight())/2;
            coverItemView.moveTo(y);
        } if (animation == Animation.ANIMATE_FROM_TOP){
            updateDetailsWithTopAnimation(details, callback);
        } if (animation == Animation.ANIMATE_FROM_BOTTOM) {
            updateDetailsWithBottomAnimation(details, callback);
        }
    }

    public Image scaleToFitCovert(BufferedImage image) {
        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int dstWidth = coverItemView.getBounds().width - 20;
        int dstHeight = coverItemView.getBounds().height - 20;

        float scaleFactor = Math.max(
                (float)dstWidth/(float)srcWidth,
                (float)dstHeight/(float)srcHeight
        );
        Image answer = image.getScaledInstance(
                (int)Math.round(srcWidth*scaleFactor),
                (int)Math.round(srcHeight*scaleFactor),
                BufferedImage.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(answer.getWidth(null),answer.getHeight(null),BufferedImage.TYPE_4BYTE_ABGR);
        bufferedImage.getGraphics().drawImage(answer,0,0,null);
        return bufferedImage;
    }

    private void updateDetailsWithBottomAnimation(final GameDetails details, final Closure<Void, Void> callback) {
            coverHideAnimator = new Animator(200);
            coverHideAnimator.addTarget(createCoverMoveToUpTimerTask());
            coverHideAnimator.setAcceleration(0.2f);

            coverShowAnimator = new Animator(100);
            coverShowAnimator.setStartDelay(200);
            coverShowAnimator.setDeceleration(0.6f);
            coverShowAnimator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end() {
                    updateDetailsWithNoAnimation(details, callback);
                }
            });
            coverShowAnimator.addTarget(createCoverMoveFromDownTimerTask());
            coverHideAnimator.addTarget(new TimingTargetAdapter(){
                @Override
                public void end() {
                    updateDetailsWithNoAnimation(details, null);
                }
            });
            coverHideAnimator.addTarget(createAnimatorTriggerTimerTask(coverShowAnimator));
            coverHideAnimator.start();
    }



    private void updateDetailsWithTopAnimation(final GameDetails details, final Closure<Void, Void> callback) {
        coverHideAnimator = new Animator(400);
        coverHideAnimator.addTarget(createCoverMoveToDownTimerTask());
        coverHideAnimator.setAcceleration(0.6f);

        coverShowAnimator = new Animator(200);
        coverShowAnimator.setDeceleration(0.6f);
        coverShowAnimator.setStartDelay(200);
        coverShowAnimator.addTarget(new TimingTargetAdapter() {
            @Override
            public void end() {
                updateDetailsWithNoAnimation(details, callback);
            }
        });
        coverShowAnimator.addTarget(createCoverMoveFromUpTimerTask());
        coverHideAnimator.addTarget(new TimingTargetAdapter(){
            @Override
            public void end() {
                updateDetailsWithNoAnimation(details, null);
            }
        });
        coverHideAnimator.addTarget(createAnimatorTriggerTimerTask(coverShowAnimator));
        coverHideAnimator.start();
    }


    private TimingTarget createAnimatorTriggerTimerTask(final Animator coverShowAnimator) {
        return new TimingTargetAdapter(){
            @Override
            public void end() {
                coverShowAnimator.start();
            }
        };
    }

    private TimingTarget createCoverMoveFromUpTimerTask() {
        return new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                int endY = (coverContentPanel.getHeight() -coverItemView.getHeight())/2;
                int startY = - coverItemView.getHeight();
                int delta = Math.round((endY-startY)*fraction);
                coverItemView.moveTo(startY + delta);
            }

        };
    }


    private TimingTarget createCoverMoveFromDownTimerTask() {
        return new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                int endY = (coverContentPanel.getHeight() -coverItemView.getHeight())/2;
                int startY = coverContentPanel.getHeight();
                int delta = Math.round((startY-endY)*fraction);
                coverItemView.moveTo(startY - delta);
            }

        };
    }


    private TimingTarget createCoverMoveToUpTimerTask() {
        return new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                int startY = (coverContentPanel.getHeight() -coverItemView.getHeight())/2;
                int endY = -coverItemView.getHeight();
                int delta = Math.round((endY-startY)*fraction);
                coverItemView.moveTo(startY + delta);
            }

        };
    }


    private TimingTarget createCoverMoveToDownTimerTask() {
        return new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                int startY = (coverContentPanel.getHeight() -coverItemView.getHeight())/2;
                int endY = coverContentPanel.getHeight();
                int delta = Math.round((endY-startY)*fraction);
                coverItemView.moveTo(startY + delta);
            }

        };
    }

    private synchronized void updateDetailsWithNoAnimation(final GameDetails details, Closure<Void, Void> callback) {
        if (lastGameDetails == details){
            if(callback!=null) callback.call(null);
            return;
        }
        hideFunArt();
        gameTitleView.setText(details.gameName);
        if (details.gameDetails == null){
            gameDetailsArea.setText("");
        } else {
            gameDetailsArea.setText(details.gameDetails);
        }
        if (details.prepearedCover == null){
            coverItemView.setCoverImage(noCoverImage);
        } else {
            coverItemView.setCoverImage(details.prepearedCover);
        }
        if (details.fanartImagePath != null){
            final UUID tmp = UUID.randomUUID();
            lastFanArtLoadingId = tmp;
            fanArtLoader.submit(new Runnable() {
                UUID loadingUUID = tmp;
                String pathToLoad = details.fanartImagePath;
                @Override
                public void run() {
                   BufferedImage image = ImageLoader.loadFromPath(pathToLoad);
                    int srcWidth = image.getWidth();
                    int srcHeight = image.getHeight();
                    int dstWidth = getBounds().width;
                    int dstHeight = getBounds().height;

                    float scaleFactor = Math.max(
                            (float)dstWidth/(float)srcWidth,
                            (float)dstHeight/(float)srcHeight
                    );
                    Image answer = image.getScaledInstance(
                            (int)Math.round(srcWidth*scaleFactor),
                            (int)Math.round(srcHeight*scaleFactor),
                            BufferedImage.SCALE_SMOOTH);
                    BufferedImage bufferedImage = new BufferedImage(answer.getWidth(null),answer.getHeight(null),BufferedImage.TYPE_4BYTE_ABGR);
                    Graphics2D rg = bufferedImage.createGraphics();
                    rg.drawImage(answer, 0, 0, null);
                    rg.setComposite( AlphaComposite.getInstance( AlphaComposite.DST_IN ) );
                    //Paint paint = new GradientPaint(0, dstHeight * 0.8f, new Color(0.0f, 0.0f, 0.0f, 0.0f), 0, dstHeight, new Color(0.0f, 0.0f, 0.0f, 0.5f));
                    Paint paint = new RadialGradientPaint(new Point(dstWidth/2,dstHeight/2),dstHeight,
                            new float[]{0f,0.6f,1f},
                            new Color[]{new Color(0.0f, 0.0f, 0.0f, 0.2f), new Color(0.0f, 0.0f, 0.0f, 0.05f),new Color(0.0f, 0.0f, 0.0f, 0f)});
                    rg.setPaint(paint);
                    rg.fillRect( 0, 0, dstWidth, dstHeight);
                    rg.dispose();
                    setFanArtImage(bufferedImage, loadingUUID);
                }
            });
        }

        lastGameDetails = details;
        if (callback != null) callback.call(null);
    }


    private synchronized void setFanArtImage(BufferedImage bufferedImage, UUID loadingUUID) {
        if (loadingUUID != lastFanArtLoadingId) return;
        showFunArt(bufferedImage);
    }

    public void selectNextGameMenuItem() {
        if(launchGameView.isSelected()){
            launchGameView.setSelected(false);
            goHomeView.setSelected(true);
        }else {
            launchGameView.setSelected(true);
            goHomeView.setSelected(false);
        }
    }


    public void pushSelectedGameMenuItem() {
        if(launchGameView.isSelected()){
            launchGameView.setPresed(true);
        }else {
            goHomeView.setPresed(true);
        }
    }

    public void releaseSelectedGameMenuItem() {
        if(launchGameView.isSelected()){
            launchGameView.setPresed(false);
        }else {
            goHomeView.setPresed(false);
        }
    }
    public String getSelectedMenuItem() {
        if(launchGameView.isSelected()){
           return launchGameView.getText();
        }else {
           return goHomeView.getText();
        }
    }

    //DRAW DEDICATED METHODS

    private final NinePatchIcon ninePatchIcon;
    private final Image backgroundPartImage;
    private BufferedImage backgroundImage;
    private BufferedImage funArtImage;

    @Override
    protected void paintComponent(Graphics g) {
          if (backgroundImage == null){
              backgroundImage = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
              int x = 0;
              int widthDelta = backgroundPartImage.getWidth(null);
              while (x<getWidth()){
                  ninePatchIcon.paintIcon((Graphics2D) backgroundImage.getGraphics(),x,-1,widthDelta,getHeight()+1);
                  x+=widthDelta;
              }
          }
          g.drawImage(backgroundImage,0,0,null);
          if (funArtImage != null){
             // Composite composite = ((Graphics2D)g).getComposite();
              //((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
              g.drawImage(funArtImage,0,0,null);
              //((Graphics2D)g).setComposite(composite);
          }
    }

    public void showFunArt(BufferedImage image){
        funArtImage = image;
        repaint();
    }

    private void hideFunArt() {
        funArtImage = null;
        repaint();
    }


    private final static class CoverItemView extends NinePatchView {

        Image coverImage = null;

        public CoverItemView(int parentWidth, int xOffset) {
            super(ImageLoader.loadBufImage("cover-shadow.9"));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            int width = parentWidth - xOffset*2;
            float aspect = 0.75f;
            int height = Math.round(width / aspect);
            setBounds(xOffset, 0, width, height);
        }


        public void moveTo(int y) {
            Rectangle rectangle = getBounds();
            rectangle.setLocation(rectangle.x, y);
            setBounds(rectangle);
        }

        public void setCoverImage(Image image) {
            coverImage = image;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(coverImage, getInsets().left, getInsets().top, null);
        }
    }



    public enum Animation{
        NO_ANIMATION, ANIMATE_FROM_TOP, ANIMATE_FROM_BOTTOM
    }

    private class GameDetailsView extends JTextArea {

        private BufferedImage originImage = null;
        private Animator showAnimator;
        private float opacity = 0.5f;

        public GameDetailsView() {
            super("");
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        @Override
        public void paint(Graphics g) {
            if (getText() == null || getText().isEmpty()){
                return;
            }

            if (originImage == null){
                originImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                super.paint(originImage.getGraphics());
            }
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g.drawImage(originImage,0,0,null);
        }

        @Override
        public void setText(String newText) {
            String wasText = getText();
            if (wasText != null && wasText.equals(newText)){
                return;
            }
            if (showAnimator != null && showAnimator.isRunning()){
                showAnimator.stop();
            }
            originImage = null;
            opacity = 0f;
            super.setText(newText);
            showAnimator = new Animator(500);
            showAnimator.setAcceleration(0.8f);
            showAnimator.setStartDelay(300);
            showAnimator.addTarget(new TimingTargetAdapter(){
                @Override
                public void timingEvent(float fraction) {
                    setOpacity(fraction);
                }
            });
            showAnimator.start();
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
            repaint();
        }
    }

}
