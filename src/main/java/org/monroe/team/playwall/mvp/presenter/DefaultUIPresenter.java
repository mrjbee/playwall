package org.monroe.team.playwall.mvp.presenter;

import org.monroe.team.playwall.ApplicationContext;
import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.gamepad.GamePadManager;
import org.monroe.team.playwall.integration.FSManager;
import org.monroe.team.playwall.lifecycle.ShutdownManager;
import org.monroe.team.playwall.logging.Logs;
import org.monroe.team.playwall.mvp.model.GameDetails;
import org.monroe.team.playwall.mvp.model.Launcher;
import org.monroe.team.playwall.mvp.model.PublicModel;
import org.monroe.team.playwall.mvp.presenter.state.GameMenuState;
import org.monroe.team.playwall.mvp.presenter.state.SystemActionState;
import org.monroe.team.playwall.mvp.ui.TopViewContainer;
import org.monroe.team.playwall.mvp.ui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/18/13 Time: 10:03 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class DefaultUIPresenter extends AbstractStateUIPresenter<DefaultUIPresenter.AbstractDefaultUIPresenterState>{

    private TopViewContainer topViewContainer;
    private final GameMenuView gameMenuView;
    private final WallpaperView wallpaperView;
    private SystemItemsView systemItemsView;
    private SystemApprovalView systemApprovalView;

    private PublicModel publicModel;

    private final ShutdownManager shutdownManager;
    private final GamePadManager gamePadManager;
    private final FSManager fsManager;

    private KeyEventDispatcher keyEventDispatcher;

    private boolean blockInput = true;

    private SystemActionState systemActionPresentState;
    private GameMenuState gameMenuPresentState;


    public DefaultUIPresenter(ApplicationContext context){

        shutdownManager = context.getManager(ShutdownManager.class);
        gamePadManager = context.getManager(GamePadManager.class);
        fsManager = context.getManager(FSManager.class);

        wallpaperView = new WallpaperView();
        gameMenuView = new GameMenuView();
        systemActionPresentState = new SystemActionState("system", this);
        gameMenuPresentState = new GameMenuState("game",this);
    }

    public void initialize(){
        if (!gamePadManager.isRunning()){
            gamePadManager.start();
        }

        gamePadManager.setListener(initGamePadListener());
        KeyboardFocusManager manager =  KeyboardFocusManager.getCurrentKeyboardFocusManager();
        initializeKeyboardListener();
        manager.addKeyEventDispatcher(keyEventDispatcher);
        installState(systemActionPresentState);
        installState(gameMenuPresentState);
    }

    @Override
    protected AbstractDefaultUIPresenterState showUIImpl(PublicModel model) {
        Logs.MAIN.d("showUI");
        publicModel = model;
        subscribeOnModel(model);
        topViewContainer.open();
        /*publicModel.requestLauncherListUpdate();
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(1000);
                    systemItemsView.open();
                    Thread.currentThread().sleep(2000);
                    wallpaperView.open();
                } catch (InterruptedException e) {}
            }
        }.start(); */
        return systemActionPresentState;
    }

    private void subscribeOnModel(PublicModel model) {
        publicModel.setListener(new PublicModel.PublicModelListener() {
            @Override
            public void onLauncherListUpdateDone(final java.util.List<Launcher> launcherList) {
                Logs.MAIN.i("Retrieved launcher list = %s", launcherList);
                //simpleLauncherListView.installLaunchersList(launcherList);
                //publicModel.navigateToNextLauncher();
                notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
                    @Override
                    public Void call(AbstractDefaultUIPresenterState in) {
                        in.onLauncherListUpdate(launcherList);
                        return null;
                    }
                });
            }

            @Override
            public void onNavigationDone(Launcher launcher) {
                //simpleLauncherListView.selectLauncher(launcher);
                //blockInput = false;
            }

            @Override
            public void onProcessStart(final Launcher launcher) {
                Logs.MAIN.i("Process starts. Launcher = %s", launcher);
                notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
                    @Override
                    public Void call(AbstractDefaultUIPresenterState in) {
                        in.onProcessStart(launcher);
                        return null;
                    }
                });
            }

            @Override
            public void onProcessFailToStart(final Launcher launcher) {
                Logs.MAIN.i("Process fails to start. Launcher = %s", launcher);
                gamePadManager.start();
                notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
                    @Override
                    public Void call(AbstractDefaultUIPresenterState in) {
                        in.onProcessFail(launcher);
                        return null;
                    }
                });
            }

            @Override
            public void onProcessStop(final Launcher launcher) {
                Logs.MAIN.i("Process stop. Launcher = %s", launcher);
                gamePadManager.start();
                notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
                    @Override
                    public Void call(AbstractDefaultUIPresenterState in) {
                        in.onProcessStop(launcher);
                        return null;
                    }
                });
                //blockInput = false;
                //simpleLauncherListView.setStatus("Process stop. Launcher =" + launcher);
            }
        });
    }


    @Override
    protected void closeUIImpl() {
        //Cause hang up in Windows since running in hook thread
        //topViewContainer.close();
        Logs.MAIN.d("closeUI");
        gamePadManager.setListener(null);
        gamePadManager.stop();
        publicModel.setListener(null);
        publicModel = null;
        KeyboardFocusManager manager =  KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.removeKeyEventDispatcher(keyEventDispatcher);
    }

    @Override
    public void installTopViewContainer(TopViewContainer viewContainer) {
        topViewContainer = viewContainer;
        wallpaperView.initialize(
                fsManager.getBackgroundImage(),
                viewContainer.getSize(),
                viewContainer.getSize().width-200);
        gameMenuView.initialize(topViewContainer.getSize().width,topViewContainer.getSize().height);
        //TODO: Remove workaround onMouseClick elsewhere - exit
        topViewContainer.addLayer(createStubExitPanel(topViewContainer),10,true);

        topViewContainer.setContent(gameMenuView);
        topViewContainer.addLayer(wallpaperView, 5, true);
        systemItemsView = new SystemItemsView();
        systemItemsView.initialize(400,150,topViewContainer.getSize().width-400-10);
        topViewContainer.addLayer(systemItemsView, 7, false);
        systemApprovalView = new SystemApprovalView(systemItemsView,50);
        topViewContainer.addLayer(systemApprovalView,6,false);
        topViewContainer.addLayer(new LogoView(wallpaperView.getLeftPanel()),9,false);
    }

    private Container createStubExitPanel(TopViewContainer topViewContainer) {
        JPanel panel = new JPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                shutdownManager.shutdown();
            }
        });
        panel.setOpaque(false);
        return panel;
    }


    private void onBackUp() {
        Logs.MAIN.d("onBackUp");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onBackUp();
                return null;
            }
        });
        //if(blockInput) return;
    }

    private void onApplyUp() {
        Logs.MAIN.d("onApplyUp");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onApplyUp();
                return null;
            }
        });
        //publicModel.launch();
        //blockInput = true;
    }

    private void onBackDown() {
        Logs.MAIN.d("onBackDown");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onBackDown();
                return null;
            }
        });

        //if(blockInput) return;
    }

    private void onApplyDown() {
        Logs.MAIN.d("onApplyDown");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onApplyDown();
                return null;
            }
        });

        //if(blockInput) return;
    }

    private void onPrev() {
        Logs.MAIN.d("onPrev");
        //if(blockInput) return;
        //blockInput = true;
        //publicModel.navigateToPrevLauncher();
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onUp();
                return null;
            }
        });

    }

    private void onNext() {
        Logs.MAIN.d("onNext");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onDown();
                return null;
            }
        });
    }

    private void onLeft() {
        Logs.MAIN.d("onLeft");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onLeft();
                return null;
            }
        });
    }

    private void onRight() {
        Logs.MAIN.d("onRight");
        notifyUIState(new Closure<AbstractDefaultUIPresenterState, Void>() {
            @Override
            public Void call(AbstractDefaultUIPresenterState in) {
                in.onRight();
                return null;
            }
        });
    }



    private GamePadManager.GamePadManagerListener initGamePadListener() {
        return new GamePadManager.GamePadManagerListener() {
            @Override
            public void onButtonStateChange(java.util.List<Integer> releasedBtnsIndx, java.util.List<Integer> pressedBtnsIndx) {
                for (Integer btnIndex : pressedBtnsIndx) {
                    if (btnIndex == 0){
                        onApplyDown();
                    }
                    if (btnIndex == 1){
                        onBackDown();
                    }
                }

                for (Integer btnIndex : releasedBtnsIndx) {
                    if (btnIndex == 0){
                        onApplyUp();
                    }
                    if (btnIndex == 1){
                        onBackUp();
                    }
                }

            }

            @Override
            public void onPovDirectionChanged(GamePadManager.PovDirection oldPovDirection, GamePadManager.PovDirection newPovDirection) {
                if (newPovDirection == GamePadManager.PovDirection.SOUTH){
                    onNext();
                }
                if (newPovDirection == GamePadManager.PovDirection.NORTH){
                    onPrev();
                }
                if (newPovDirection == GamePadManager.PovDirection.EAST){
                    onLeft();
                }
                if (newPovDirection == GamePadManager.PovDirection.WEST){
                    onRight();
                }
            }
        };
    }

    private void initializeKeyboardListener() {
        keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                //Logs.MAIN.d("On key dispatch e = %s", e);
                if (e.getID() == KeyEvent.KEY_PRESSED){
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        onApplyDown();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_SPACE){
                        onBackDown();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP){
                        onPrev();;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN){
                        onNext();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT){
                        onLeft();;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                        onRight();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        //TODO: Temporary close on enter
                        shutdownManager.shutdown();
                    }
                }
                if (e.getID() == KeyEvent.KEY_RELEASED){
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        onApplyUp();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_SPACE){
                        onBackUp();
                    }
                }

                //Disallow the event to be re dispatched
                return false;
            }
        };
    }


    public void requestLauncherList() {
        publicModel.requestLauncherListUpdate();
    }

    public void showSystemPanel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemItemsView.open();
            }
        });
    }

    public void reInitSystemPanel(List<SystemItemsView.Item> itemList) {
        systemItemsView.installItems(itemList);
    }

    public void selectNextSystemItem() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemItemsView.selectNext();
            }
        });
    }

    public void selectPrevSystemItem() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemItemsView.selectPrev();
            }
        });
    }

    public void pushSelectedSystemItem() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemItemsView.pressSelected();
            }
        });
    }

    public SystemItemsView.Item getSelectedSystemItem() {
        SystemItemsView.Item selected = systemItemsView.getSelectedItem();
        return selected;
    }

    public SystemItemsView.Item releaseSelectedSystemItem() {
        SystemItemsView.Item selected = systemItemsView.getSelectedItem();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemItemsView.releaseSelected();
            }
        });
        return selected;
    }

    public void showApprovalPanel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemApprovalView.open();
            }
        });
    }

    public void pushSelectedSystemDecition() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemApprovalView.pushSelected();
            }
        });
    }

    public boolean releaseSelectedSystemDecision() {
        boolean answer = systemApprovalView.isYes();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemApprovalView.releaseSelected();
            }
        });
        return answer;
    }

    public void selectOtherSystemDecision() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemApprovalView.changeSelection();
            }
        });
    }

    public void closeApprovalPanel(final Runnable closeDecisionMenuCallback) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemApprovalView.close(closeDecisionMenuCallback);
            }
        });
    }

    public void closeSystemPanel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                systemItemsView.close();
            }
        });
    }

    public void selectNextGameAction() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameMenuView.selectNextGameMenuItem();
            }
        });
    }

    public void openGameView() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                wallpaperView.open();
            }
        });
    }

    public void openGameView(final GameDetails details) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameMenuView.updateDetails(details, null, GameMenuView.Animation.NO_ANIMATION);
                wallpaperView.open();
            }
        });
    }

    public void closeGameView() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                wallpaperView.close();
            }
        });
    }


    public void pushSelectedGameAction() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameMenuView.pushSelectedGameMenuItem();
            }
        });
    }

    public String releaseSelectedGameAction() {
        String answer = gameMenuView.getSelectedMenuItem();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameMenuView.releaseSelectedGameMenuItem();
            }
        });
        return answer;
    }

    public void selectGameAnimateFromTop(final GameDetails gameDetails, final Closure<Void,Void> callback) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameMenuView.updateDetails(gameDetails, callback, GameMenuView.Animation.ANIMATE_FROM_TOP);
            }
        });
    }

    public void selectGameAnimateFromBottom(final GameDetails gameDetails, final Closure<Void, Void> closure) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameMenuView.updateDetails(gameDetails, closure, GameMenuView.Animation.ANIMATE_FROM_BOTTOM);
            }
        });
    }

    public void activateGameMode() {
        activate(gameMenuPresentState);
    }

    public void activateHomeMode() {
        activate(systemActionPresentState);
    }

    public GameDetails getGameInfo(Launcher launcher) {
        return publicModel.getGameInfo(launcher);
    }

    public Image scaleCover(BufferedImage covertImage) {
        if (covertImage ==null) return null;
        return gameMenuView.scaleToFitCovert(covertImage);
    }

    public void launch(Launcher launcher) {
       gamePadManager.stop();
       publicModel.launch(launcher);
    }

    public static abstract class AbstractDefaultUIPresenterState extends AbstractStateUIPresenter.AbstractUIState{

        private final DefaultUIPresenter owner;

        public AbstractDefaultUIPresenterState(String id, DefaultUIPresenter owner) {
            super(id);
            this.owner = owner;
        }

        final public DefaultUIPresenter getOwner() {
            return owner;
        }

        public void onLauncherListUpdate(List<Launcher> launcherList){};

        public void onProcessFail(Launcher launcher){};

        public void onProcessStop(Launcher launcher){};

        public void onProcessStart(Launcher launcher){};

        public void onBackUp(){};

        public void onApplyUp(){};

        public void onBackDown(){};

        public void onApplyDown(){};

        public void onUp(){};

        public void onDown(){};

        public void onLeft(){};
        public void onRight(){};

    }

}
